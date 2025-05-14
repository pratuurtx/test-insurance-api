package com.streamit.application.daos.banner;

import com.streamit.application.daos.common.AbstractCommonDAO;
import com.streamit.application.daos.content.ContentDAO;
import com.streamit.application.dtos.banner.*;
import com.streamit.application.dtos.content.Content;
import com.streamit.application.dtos.content.ContentCreateDTO;
import com.streamit.application.exceptions.BadRequestException;
import com.streamit.application.exceptions.NotFoundException;
import com.streamit.application.mappers.banner.BannerMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Repository
public class BannerDAO extends AbstractCommonDAO<Banner, UUID, BannerCreateWithContentDTO> {
    private static final Set<String> UPDATABLE_FIELDS = Set.of(
            "coverImagePath", "coverHyperlink"
    );

    private static final Set<String> UPDATABLE_CONTENT_FIELDS = Set.of(
            "contentImagePath", "contentHyperLink"
    );

    private final BannerContentDAO bannerContentDAO;
    private final ContentDAO contentDAO;

    public BannerDAO(DataSource dataSource, BannerContentDAO bannerContentDAO, ContentDAO contentDAO) {
        super(dataSource, "banners", "id", new BannerDAO.BannerRowMapper());
        this.bannerContentDAO = bannerContentDAO;
        this.contentDAO = contentDAO;
    }

    public BannerResDTO insertBanner(BannerCreateDTO bannerCreateDTO, ContentCreateDTO contentCreateReqDTO, List<BannerContentCreateDTO> bannerContentCreateDTOs) throws SQLException {
        return executeTransaction(
                conn -> {
                    Content content = contentDAO.insert(contentCreateReqDTO);
                    BannerCreateWithContentDTO bannerCreateWithContentDTO = BannerMapper.mapBannerCreateDTOToBannerCreateWithContent(content.getId(), bannerCreateDTO);
                    Banner banner = super.insert(bannerCreateWithContentDTO);

                    List<BannerContentCreateWithBannerDTO> bannerContentCreateWithBannerDTOs = BannerMapper.mapBannerContentCreateDTOListToBannerCreateWithContentDTOList(banner.getId(), bannerContentCreateDTOs);
                    List<BannerContent> bannerContents = bannerContentDAO.insert(bannerContentCreateWithBannerDTOs);
                    return BannerMapper.mapBannerToBannerResDTO(banner, bannerContents, content);
                }
        ).get(0);
    }

    public List<BannerResDTO> findAllBannerWithContents() throws SQLException {
        return executeTransaction(
                conn -> {
                    List<Banner> banners = super.findAll();
                    List<BannerResDTO> bannerResDTOs = new ArrayList<>();
                    for (Banner banner : banners) {
                        Optional<Content> content = contentDAO.findById(banner.getContentId(), List.of("deleted_at IS NULL", "status = 'ACTIVE'::status_enum", "? BETWEEN effective_from AND effective_to"), List.of(LocalDateTime.now()));
                        content.ifPresent(c -> {
                            try {
                                List<BannerContent> bannerContents = bannerContentDAO.findAll(banner.getId());
                                bannerResDTOs.add(BannerMapper.mapBannerToBannerResDTO(banner, bannerContents, c));
                            } catch (SQLException ex) {
                                log.error(ex.getMessage());
                            }
                        });
                    }
                    return bannerResDTOs;
                }
        ).get(0);
    }

    public BannerResDTO findBannerWithContentsById(UUID id) throws SQLException {
        return executeTransaction(
                conn -> {
                    Banner banner = super.findById(id).orElseThrow(() -> new SQLException("Banner was not found."));
                    Content content = contentDAO.findById(banner.getContentId()).orElseThrow(() -> new NotFoundException("Content was not found."));
                    List<BannerContent> bannerContents = bannerContentDAO.findAll(banner.getId());
                    return BannerMapper.mapBannerToBannerResDTO(banner, bannerContents, content);
                }
        ).get(0);
    }

    public BannerContentResDTO findBannerContentByBannerContentId(UUID bannerContentId) throws SQLException {
        return executeTransaction(
                conn -> {
                    BannerContent bannerContent = bannerContentDAO.findById(bannerContentId).orElseThrow(() -> new SQLException("Banner content was not found."));
                    return BannerMapper.mapBannerContentToBannerContentResDTO(bannerContent);
                }
        ).get(0);
    }

    public BannerResDTO updateBannerWithContentById(UUID id, BannerUpdateDTO bannerUpdateDTO) throws SQLException {
        return executeTransaction(
                conn -> {
                    // remove exist id
                    bannerContentDAO.delete(bannerUpdateDTO.getBannerContentRemoves());
                    // find exists banner content
                    List<BannerContent> existsNannerContents = bannerContentDAO.findAll(id);
                    if (existsNannerContents.size() + bannerUpdateDTO.getBannerContentCreateDTOs().size() > 10) {
                        throw new BadRequestException("Exceeds banner content limit of 10.");
                    }
                    // add new banner content
                    List<BannerContentCreateWithBannerDTO> bannerContentCreateWithBannerDTOs = BannerMapper.mapBannerContentCreateDTOListToBannerCreateWithContentDTOList(id, bannerUpdateDTO.getBannerContentCreateDTOs());
                    bannerContentDAO.insert(bannerContentCreateWithBannerDTOs);

                    // update exist banner
                    Banner updatedBanner = super.update(id, bannerUpdateDTO.getBannerUpdateMap(), UPDATABLE_FIELDS);

                    // update exist content
                    Content updatedContent = contentDAO.update(updatedBanner.getContentId(), bannerUpdateDTO.getContentUpdateMap());

                    // update exist banner content
                    for (BannerContentUpdateDTO bannerContentUpdate : bannerUpdateDTO.getBannerContentUpdateDTOs()) {
                        bannerContentDAO.update(bannerContentUpdate.getId(), bannerContentUpdate.getContentFieldMap(), UPDATABLE_CONTENT_FIELDS);
                    }

                    List<BannerContent> bannerContents = bannerContentDAO.findByBannerId(id);
                    return BannerMapper.mapBannerToBannerResDTO(
                            updatedBanner,
                            bannerContents,
                            updatedContent
                    );
                }
        ).get(0);
    }

    public boolean deleteBanner(UUID id, UUID contentId) throws SQLException {
        return executeTransaction(
                conn -> {
                    boolean bannerDeleted = super.delete(id);
                    boolean contentDeleted = contentDAO.delete(contentId);
                    return bannerDeleted && contentDeleted;
                }
        ).get(0);
    }

    @Override
    protected Map<String, Object> getCreateFieldMap(BannerCreateWithContentDTO createDto) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("coverImagePath", createDto.getCoverImagePath());
        fieldMap.put("coverHyperLink", createDto.getCoverHyperLink());
        fieldMap.put("contentId", createDto.getContentId());
        return fieldMap;
    }

    @Override
    protected boolean isInsertableField(String fieldName) {
        return !fieldName.equals("id");
    }

    private static class BannerRowMapper implements RowMapper<Banner> {
        @Override
        public Banner mapRow(ResultSet rs, int rowNum) throws SQLException {
            Banner dto = new Banner();
            dto.setId(rs.getObject("id", UUID.class));
            dto.setCoverImagePath(rs.getString("cover_image_path"));
            dto.setCoverHyperLink(rs.getString("cover_hyper_link"));
            dto.setContentId(rs.getObject("content_id", UUID.class));
            return dto;
        }
    }
}
