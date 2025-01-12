package com.cocos.cocos.common.handler;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.common.response.message.FailMessage;
import com.cocos.cocos.common.response.FailResponse;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CocosException.class)
    public ResponseEntity<BaseResponse<?>> handleAgodaException(final CocosException e) {
        return FailResponse.failure(e.getFailMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<?>> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        final String errorMessage = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("\n"));

        return FailResponse.failure(FailMessage.BAD_REQUEST_REQUEST_BODY_VALID, errorMessage);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BaseResponse<?>> handleMissingParamException(final MissingServletRequestParameterException e) {
        final String errorMessage = "누락된 파라미터 : " + e.getParameterName();
        return FailResponse.failure(FailMessage.BAD_REQUEST_MISSING_PARAM, errorMessage);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseResponse<?>> handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        final String errorMessage = "잘못된 인자값 : " + e.getParameter().getParameterName();
        return FailResponse.failure(FailMessage.BAD_REQUEST_METHOD_ARGUMENT_TYPE, errorMessage);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse<?>> handleHttpMessageNotReadableException(final HttpMessageNotReadableException e) {
        if (e.getCause() instanceof JsonMappingException jsonMappingException) {
            final String errorMessage = jsonMappingException.getPath().stream()
                    .map(ref -> String.format("잘못된 필드 값 : '%s'", ref.getFieldName()))
                    .collect(Collectors.joining("\n"));
            return FailResponse.failure(FailMessage.BAD_REQUEST_NOT_READABLE, errorMessage);
        } else {
            return FailResponse.failure(FailMessage.BAD_REQUEST_NOT_READABLE);
        }
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<BaseResponse<?>> handleEntityNotFoundException(final EntityNotFoundException e) {
        return FailResponse.failure(FailMessage.NOT_FOUND_ENTITY);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<BaseResponse<?>> handleNoResourceFoundException(final NoResourceFoundException e) {
        return FailResponse.failure(FailMessage.NOT_FOUND_API);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<BaseResponse<?>> handleNoHandlerFoundException(final NoHandlerFoundException e) {
        return FailResponse.failure(FailMessage.NOT_FOUND_API);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BaseResponse<?>> handleHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException e) {
        return FailResponse.failure(FailMessage.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BaseResponse<?>> handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        if (e.getCause() instanceof ConstraintViolationException constraintViolationException) {
            final String constraintName = constraintViolationException.getConstraintViolations().toString();
            final String errorMessage = String.format("제약 조건 '%s' 위반이 발생했습니다.", constraintName);
            return FailResponse.failure(FailMessage.INTEGRITY_CONFLICT, errorMessage);
        } else {
            return FailResponse.failure(FailMessage.INTEGRITY_CONFLICT);
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<?>> handleGeneralException(Exception e) {
        return FailResponse.failure(FailMessage.INTERNAL_SERVER_ERROR);
    }
}
