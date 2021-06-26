package ru.netology.cloud_drive.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloud_drive.CloudDriveApplication;
import ru.netology.cloud_drive.controller.CloudFilesController;
import ru.netology.cloud_drive.exception.ErrorUnauthorized;
import ru.netology.cloud_drive.model.FileRequest;
import ru.netology.cloud_drive.repository.CloudServiceRepository;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CloudFilesService {

    private final CloudServiceRepository cloudServiceRepository;
    private final CloudAuthenticationService cloudAuthenticationService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudFilesService.class);

    public CloudFilesService(CloudServiceRepository cloudServiceRepository, CloudAuthenticationService cloudAuthenticationService) {
        this.cloudServiceRepository = cloudServiceRepository;
        this.cloudAuthenticationService = cloudAuthenticationService;
    }

    public long getUserId(String authToken) {
        String username = cloudAuthenticationService.getUsernameByTokenFromMap(authToken);
        return cloudServiceRepository.getIdByUsername(username);
    }

    public List<FileRequest> getAllFiles(String authToken, int limit) {
        long userId = getUserId(authToken);
        List<FileRequest> files = cloudServiceRepository.getFilenamesFromStorage(userId);
        return files.stream()
                .limit(limit)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    public Boolean uploadFileToServer(String authToken, String filename, MultipartFile file) throws IOException {
        long userId = getUserId(authToken);
        if (file.isEmpty()) {
            LOGGER.error("File not found");
            throw new ErrorUnauthorized("Error input data");
        }
        if (cloudServiceRepository.uploadFile(file, userId, filename)) {
            LOGGER.info("Service_upload. Success upload {}", filename);
            return true;
        }
        return false;
    }

    public Boolean deleteFile(String authToken, String filename) {
        long userId = getUserId(authToken);
        if (cloudServiceRepository.deleteFile(filename, userId)) {
            LOGGER.info("Service_delete. Success deleted {}", filename);
            return true;
        }
        return false;
    }

    public Boolean renameFile(String authToken, String currentFilename, String newFilename) {
        long userId = getUserId(authToken);
        if (cloudServiceRepository.renameFile(currentFilename, newFilename, userId)) {
            LOGGER.info("Service_rename. Success rename file {}", newFilename);
            return true;
        }
        return false;
    }

    public boolean downloadFileFromServer(String authToken, String filename) {
        long userId = getUserId(authToken);
        if (cloudServiceRepository.downloadFileFromServer(userId, filename)) {
            LOGGER.info("Service_download. Success download file {}", filename);
            return true;
        }
        return false;
    }
}
