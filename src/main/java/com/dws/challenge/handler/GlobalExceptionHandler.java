package com.dws.challenge.handler;

import com.dws.challenge.exception.SelfFundTransferException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SelfFundTransferException.class)
    public ResponseEntity<String> handle(SelfFundTransferException exp) {
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(exp.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException exp) {

        var errors = new HashMap<String,String>();
        exp.getBindingResult().getAllErrors().forEach(
                error -> {
                    var fieldName = ((FieldError)error).getField();
                    var errorMesaage = error.getDefaultMessage();
                    errors.put(fieldName,errorMesaage);
                }

        );

        return  ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(errors));
    }

}
