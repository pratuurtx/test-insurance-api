package com.streamit.application.daos.insurance;

import com.streamit.application.daos.common.AbstractCommonDAO;
import com.streamit.application.daos.content.ContentDAO;
import com.streamit.application.dtos.content.Content;
import com.streamit.application.dtos.content.ContentCreateDTO;
import com.streamit.application.dtos.insurance.*;
import com.streamit.application.exceptions.NotFoundException;
import com.streamit.application.mappers.insurance.InsuranceMapper;
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
public class InsuranceDAO extends AbstractCommonDAO<Insurance, UUID, InsuranceCreateWithContentDTO> {
    private static final Set<String> UPDATEABLE_FIELDS = Set.of(
            "titleTh", "titleEn", "descriptionTh", "descriptionEn",
            "coverImagePath", "iconImagePath"
    );

    private final ContentDAO contentDAO;

    public InsuranceDAO(DataSource dataSource, ContentDAO contentDAO) {
        super(dataSource, "insurances", "id", new InsuranceDAO.InsuranceRowMapper());
        this.contentDAO = contentDAO;
    }

    public InsuranceResDTO insertInsurance(InsuranceCreateDTO insuranceCreateDTO, ContentCreateDTO contentCreateDTO) throws SQLException {
        return executeTransaction(
                conn -> {
                    Content content = contentDAO.insert(contentCreateDTO);
                    InsuranceCreateWithContentDTO insuranceCreateWithContentDTO = InsuranceMapper.mapInsuranceCreateToInsuranceCreateWithContentDTO(insuranceCreateDTO, content);
                    Insurance insurance = insert(insuranceCreateWithContentDTO);

                    return InsuranceMapper.mapInsuranceToInsuranceResDTO(insurance, content);
                }
        ).get(0);
    }

    public List<InsuranceResDTO> findAllInsurancesWithContent() throws SQLException {
        return executeTransaction(
                conn -> {
                    List<Insurance> insurances = findAll();
                    return insurances.stream()
                            .map(insurance -> {
                                try {
                                    Content content = contentDAO.findById(insurance.getContentId()).orElseThrow(() -> new NotFoundException("content with id#" + insurance.getContentId().toString() + " not found."));
                                    return InsuranceMapper.mapInsuranceToInsuranceResDTO(insurance, content);
                                } catch (SQLException ex) {
                                    throw new RuntimeException(ex);
                                }
                            })
                            .toList();
                }
        ).get(0);
    }

    public InsuranceResDTO findInsuranceWithContentById(UUID id) throws SQLException {
        return executeTransaction(
                conn -> {
                    Insurance insurance = findById(id).orElseThrow(() -> new NotFoundException("Insurance was not found."));
                    Content content = contentDAO.findById(insurance.getContentId()).orElseThrow(() -> new NotFoundException("Content was not found."));
                    return InsuranceMapper.mapInsuranceToInsuranceResDTO(insurance, content);
                }
        ).get(0);
    }

    public InsuranceResDTO updateInsurance(UUID id, InsuranceUpdateDTO insuranceUpdateDTO) throws SQLException {
        return executeTransaction(
                conn -> {
                    Insurance updatedInsurance = update(id, insuranceUpdateDTO.getInsuranceUpdateMap(), UPDATEABLE_FIELDS);
                    Content updatedContent = contentDAO.update(updatedInsurance.getContentId(), insuranceUpdateDTO.getContentUpdateMap());
                    return InsuranceMapper.mapInsuranceToInsuranceResDTO(updatedInsurance, updatedContent);
                }
        ).get(0);
    }

    public boolean deleteInsuranceById(UUID id, UUID contentId) throws SQLException {
        return executeTransaction(
                conn -> {
                    boolean insuranceDeleted = super.delete(id);
                    boolean contentDeleted = contentDAO.delete(contentId);
                    return insuranceDeleted && contentDeleted;
                }
        ).get(0);
    }


    @Override
    protected Map<String, Object> getCreateFieldMap(InsuranceCreateWithContentDTO createDto) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("titleTh", createDto.getTitleTh());
        fieldMap.put("titleEn", createDto.getTitleEn());
        fieldMap.put("descriptionTh", createDto.getDescriptionTh());
        fieldMap.put("descriptionEn", createDto.getDescriptionEn());
        fieldMap.put("coverImagePath", createDto.getCoverImagePath());
        fieldMap.put("iconImagePath", createDto.getIconImagePath());
        fieldMap.put("contentId", createDto.getContentId());
        return fieldMap;
    }

    @Override
    protected Map<String, Object> getFieldMap(Insurance entity) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("id", entity.getId());
        fieldMap.put("titleTh", entity.getTitleTh());
        fieldMap.put("titleEn", entity.getTitleEn());
        fieldMap.put("descriptionTh", entity.getDescriptionTh());
        fieldMap.put("descriptionEn", entity.getDescriptionEn());
        fieldMap.put("coverImagePath", entity.getCoverImagePath());
        fieldMap.put("iconImagePath", entity.getIconImagePath());
        fieldMap.put("contentId", entity.getContentId());
        fieldMap.put("createdAt", entity.getCreatedAt());
        fieldMap.put("updatedAt", entity.getUpdatedAt());
        fieldMap.put("deletedAt", entity.getDeletedAt());
        return fieldMap;
    }

    @Override
    protected UUID getIdValue(Insurance entity) {
        return entity.getId();
    }

    @Override
    protected boolean isInsertableField(String fieldName) {
        return !fieldName.equals("createdAt") && !fieldName.equals("updatedAt")
                && !fieldName.equals("deletedAt")
                && !fieldName.equals("id");
    }

    public static class InsuranceRowMapper implements RowMapper<Insurance> {
        @Override
        public Insurance mapRow(ResultSet rs, int rowNum) throws SQLException {
            Insurance insurance = new Insurance();
            insurance.setId(rs.getObject("id", UUID.class));
            insurance.setTitleTh(rs.getString("title_th"));
            insurance.setTitleEn(rs.getString("title_en"));
            insurance.setDescriptionTh(rs.getString("description_th"));
            insurance.setDescriptionEn(rs.getString("description_en"));
            insurance.setCoverImagePath(rs.getString("cover_image_path"));
            insurance.setIconImagePath(rs.getString("icon_image_path"));
            insurance.setContentId(rs.getObject("content_id", UUID.class));
            insurance.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
            insurance.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
            insurance.setDeletedAt(rs.getObject("deleted_at", LocalDateTime.class));
            return insurance;
        }
    }
}