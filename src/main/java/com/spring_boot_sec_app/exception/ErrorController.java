package com.spring_boot_sec_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> resourceNotFound(ResourceNotFoundException ex) {
        final ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(AppException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> appException(AppException ex) {
        final ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(HttpServletRequest request, Throwable ex) {
        final ApiError apiError = new ApiError(getStatus(request));
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }



    private ResponseEntity<?> buildResponseEntity(final ApiError apiError) {
        return new ResponseEntity<>(apiError,apiError.getStatus());
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if(statusCode == null)
            return HttpStatus.INTERNAL_SERVER_ERROR;
        return HttpStatus.valueOf(statusCode);
    }
}
