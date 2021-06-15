package ru.netology.cloud_drive.exception;

public class ErrorUploadFile extends RuntimeException {
    public ErrorUploadFile(String message) {
        super(message);
    }
}
