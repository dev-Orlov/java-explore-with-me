package ru.practicum.main.exception;

public class IncorrectRequestException extends RuntimeException {

    public IncorrectRequestException(String s) {
        super(s);
    }
}
