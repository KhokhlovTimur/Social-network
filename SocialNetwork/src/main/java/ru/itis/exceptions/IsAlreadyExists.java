package ru.itis.exceptions;

public class IsAlreadyExists extends RuntimeException{
    public IsAlreadyExists(String message) {
        super(message);
    }
}
