package ru.netology.cloud_drive.exception;

public class ErrorGettingFileList extends RuntimeException {
    public ErrorGettingFileList(String message) {
        super(message);
    }
}
