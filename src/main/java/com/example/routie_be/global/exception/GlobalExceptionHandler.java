package com.example.routie_be.global.exception;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.example.routie_be.global.common.ApiResponse;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 디버그용 상세 노출 스위치 (기본 false)
    @Value("${app.error.expose-details:false}")
    private boolean exposeDetails;

    private ResponseEntity<ApiResponse<Object>> resp(int status, String message, Object data) {
        return ResponseEntity.status(status).body(new ApiResponse<>(status, message, data));
    }

    /** 400 - @Valid 바디 검증 실패 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e) {
        Map<String, List<String>> errors =
                e.getBindingResult().getFieldErrors().stream()
                        .collect(
                                Collectors.groupingBy(
                                        fe -> fe.getField(),
                                        Collectors.mapping(
                                                fe -> fe.getDefaultMessage(),
                                                Collectors.toList())));

        String message = "요청 형식이 올바르지 않습니다.";
        Object data = exposeDetails ? Map.of("errors", errors) : null;

        log.debug("[400] MethodArgumentNotValidException: {}", errors, e);
        return resp(400, message, data);
    }

    /** 400 - @ModelAttribute 바인딩 실패 등 */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Object>> handleBindException(BindException e) {
        Map<String, List<String>> errors =
                e.getBindingResult().getFieldErrors().stream()
                        .collect(
                                Collectors.groupingBy(
                                        fe -> fe.getField(),
                                        Collectors.mapping(
                                                fe -> fe.getDefaultMessage(),
                                                Collectors.toList())));
        String message = "요청 형식이 올바르지 않습니다.";
        Object data = exposeDetails ? Map.of("errors", errors) : null;

        log.debug("[400] BindException: {}", errors, e);
        return resp(400, message, data);
    }

    /** 400 - JSON 파싱 실패, 잘못된 본문 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotReadable(
            HttpMessageNotReadableException e) {
        String message = "요청 본문을 읽을 수 없습니다.";
        Object data =
                exposeDetails ? Map.of("reason", e.getMostSpecificCause().getMessage()) : null;

        log.debug("[400] HttpMessageNotReadableException", e);
        return resp(400, message, data);
    }

    /** 400 - 요청 파라미터 누락 */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingParam(
            MissingServletRequestParameterException e) {
        String message = "필수 요청 파라미터가 누락되었습니다.";
        Object data = exposeDetails ? Map.of("parameter", e.getParameterName()) : null;

        log.debug("[400] MissingServletRequestParameterException: {}", e.getParameterName(), e);
        return resp(400, message, data);
    }

    /** 400 - 제약 위반(@Validated on params) */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraint(ConstraintViolationException e) {
        List<String> violations =
                e.getConstraintViolations().stream()
                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                        .collect(Collectors.toList());

        String message = "유효성 검증에 실패했습니다.";
        Object data = exposeDetails ? Map.of("violations", violations) : null;

        log.debug("[400] ConstraintViolationException: {}", violations, e);
        return resp(400, message, data);
    }

    /** 400 - 잘못된 인자 */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(IllegalArgumentException e) {
        String message = "잘못된 요청입니다.";
        Object data = exposeDetails ? Map.of("reason", e.getMessage()) : null;

        log.debug("[400] IllegalArgumentException: {}", e.getMessage(), e);
        return resp(400, message, data);
    }

    /** 💡 FriendsService 등에서 던진 ResponseStatusException 그대로 전달 */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Object>> handleResponseStatus(ResponseStatusException e) {
        String message = e.getReason() != null ? e.getReason() : e.getStatusCode().toString();
        Object data = exposeDetails ? Map.of("exception", e.getClass().getSimpleName()) : null;

        log.debug("[{}] ResponseStatusException: {}", e.getStatusCode().value(), message, e);
        return resp(e.getStatusCode().value(), message, data);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrity(
            DataIntegrityViolationException e) {
        String message = "이미 팔로우 중입니다.";
        Object data =
                exposeDetails ? Map.of("reason", e.getMostSpecificCause().getMessage()) : null;

        log.debug("[400] DataIntegrityViolationException", e);
        return resp(HttpStatus.BAD_REQUEST.value(), message, data);
    }

    /** 401 - 인증 실패 */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthentication(AuthenticationException e) {
        String message = "인증이 필요합니다.";
        Object data = exposeDetails ? Map.of("reason", e.getMessage()) : null;

        log.debug("[401] AuthenticationException: {}", e.getMessage(), e);
        return resp(401, message, data);
    }

    /** 403 - 권한 없음 */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException e) {
        String message = "접근 권한이 없습니다.";
        Object data = exposeDetails ? Map.of("reason", e.getMessage()) : null;

        log.debug("[403] AccessDeniedException: {}", e.getMessage(), e);
        return resp(403, message, data);
    }

    /** 404 - 매핑 없음 */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoHandler(NoHandlerFoundException e) {
        String message = "요청한 리소스를 찾을 수 없습니다.";
        Object data =
                exposeDetails
                        ? Map.of("path", e.getRequestURL(), "method", e.getHttpMethod())
                        : null;

        log.debug("[404] NoHandlerFoundException: {} {}", e.getHttpMethod(), e.getRequestURL(), e);
        return resp(404, message, data);
    }

    /** 405 - HTTP 메서드 미지원 */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException e) {
        String message = "지원하지 않는 HTTP 메서드입니다.";
        Object data =
                exposeDetails
                        ? Map.of("method", e.getMethod(), "supported", e.getSupportedHttpMethods())
                        : null;

        log.debug("[405] HttpRequestMethodNotSupportedException: {}", e.getMethod(), e);
        return resp(405, message, data);
    }

    /** 500 - 그 외 모든 예외 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneral(Exception e) {
        String message = "서버 내부 오류가 발생했습니다.";
        Object data = exposeDetails ? Map.of("reason", e.getMessage()) : null;

        log.error("[500] Unhandled Exception", e);
        return resp(500, message, data);
    }
}
