package ru.netology.cloud_drive.exception;

public class ErrorDownloadFile extends RuntimeException {
    public ErrorDownloadFile(String message) {
        super(message);
    }
}
