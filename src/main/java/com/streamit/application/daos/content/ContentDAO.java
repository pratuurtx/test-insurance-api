package com.streamit.application.daos.content;

import com.streamit.application.daos.common.AbstractCommonDAO;
import com.streamit.application.dtos.common.StatusEnum;
import com.streamit.application.dtos.common.TypeEnum;
import com.streamit.application.dtos.content.Content;
import com.streamit.application.dtos.content.ContentCreateDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Repository
public class ContentDAO extends AbstractCommonDAO<Content, UUID, ContentCreateDTO> {
    private static final Set<String> UPDATEABLE_FIELDS = Set.of(
            "title", "effectiveFrom", "effectiveTo", "status"
    );

    public ContentDAO(DataSource dataSource) {
        super(dataSource, "contents", "id", new ContentDAO.ContentRowMapper());
    }

    public Content update(UUID id, Map<String, Object> fieldUpdates) throws SQLException {
        return super.update(id, fieldUpdates, UPDATEABLE_FIELDS);
    }

    @Override
    protected Map<String, Object> getCreateFieldMap(ContentCreateDTO createDto) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("title", createDto.getTitle());
        fieldMap.put("status", createDto.getStatus());
        fieldMap.put("type", createDto.getType());
        fieldMap.put("effectiveFrom", createDto.getEffectiveFrom());
        fieldMap.put("effectiveTo", createDto.getEffectiveTo());
        return fieldMap;
    }

    @Override
    protected Map<String, Object> getFieldMap(Content entity) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("id", entity.getId());
        fieldMap.put("title", entity.getTitle());
        fieldMap.put("status", entity.getStatus());
        fieldMap.put("type", entity.getType());
        fieldMap.put("effectiveFrom", entity.getEffectiveFrom());
        fieldMap.put("effectiveTo", entity.getEffectiveTo());
        return fieldMap;
    }

    @Override
    protected UUID getIdValue(Content entity) {
        return entity.getId();
    }

    @Override
    protected boolean isInsertableField(String fieldName) {
        return !fieldName.equals("id");
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
            String typeStr = rs.getString("type");
            if (typeStr != null) {
                dto.setType(TypeEnum.fromValue(typeStr));
            }
            return dto;
        }
    }
}
