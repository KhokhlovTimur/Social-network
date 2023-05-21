package ru.itis.aspects.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.itis.dto.other.ExceptionDto;
import ru.itis.exceptions.IsAlreadyExists;
import ru.itis.exceptions.NoAccessException;
import ru.itis.exceptions.NotFoundException;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionDto> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ExceptionDto.builder()
                        .message(e.getMessage())
                        .status(HttpStatus.NOT_FOUND.value())
                        .build());
    }

    @ExceptionHandler(NoAccessException.class)
    public ResponseEntity<ExceptionDto> handleNoAccessException(NoAccessException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ExceptionDto.builder()
                        .message(e.getMessage())
                        .status(HttpStatus.FORBIDDEN.value())
                        .build());
    }

    @ExceptionHandler(IsAlreadyExists.class)
    public ResponseEntity<ExceptionDto> handleNoAccessException(IsAlreadyExists e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ExceptionDto.builder()
                        .message(e.getMessage())
                        .status(HttpStatus.FORBIDDEN.value())
                        .build());
    }
}
