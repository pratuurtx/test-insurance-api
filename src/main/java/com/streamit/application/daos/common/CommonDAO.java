package com.streamit.application.daos.common;

import java.sql.SQLException;
import java.util.*;

public interface CommonDAO<T, ID, C> {
    T insert(C createDTO) throws SQLException;

    List<T> insert(List<C> createDTOs) throws SQLException;

    Optional<T> findById(ID id) throws SQLException;

    List<T> findAll() throws SQLException;
    List<T> findAll(UUID id) throws SQLException;
    T update(ID id, Map<String, Object> fieldUpdates, Set<String> allowedFields) throws SQLException;

    boolean delete(ID id) throws SQLException;
    boolean delete(List<ID> ids) throws SQLException;
}
