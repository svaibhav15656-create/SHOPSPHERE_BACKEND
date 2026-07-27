package com.shopsphere.product_service.exception;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;


@RestControllerAdvice
public class GlobalExceptionHandler{



    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String>  handleRuntimeException(RuntimeException ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

}