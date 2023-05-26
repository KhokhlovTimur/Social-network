package ru.itis.aspects.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.itis.dto.other.ExceptionDto;
import ru.itis.exceptions.AlreadyExistsException;
import ru.itis.exceptions.NoAccessException;
import ru.itis.exceptions.NotFoundException;
import ru.itis.exceptions.WrongPasswordException;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<List<ExceptionDto>> handleNotFoundException(NotFoundException e) {
        return generateResponse(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(NoAccessException.class)
    public ResponseEntity<List<ExceptionDto>> handleNoAccessException(NoAccessException e) {
        return generateResponse(HttpStatus.FORBIDDEN, e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<List<ExceptionDto>> handleValidationException(MethodArgumentNotValidException e) {
        return generateBindingErrorsResponse(e.getBindingResult());
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<List<ExceptionDto>> handleNoAccessException(AlreadyExistsException e) {
        return generateResponse(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<ExceptionDto>> handleConstraintValidationException(ConstraintViolationException e) {
        return generateResponse(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<List<ExceptionDto>> handleConstraintValidationException(BindException e) {
        return generateBindingErrorsResponse(e.getBindingResult());
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<List<ExceptionDto>> handleConstraintValidationException(WrongPasswordException e) {
        return generateResponse(HttpStatus.BAD_REQUEST, e);
    }

    private ResponseEntity<List<ExceptionDto>> generateResponse(HttpStatus status, Exception e) {
        log.error(e.getMessage());
        return ResponseEntity.status(status)
                .body(List.of(ExceptionDto.builder()
                        .message(e.getMessage())
                        .build()));
    }

    private ResponseEntity<List<ExceptionDto>> generateBindingErrorsResponse(BindingResult result) {
        List<ExceptionDto> errors = new ArrayList<>();

        result.getAllErrors().forEach(error -> {
            log.error(error.getDefaultMessage());
            ExceptionDto errorDto = ExceptionDto.builder()
                    .message(error.getDefaultMessage())
                    .build();

            errors.add(errorDto);
        });

        return ResponseEntity.badRequest()
                .body(errors);
    }
}
