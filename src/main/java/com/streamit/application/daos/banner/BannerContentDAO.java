package com.streamit.application.daos.banner;

import com.streamit.application.daos.common.AbstractCommonDAO;
import com.streamit.application.dtos.banner.BannerContent;
import com.streamit.application.dtos.banner.BannerContentCreateWithBannerDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class BannerContentDAO extends AbstractCommonDAO<BannerContent, UUID, BannerContentCreateWithBannerDTO> {
    public BannerContentDAO(DataSource dataSource) {
        super(dataSource, "banner_contents", "id", new BannerContentDAO.BannerContentMapper());
    }

    @Override
    public List<BannerContent> findAll(UUID bannerId) throws SQLException {
        String sql = """
                SELECT * FROM %s WHERE banner_id = ?
                """.formatted(tableName);
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, bannerId);
            try (ResultSet rs = ps.executeQuery()) {
                List<BannerContent> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(rowMapper.mapRow(rs, 1));
                }
                return results;
            }
        }
    }

    public List<BannerContent> findByBannerId(UUID bannerId) throws SQLException {
        String sql = """
                SELECT * FROM %s WHERE banner_id = ?
                """.formatted(tableName);
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, bannerId);
            try (ResultSet rs = ps.executeQuery()) {
                List<BannerContent> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(rowMapper.mapRow(rs, 1));
                }
                return results;
            }
        }
    }

    @Override
    protected Map<String, Object> getCreateFieldMap(BannerContentCreateWithBannerDTO createDto) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("bannerId", createDto.getBannerId());
        fieldMap.put("contentImagePath", createDto.getContentImagePath());
        fieldMap.put("contentHyperLink", createDto.getContentHyperLink());
        return fieldMap;
    }

    @Override
    protected boolean isInsertableField(String fieldName) {
        return !fieldName.equals("id");
    }

    private static class BannerContentMapper implements RowMapper<BannerContent> {
        @Override
        public BannerContent mapRow(ResultSet rs, int rowNum) throws SQLException {
            BannerContent dto = new BannerContent();
            dto.setId(rs.getObject("id", UUID.class));
            dto.setContentImagePath(rs.getString("content_image_path"));
            dto.setContentHyperLink(rs.getString("content_hyper_link"));
            dto.setBannerId(rs.getObject("banner_id", UUID.class));
            return dto;
        }
    }
}
