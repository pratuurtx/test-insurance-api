package com.streamit.application.daos.suit_insurance;

import com.streamit.application.daos.common.AbstractCommonDAO;
import com.streamit.application.daos.content.ContentDAO;
import com.streamit.application.dtos.content.Content;
import com.streamit.application.dtos.content.ContentCreateDTO;
import com.streamit.application.dtos.suit_insurance.*;
import com.streamit.application.exceptions.NotFoundException;
import com.streamit.application.mappers.suit_insurance.SuitInsuranceMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class SuitInsuranceDAO extends AbstractCommonDAO<SuitInsurance, UUID, SuitInsuranceCreateWithContentDTO> {
    private final ContentDAO contentDAO;
    private static final Set<String> UPDATABLE_FIELDS = Set.of(
            "titleTh", "titleEn", "imagePath"
    );

    public SuitInsuranceDAO(DataSource dataSource, ContentDAO contentDAO) {
        super(dataSource, "suit_insurances", "id", new SuitInsuranceDAO.SuitInsuranceRowMapper());
        this.contentDAO = contentDAO;
    }

    public SuitInsuranceResDTO insertSuitInsurance(SuitInsuranceCreateDTO suitInsuranceCreateDTO, ContentCreateDTO contentCreateDTO) throws SQLException {
        return executeTransaction(
                conn -> {
                    Content content = contentDAO.insert(contentCreateDTO);
                    SuitInsuranceCreateWithContentDTO suitInsuranceCreateWithContentDTO = SuitInsuranceMapper.mapSuitInsuranceCreateToSuitInsuranceCreateWithDTO(suitInsuranceCreateDTO, content);
                    SuitInsurance suitInsurance = super.insert(suitInsuranceCreateWithContentDTO);
                    return SuitInsuranceMapper.mapSuitInsuranceToSuitInsuranceResDTO(suitInsurance, content);
                }
        ).get(0);
    }

    public List<SuitInsuranceResDTO> findAllSuitInsurancesWithContent() throws SQLException {
        return executeTransaction(
                conn -> {
                    List<SuitInsurance> suitInsurances = super.findAll();
                    return suitInsurances.stream()
                            .flatMap(suitInsurance -> {
                                try {
                                    Optional<Content> content = contentDAO.findById(suitInsurance.getContentId(), List.of("deleted_at IS NULL", "status = 'ACTIVE'::status_enum", "? BETWEEN effective_from AND effective_to"), List.of(LocalDateTime.now()));
                                    return content.stream().map(c -> SuitInsuranceMapper.mapSuitInsuranceToSuitInsuranceResDTO(suitInsurance, c));
                                } catch (SQLException ex) {
                                    throw new RuntimeException(ex);
                                }
                            })
                            .toList();
                }
        ).get(0);
    }

    public SuitInsuranceResDTO findSuitInsuranceWithContentById(UUID id) throws SQLException {
        return executeTransaction(
                conn -> {
                    SuitInsurance suitInsurance = super.findById(id).orElseThrow(() -> new NotFoundException("Suit insurance was not found."));
                    Content content = contentDAO.findById(suitInsurance.getContentId()).orElseThrow(() -> new NotFoundException("Content was not found."));
                    return SuitInsuranceMapper.mapSuitInsuranceToSuitInsuranceResDTO(suitInsurance, content);
                }
        ).get(0);
    }

    public SuitInsuranceResDTO updateSuitInsuranceById(UUID id, SuitInsuranceUpdateDTO suitInsuranceUpdateDTO) throws SQLException {
        return executeTransaction(
                conn -> {
                    SuitInsurance updatedSuitInsurance = update(id, suitInsuranceUpdateDTO.getSuitInsuranceUpdateMap());
                    Content updatedContent = contentDAO.update(updatedSuitInsurance.getContentId(), suitInsuranceUpdateDTO.getContentUpdateMap());
                    return SuitInsuranceMapper.mapSuitInsuranceToSuitInsuranceResDTO(updatedSuitInsurance, updatedContent);
                }
        ).get(0);
    }

    public boolean deleteSuitInsuranceById(UUID id, UUID contentId) throws SQLException {
        return executeTransaction(
                conn -> {
                    boolean deletedSuitInsurance = super.delete(id);
                    boolean deletedContent = contentDAO.delete(contentId);
                    return deletedSuitInsurance && deletedContent;
                }
        ).get(0);
    }

    @Override
    protected Map<String, Object> getCreateFieldMap(SuitInsuranceCreateWithContentDTO createDto) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("titleTh", createDto.getTitleTh());
        fieldMap.put("titleEn", createDto.getTitleEn());
        fieldMap.put("imagePath", createDto.getImagePath());
        fieldMap.put("contentId", createDto.getContentId());
        return fieldMap;
    }

    @Override
    protected boolean isInsertableField(String fieldName) {
        return !fieldName.equals("id");
    }

    public SuitInsurance update(UUID id, Map<String, Object> fieldUpdates) throws SQLException {
        return super.update(id, fieldUpdates, UPDATABLE_FIELDS);
    }

    private static class SuitInsuranceRowMapper implements RowMapper<SuitInsurance> {
        @Override
        public SuitInsurance mapRow(ResultSet rs, int rowNum) throws SQLException {
            SuitInsurance dto = new SuitInsurance();
            dto.setId(rs.getObject("id", UUID.class));
            dto.setTitleTh(rs.getString("title_th"));
            dto.setTitleEn(rs.getString("title_en"));
            dto.setImagePath(rs.getString("image_path"));
            dto.setContentId(rs.getObject("content_id", UUID.class));
            return dto;
        }
    }

}
