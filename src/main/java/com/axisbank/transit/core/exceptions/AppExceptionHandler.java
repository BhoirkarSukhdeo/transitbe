package com.axisbank.transit.core.exceptions;

import com.axisbank.transit.core.model.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
public class AppExceptionHandler {
    /**
     * This handler catches all the exceptions that are not explicitly handled and return response in formatted way.
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(value= {Exception.class, RuntimeException.class})
    public ResponseEntity<Object> handleOtherException(Exception ex, WebRequest request){
        log.error("Exception in handleOtherException: {}", ex.getMessage());
        return new ResponseEntity<>(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(),
                null), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * This handler catches AcessDenied Exception that are not explicitly handled and return response in formatted way.
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(value= {AccessDeniedException.class})
    public ResponseEntity<Object> handleAccessDeniedException(Exception ex, WebRequest request){
        log.error("Exception in handleAccessDeniedException method: {}", ex.getMessage());
        return new ResponseEntity<>(new BaseResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage(),
                null), new HttpHeaders(), HttpStatus.FORBIDDEN);
    }
}