package ru.itis.aspects.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.itis.dto.other.ExceptionDto;
import ru.itis.exceptions.AlreadyExistsException;
import ru.itis.exceptions.NoAccessException;
import ru.itis.exceptions.NotFoundException;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<List<ExceptionDto>> handleNotFoundException(NotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(List.of(ExceptionDto.builder()
                        .message(e.getMessage())
                        .build()));
    }

    @ExceptionHandler(NoAccessException.class)
    public ResponseEntity<List<ExceptionDto>> handleNoAccessException(NoAccessException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(List.of(ExceptionDto.builder()
                        .message(e.getMessage())
                        .build()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<List<ExceptionDto>> handleValidationException(MethodArgumentNotValidException e) {
        log.error(e.getMessage());

        List<ExceptionDto> errors = new ArrayList<>();

        e.getBindingResult().getAllErrors().forEach(error -> {
            ExceptionDto errorDto = ExceptionDto.builder()
                    .message(error.getDefaultMessage())
                    .build();

            errors.add(errorDto);
        });

        return ResponseEntity.badRequest()
                .body(errors);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<List<ExceptionDto>> handleNoAccessException(AlreadyExistsException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(List.of(ExceptionDto.builder()
                        .message(e.getMessage())
                        .build()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionDto> handleConstraintValidationException(ConstraintViolationException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionDto.builder()
                        .message(e.getMessage())
                        .build());
    }
}
