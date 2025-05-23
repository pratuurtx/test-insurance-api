package com.streamit.application.utils;

import com.streamit.application.constants.ResponseConstant;
import com.streamit.application.dtos.common.Paging;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseUtil {

    public static <T> Map<String, Object> success(String message, T data) {
        return createSuccessResponse(HttpStatus.OK.value(), message, data);
    }

    public static <T> Map<String, Object> created(String message, T data) {
        return createSuccessResponse(HttpStatus.CREATED.value(), message, data);
    }

    public static Map<String, Object> error(String message, List<String> errors) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, errors);
    }

    public static Map<String, Object> badRequest(String message, List<String> errors) {
        return createErrorResponse(HttpStatus.BAD_REQUEST.value(), message, errors);
    }

    public static Map<String, Object> conflict(String message, List<String> errors) {
        return createErrorResponse(HttpStatus.CONFLICT.value(), message, errors);
    }

    public static Map<String, Object> methodNotAllowed(String message, List<String> errors) {
        return createErrorResponse(HttpStatus.METHOD_NOT_ALLOWED.value(), message, errors);
    }

    public static Map<String, Object> notFound(String message, List<String> errors) {
        return createErrorResponse(HttpStatus.NOT_FOUND.value(), message, errors);
    }

    private static <T> Map<String, Object> createSuccessResponse(int statusCode, String message, T data) {
        return Map.of(
                ResponseConstant.STATUS_CODE, statusCode,
                ResponseConstant.MESSAGE, message,
                ResponseConstant.DATA, data
        );
    }

    // Core method for error responses
    private static <T> Map<String, Object> createErrorResponse(int statusCode, String message, List<String> errors) {
        return Map.of(
                ResponseConstant.STATUS_CODE, statusCode,
                ResponseConstant.MESSAGE, message,
                ResponseConstant.ERROR, errors
        );
    }

    public static Map<String, Object> createNoDataResponse(String message) {
        return Map.of(
                ResponseConstant.STATUS_CODE, HttpStatus.OK.value(),
                ResponseConstant.MESSAGE, message
        );
    }

    public static <T> Map<String, Object> createPagingResponse(String message, T data, Paging paging) {
        return Map.of(
                ResponseConstant.STATUS_CODE, HttpStatus.OK.value(),
                ResponseConstant.MESSAGE, message,
                ResponseConstant.DATA, data,
                ResponseConstant.PAGING, paging
        );
    }
}

