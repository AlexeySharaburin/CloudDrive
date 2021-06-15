package ru.netology.cloud_drive.exception;

public class ErrorUnauthorized extends RuntimeException {
    public ErrorUnauthorized(String message) {
        super(message);
    }
}
