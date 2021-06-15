package ru.netology.cloud_drive.exception;

public class ErrorDeleteFile extends RuntimeException {
    public ErrorDeleteFile(String message) {
        super(message);
    }
}
