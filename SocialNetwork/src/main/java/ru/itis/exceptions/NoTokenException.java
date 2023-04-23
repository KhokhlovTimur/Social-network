package ru.itis.exceptions;

public class NoTokenException extends RuntimeException{
    public NoTokenException(String message) {
        super(message);
    }
}
