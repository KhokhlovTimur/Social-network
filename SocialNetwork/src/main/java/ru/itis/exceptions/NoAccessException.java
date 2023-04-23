package ru.itis.exceptions;

public class NoAccessException extends RuntimeException{
    public NoAccessException(String message) {
        super(message);
    }
}
