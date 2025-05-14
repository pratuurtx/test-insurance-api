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
import java.util.*;

@Repository
public class SuitInsuranceDAO extends AbstractCommonDAO<SuitInsurance, UUID, SuitInsuranceCreateWithContentDTO> {
    private final ContentDAO contentDAO;
    private static final Set<String> UPDATEABLE_FIELDS = Set.of(
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
                            .map(suitInsurance -> {
                                try {
                                    Content content = contentDAO.findById(suitInsurance.getContentId()).orElseThrow(() -> new NotFoundException("Content with ID#" + suitInsurance.getId().toString() + " Not Found."));
                                    return SuitInsuranceMapper.mapSuitInsuranceToSuitInsuranceResDTO(suitInsurance, content);
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
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

    public boolean deleteSuitInsuranceById(UUID id) throws SQLException {
        return executeTransaction(
                conn -> {
                    boolean deletedSuitInsurance = super.delete(id);
                    boolean deletedContent = contentDAO.delete(id);
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
    protected Map<String, Object> getFieldMap(SuitInsurance entity) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("id", entity.getId());
        fieldMap.put("titleTh", entity.getTitleTh());
        fieldMap.put("titleEn", entity.getTitleEn());
        fieldMap.put("imagePath", entity.getImagePath());
        fieldMap.put("contentId", entity.getContentId());
        return fieldMap;
    }

    @Override
    protected UUID getIdValue(SuitInsurance entity) {
        return entity.getId();
    }

    @Override
    protected boolean isInsertableField(String fieldName) {
        return !fieldName.equals("id");
    }

    public SuitInsurance update(UUID id, Map<String, Object> fieldUpdates) throws SQLException {
        return super.update(id, fieldUpdates, UPDATEABLE_FIELDS);
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
