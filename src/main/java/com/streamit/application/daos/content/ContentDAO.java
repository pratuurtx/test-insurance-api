package com.streamit.application.daos.content;

import com.streamit.application.daos.common.AbstractCommonDAO;
import com.streamit.application.dtos.common.ItemQueryParams;
import com.streamit.application.dtos.common.StatusEnum;
import com.streamit.application.dtos.common.CategoryEnum;
import com.streamit.application.dtos.content.Content;
import com.streamit.application.dtos.content.ContentCreateDTO;
import com.streamit.application.dtos.content.ContentResDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Repository
public class ContentDAO extends AbstractCommonDAO<Content, UUID, ContentCreateDTO> {
    private static final Set<String> UPDATABLE_FIELDS = Set.of(
            "title", "effectiveFrom", "effectiveTo",
            "status", "deletedAt", "updatedAt"
    );

    public ContentDAO(DataSource dataSource) {
        super(dataSource, "contents", "id", new ContentDAO.ContentRowMapper());
    }

    public List<ContentResDTO> findAllContentsWithItemQueryParams(ItemQueryParams itemQueryParams) throws SQLException {
        StringBuilder sql = new StringBuilder("""
                SELECT id, title, status, category, category_content_id
                FROM (
                    SELECT
                        c.id,
                        c.title,
                        c.status,
                        c.category,
                        c.updated_at,
                        CASE
                            WHEN c.category = 'BANNER'::category_enum THEN b.id::uuid
                            WHEN c.category = 'INSURANCE'::category_enum THEN i.id::uuid
                            WHEN c.category = 'SUIT_INSURANCE'::category_enum THEN si.id::uuid
                            WHEN c.category = 'PROMOTION'::category_enum THEN p.id::uuid
                        END AS category_content_id
                    FROM contents c
                    LEFT JOIN banners b ON c.id = b.content_id AND c.category = 'BANNER'::category_enum
                    LEFT JOIN insurances i ON c.id = i.content_id AND c.category = 'INSURANCE'::category_enum
                    LEFT JOIN suit_insurances si ON c.id = si.content_id AND c.category = 'SUIT_INSURANCE'::category_enum
                    LEFT JOIN promotions p ON c.id = p.content_id AND c.category = 'PROMOTION'::category_enum
                    WHERE 1=1
                """);

        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        if (itemQueryParams != null) {
            if (itemQueryParams.getCategory() != null && !itemQueryParams.getCategory().isEmpty()) {
                conditions.add("category = ?::category_enum");
                params.add(itemQueryParams.getCategory());
            }

            if (itemQueryParams.getStatus() != null && !itemQueryParams.getStatus().isEmpty()) {
                conditions.add("status = ?::status_enum");
                params.add(itemQueryParams.getStatus());
            }

            for (String condition : conditions) {
                sql.append(" AND ").append(condition);
            }
        }

        sql.append("""
                ) sub
                WHERE sub.category_content_id IS NOT NULL
                ORDER BY sub.updated_at DESC
                LIMIT ? OFFSET ?
                """);

        assert itemQueryParams != null;
        params.add(itemQueryParams.getPageSize());
        params.add(itemQueryParams.getPage() * itemQueryParams.getPageSize());

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString())
        ) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                List<ContentResDTO> results = new ArrayList<>();
                int rowNum = 0;
                while (rs.next()) {
                    results.add(mapContentResDTO(rs, rowNum++));
                }
                return results;
            }
        }
    }

    public int getTotalCount(ItemQueryParams itemQueryParams) throws SQLException {
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(*) FROM (
                    SELECT c.id,
                        CASE
                            WHEN c.category = 'BANNER'::category_enum THEN b.id::uuid
                            WHEN c.category = 'INSURANCE'::category_enum THEN i.id::uuid
                            WHEN c.category = 'SUIT_INSURANCE'::category_enum THEN si.id::uuid
                            WHEN c.category = 'PROMOTION'::category_enum THEN p.id::uuid
                        END AS category_content_id
                    FROM contents c
                    LEFT JOIN banners b ON c.id = b.content_id AND c.category = 'BANNER'::category_enum
                    LEFT JOIN insurances i ON c.id = i.content_id AND c.category = 'INSURANCE'::category_enum
                    LEFT JOIN suit_insurances si ON c.id = si.content_id AND c.category = 'SUIT_INSURANCE'::category_enum
                    LEFT JOIN promotions p ON c.id = p.content_id AND c.category = 'PROMOTION'::category_enum
                    WHERE 1=1
                """);

        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        if (itemQueryParams != null) {
            if (itemQueryParams.getStatus() != null && !itemQueryParams.getStatus().isEmpty()) {
                conditions.add("c.status = ?::status_enum");
                params.add(itemQueryParams.getStatus());
            }

            if (itemQueryParams.getCategory() != null && !itemQueryParams.getCategory().isEmpty()) {
                conditions.add("c.category = ?::category_enum");
                params.add(itemQueryParams.getCategory());
            }

            for (String condition : conditions) {
                sql.append(" AND ").append(condition);
            }

        }
        sql.append(") sub WHERE sub.category_content_id IS NOT NULL");
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }

    private ContentResDTO mapContentResDTO(ResultSet rs, int rowNum) throws SQLException {
        ContentResDTO dto = new ContentResDTO();
        dto.setId(UUID.fromString(rs.getString("id")));
        dto.setTitle(rs.getString("title"));
        dto.setStatus(StatusEnum.valueOf(rs.getString("status")));
        dto.setCategory(CategoryEnum.valueOf(rs.getString("category")));

        String categoryContentId = rs.getString("category_content_id");
        if (categoryContentId != null) {
            dto.setCategoryContentId(UUID.fromString(categoryContentId));
        }

        return dto;
    }

    public Content update(UUID id, Map<String, Object> fieldUpdates) throws SQLException {
        return super.update(id, fieldUpdates, UPDATABLE_FIELDS);
    }

    @Override
    protected Map<String, Object> getCreateFieldMap(ContentCreateDTO createDto) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("title", createDto.getTitle());
        fieldMap.put("status", createDto.getStatus());
        fieldMap.put("category", createDto.getCategory());
        fieldMap.put("effectiveFrom", createDto.getEffectiveFrom());
        fieldMap.put("effectiveTo", createDto.getEffectiveTo());
        return fieldMap;
    }

    @Override
    protected boolean isInsertableField(String fieldName) {
        return !fieldName.equals("id") &&
                !fieldName.equals("createdAt") &&
                !fieldName.equals("updatedAt") &&
                !fieldName.equals("deletedAt");
    }

    private static class ContentRowMapper implements RowMapper<Content> {
        @Override
        public Content mapRow(ResultSet rs, int rowNum) throws SQLException {
            Content dto = new Content();
            dto.setId(rs.getObject("id", UUID.class));
            dto.setTitle(rs.getString("title"));
            dto.setEffectiveFrom(rs.getObject("effective_from", LocalDateTime.class));
            dto.setEffectiveTo(rs.getObject("effective_to", LocalDateTime.class));
            String statusStr = rs.getString("status");
            if (statusStr != null) {
                dto.setStatus(StatusEnum.fromValue(statusStr));
            }
            String typeStr = rs.getString("category");
            if (typeStr != null) {
                dto.setCategory(CategoryEnum.fromValue(typeStr));
            }
            dto.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
            dto.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
            dto.setDeletedAt(rs.getObject("deleted_at", LocalDateTime.class));
            return dto;
        }
    }
}
