package com.streamit.application.daos.promotion;

import com.streamit.application.daos.common.AbstractCommonDAO;
import com.streamit.application.daos.content.ContentDAO;
import com.streamit.application.dtos.content.Content;
import com.streamit.application.dtos.content.ContentCreateDTO;
import com.streamit.application.dtos.promotion.*;
import com.streamit.application.exceptions.NotFoundException;
import com.streamit.application.mappers.promotion.PromotionMapper;
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
public class PromotionDAO extends AbstractCommonDAO<Promotion, UUID, PromotionCreateWithContentDTO> {
    private static final Set<String> UPDATABLE_FIELDS = Set.of(
            "titleTh", "titleEn", "descriptionTh", "descriptionEn",
            "coverImagePath", "startDate", "endDate",
            "deletedAt", "updatedAt"
    );

    private final ContentDAO contentDAO;

    public PromotionDAO(DataSource dataSource, ContentDAO contentDAO) {
        super(dataSource, "promotions", "id", new PromotionDAO.PromotionRowMapper());
        this.contentDAO = contentDAO;
    }

    public PromotionResDTO insertPromotion(PromotionCreateDTO promotionCreateDTO, ContentCreateDTO contentCreateDTO) throws SQLException {
        return executeTransaction(
                conn -> {
                    Content content = contentDAO.insert(contentCreateDTO);
                    PromotionCreateWithContentDTO promotionCreateWithContentDTO = PromotionMapper.mapPromotionCreateToPromotionCreateWithContentDTO(promotionCreateDTO, content);
                    Promotion promotion = insert(promotionCreateWithContentDTO);

                    return PromotionMapper.mapPromotionToPromotionResDTO(promotion, content);
                }
        ).get(0);
    }

    public List<PromotionResDTO> findAllPromotionsWithContent() throws SQLException {
        return executeTransaction(
                conn -> {
                    List<Promotion> promotions = super.findAll(List.of("deleted_at IS NULL"), List.of());
                    return promotions.stream()
                            .flatMap(promotion -> {
                                try {
                                    Optional<Content> content = contentDAO.findById(promotion.getContentId(), List.of("deleted_at IS NULL", "status = 'ACTIVE'::status_enum", "? BETWEEN effective_from AND effective_to"), List.of(LocalDateTime.now()));
                                    return content.stream().map(c -> PromotionMapper.mapPromotionToPromotionResDTO(promotion, c));
                                } catch (SQLException ex) {
                                    throw new RuntimeException(ex);
                                }
                            })
                            .toList();
                }
        ).get(0);
    }

    public PromotionResDTO findPromotionWithContentById(UUID id) throws SQLException {
        return executeTransaction(
                conn -> {
                    Promotion promotion = super.findById(id).orElseThrow(() -> new NotFoundException("Promotion was not found."));
                    Content content = contentDAO.findById(promotion.getContentId()).orElseThrow(() -> new NotFoundException("Content was not found."));
                    return PromotionMapper.mapPromotionToPromotionResDTO(promotion, content);
                }
        ).get(0);
    }

    public PromotionResDTO updatePromotion(UUID id, PromotionUpdateDTO promotionUpdateDTO) throws SQLException {
        return executeTransaction(
                conn -> {
                    Promotion updatedPromotion = super.update(id, promotionUpdateDTO.getPromotionUpdateMap(), UPDATABLE_FIELDS);
                    Content updatedContent = contentDAO.update(updatedPromotion.getContentId(), promotionUpdateDTO.getContentUpdateMap());
                    return PromotionMapper.mapPromotionToPromotionResDTO(updatedPromotion, updatedContent);
                }
        ).get(0);
    }

    public boolean deletePromotionById(UUID id, UUID contentId) throws SQLException {
        return executeTransaction(
                conn -> {
                    boolean promotionDeleted = super.delete(id);
                    boolean contentDeleted = contentDAO.delete(contentId);
                    return promotionDeleted && contentDeleted;
                }
        ).get(0);
    }

    @Override
    protected Map<String, Object> getCreateFieldMap(PromotionCreateWithContentDTO createDto) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("titleTh", createDto.getTitleTh());
        fieldMap.put("titleEn", createDto.getTitleEn());
        fieldMap.put("descriptionTh", createDto.getDescriptionTh());
        fieldMap.put("descriptionEn", createDto.getDescriptionEn());
        fieldMap.put("coverImagePath", createDto.getCoverImagePath());
        fieldMap.put("startDate", createDto.getStartDate());
        fieldMap.put("endDate", createDto.getEndDate());
        fieldMap.put("contentId", createDto.getContentId());
        return fieldMap;
    }

    @Override
    protected boolean isInsertableField(String fieldName) {
        return !fieldName.equals("createdAt") && !fieldName.equals("updatedAt")
                && !fieldName.equals("deletedAt")
                && !fieldName.equals("id");
    }

    public static class PromotionRowMapper implements RowMapper<Promotion> {
        @Override
        public Promotion mapRow(ResultSet rs, int rowNum) throws SQLException {
            Promotion promotion = new Promotion();
            promotion.setId(rs.getObject("id", UUID.class));
            promotion.setTitleTh(rs.getString("title_th"));
            promotion.setTitleEn(rs.getString("title_en"));
            promotion.setDescriptionTh(rs.getString("description_th"));
            promotion.setDescriptionEn(rs.getString("description_en"));
            promotion.setCoverImagePath(rs.getString("cover_image_path"));
            promotion.setStartDate(rs.getObject("start_date", LocalDateTime.class));
            promotion.setEndDate(rs.getObject("end_date", LocalDateTime.class));
            promotion.setContentId(rs.getObject("content_id", UUID.class));
            promotion.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
            promotion.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
            promotion.setDeletedAt(rs.getObject("deleted_at", LocalDateTime.class));
            return promotion;
        }
    }
}