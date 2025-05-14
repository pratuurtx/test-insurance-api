package com.streamit.application.daos.common;

import com.streamit.application.dtos.common.StatusEnum;
import com.streamit.application.dtos.common.CategoryEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractCommonDAO<T, ID, C> implements CommonDAO<T, ID, C> {
    protected final DataSource dataSource;
    protected final RowMapper<T> rowMapper;
    protected final String tableName;
    protected final String idColumnName;

    public AbstractCommonDAO(DataSource dataSource, String tableName, String idColumnName, RowMapper<T> rowMapper) {
        this.dataSource = dataSource;
        this.rowMapper = rowMapper;
        this.tableName = tableName;
        this.idColumnName = idColumnName;
    }

    @FunctionalInterface
    public interface TransactionOperation<R> {
        R execute(Connection conn) throws SQLException;
    }

    @SafeVarargs
    protected final <R> List<R> executeTransaction(TransactionOperation<R>... operations) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                List<R> results = new ArrayList<>();
                for (TransactionOperation<R> operation : operations) {
                    results.add(operation.execute(conn));
                }
                conn.commit();
                return results;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }

    @Override
    public T insert(C createDto) throws SQLException {
        return insert(List.of(createDto)).get(0);
    }

    @Override
    public List<T> insert(List<C> createDtos) throws SQLException {
        if (createDtos.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Object> firstFieldMap = getCreateFieldMap(createDtos.get(0));
        List<String> insertFields = firstFieldMap.keySet().stream()
                .filter(this::isInsertableField)
                .toList();

        String columns = insertFields.stream()
                .map(this::camelToSnake)
                .collect(Collectors.joining(", "));

        String values = createDtos.stream()
                .map(dto -> "(" + insertFields.stream()
                        .map(k -> "?")
                        .collect(Collectors.joining(", ")) + ")")
                .collect(Collectors.joining(", "));

        String sql = """
                INSERT INTO %s (%s)
                VALUES %s
                RETURNING *
                """.formatted(tableName, columns, values);

        log.info("SQL: {}", sql);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int idx = 1;
            for (C dto : createDtos) {
                Map<String, Object> fieldMap = getCreateFieldMap(dto);
                for (String field : insertFields) {
                    Object value = fieldMap.get(field);
                    if (value instanceof Enum<?>) {
                        ps.setObject(idx++, ((Enum<?>) value).name(), Types.OTHER);
                    } else {
                        ps.setObject(idx++, value);
                    }
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                List<T> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(rowMapper.mapRow(rs, results.size() + 1));
                }
                return results;
            }
        }
    }

    @Override
    public Optional<T> findById(ID id) throws SQLException {
        String sql = """
                SELECT * FROM %s WHERE %s = ?
                """.formatted(tableName, idColumnName);

        log.info("SQL: {}", sql);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(rowMapper.mapRow(rs, 1));
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public Optional<T> findById(ID id, List<String> conditions, List<Object> params) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder("""
                SELECT * FROM %s
                WHERE %s = ?
                """.formatted(tableName, idColumnName));
        for (String condition : conditions) {
            sqlBuilder.append(" AND ").append(condition);
        }

        log.info("SQL: {}", sqlBuilder);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlBuilder.toString())) {
            int index = 1;
            ps.setObject(index++, id);
            for (Object param : params) {
                ps.setObject(index++, param);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(rowMapper.mapRow(rs, 1));
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public List<T> findAll() throws SQLException {
        String sql = """
                SELECT * FROM %s
                """.formatted(tableName);

        log.info("SQL: {}", sql);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<T> results = new ArrayList<>();
            int rowNum = 0;
            while (rs.next()) {
                results.add(rowMapper.mapRow(rs, rowNum++));
            }
            return results;
        }
    }

    @Override
    public List<T> findAll(UUID id) throws SQLException {
        String sql = """
                SELECT * FROM %s
                WHERE %s = ?
                """.formatted(tableName, idColumnName);

        log.info("SQL: {}", sql);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(rowMapper.mapRow(rs, 1));
                }
                return results;
            }
        }
    }

    @Override
    public List<T> findAll(List<String> conditions, List<Object> params) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder("""
                SELECT * FROM %s
                WHERE 1 = 1
                """.formatted(tableName));
        for (String condition : conditions) {
            sqlBuilder.append(" AND ").append(condition);
        }

        log.info("SQL : {}", sqlBuilder);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sqlBuilder.toString())) {
            int index = 1;
            for (Object param : params) {
                ps.setObject(index++, param);
            }

            try (ResultSet rs = ps.executeQuery()) {
                List<T> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(rowMapper.mapRow(rs, 1));
                }
                return results;
            }
        }
    }

    @Override
    public T update(ID id, Map<String, Object> fieldUpdates, Set<String> allowedFields) throws SQLException {
        if (fieldUpdates == null || fieldUpdates.isEmpty()) {
            return findById(id).orElseThrow(() -> new SQLException("No such field: " + id));
        }

        // Filter allowed fields
        Map<String, Object> filteredUpdates = fieldUpdates.entrySet().stream()
                .filter(e -> allowedFields == null || allowedFields.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // If no valid fields after filtering, return the current record
        if (filteredUpdates.isEmpty()) {
            return findById(id).orElseThrow(() -> new SQLException("No such field: " + id));
        }

        String setClause = filteredUpdates.keySet().stream()
                .map(k -> camelToSnake(k) + " = ?")
                .collect(Collectors.joining(", "));

        String sql = """
                UPDATE %s
                SET %s
                WHERE %s = ?
                RETURNING *
                """.formatted(tableName, setClause, idColumnName);

        log.info("SQL: {}", sql);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            int idx = 1;
            for (Object value : filteredUpdates.values()) {
                if (value instanceof Enum<?>) {
                    ps.setObject(idx++, ((Enum<?>) value).name(), Types.OTHER);
                } else {
                    ps.setObject(idx++, value);
                }
            }
            ps.setObject(idx, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rowMapper.mapRow(rs, 1);
                }
                throw new SQLException("Update failed, no rows affected");
            }
        }
    }

    @Override
    public boolean delete(ID id) throws SQLException {
        String sql = """
                DELETE FROM %s
                WHERE %s = ?
                """.formatted(tableName, idColumnName);

        log.info("SQL: {}", sql);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(List<ID> ids) throws SQLException {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        String placeholders = ids.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));

        String sql = """
                DELETE FROM %s
                WHERE (%s) IN (%s)
                """.formatted(tableName, placeholders, idColumnName);

        log.info("SQL: {}", sql);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            for (int i = 0; i < ids.size(); i++) {
                ps.setObject(i + 1, ids.get(i));
            }

            int deletedRows = ps.executeUpdate();
            return deletedRows > 0;
        }
    }

    protected String camelToSnake(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    protected abstract Map<String, Object> getCreateFieldMap(C createDto);

    protected abstract boolean isInsertableField(String fieldName);
}
