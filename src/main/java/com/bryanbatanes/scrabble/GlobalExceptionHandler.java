package com.bryanbatanes.scrabble;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public static final String ERROR = "errorMessage";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException manve) {
        StringBuilder msg = new StringBuilder("");
        AtomicInteger count = new AtomicInteger(0);

        manve.getBindingResult().getAllErrors().forEach(error -> {
            if(count.getAndIncrement() > 0) msg.append("\n");
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            msg.append(field).append(" : ").append(message);
        });
        return Map.of(ERROR, msg.toString());
    }

}
