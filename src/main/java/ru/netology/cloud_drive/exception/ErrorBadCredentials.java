package ru.netology.cloud_drive.exception;

public class ErrorBadCredentials extends RuntimeException {
    public ErrorBadCredentials(String message) {
        super(message);
    }
}
