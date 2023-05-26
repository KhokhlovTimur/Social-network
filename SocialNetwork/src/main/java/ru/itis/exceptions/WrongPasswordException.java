package ru.itis.exceptions;

public class WrongPasswordException extends RuntimeException{
    public WrongPasswordException(String message) {
        super(message);
    }
}
