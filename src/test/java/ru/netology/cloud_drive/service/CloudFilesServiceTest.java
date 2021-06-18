package ru.netology.cloud_drive.service;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloud_drive.controller.CloudFilesControllerTest;
import ru.netology.cloud_drive.model.FileRequest;
import ru.netology.cloud_drive.repository.CloudServiceRepository;

import java.io.IOException;
import java.util.List;

public class CloudFilesServiceTest {

    CloudServiceRepository cloudServiceRepositoryMock = Mockito.mock(CloudServiceRepository.class);
    CloudAuthenticationService cloudAuthenticationServiceMock = Mockito.mock(CloudAuthenticationService.class);
    MultipartFile testMultipartFile = Mockito.mock(MultipartFile.class);

    CloudFilesService cloudFilesService = new CloudFilesService(
            cloudServiceRepositoryMock,
            cloudAuthenticationServiceMock
    );

    String testUsername = "User";
    long testId = 1l;
    String testAuthToken = "Bearer_testAuthToken";
    List<FileRequest> testListFiles = CloudFilesControllerTest.addFiles();
    int testLimit = 3;
    String testFilename = "testFile1.txt";
    String testNewFilename = "testFile1_NEW.txt";

    @BeforeEach
    public void mockBefore() {
        Mockito.when(cloudAuthenticationServiceMock.getUsernameByTokenFromMap(testAuthToken))
                .thenReturn(testUsername);
        Mockito.when(cloudServiceRepositoryMock.getIdByUsername(testUsername))
                .thenReturn(testId);
    }

    @Test
    void testGetUserIdCloudFilesService() {
        long result = cloudFilesService.getUserId(testAuthToken);
        long expected = testId;
        Assertions.assertEquals(expected, result);
    }

    @Before
    public void mockBeforeGetAllFiles() {
        Mockito.when(cloudServiceRepositoryMock.getFilenamesFromStorage(testId))
                .thenReturn(testListFiles);
    }

    @Test
    void testGetAllFilesCloudFilesService() {
        mockBeforeGetAllFiles();
        List<FileRequest> result = cloudFilesService.getAllFiles(testAuthToken, testLimit);
        List<FileRequest> expected = testListFiles;
        Assertions.assertEquals(expected, result);
    }

    @Before
    public void mockUploadFile() throws IOException {
        Mockito.when(cloudServiceRepositoryMock.uploadFile(testMultipartFile, testId, testFilename))
                .thenReturn(true);
    }

    @Test
    void testUploadFileCloudFilesService() throws IOException {
        mockUploadFile();
        Boolean result = cloudFilesService.uploadFileToServer(testAuthToken, testFilename, testMultipartFile);
        Boolean expected = true;
        Assertions.assertEquals(expected, result);
    }

    @Before
    public void mockDeleteFile() {
        Mockito.when(cloudServiceRepositoryMock.deleteFile(testFilename, testId))
                .thenReturn(true);
    }

    @Test
    void testDeleteFileCloudFilesService() {
        mockDeleteFile();
        Boolean result = cloudFilesService.deleteFile(testAuthToken, testFilename);
        Boolean expected = true;
        Assertions.assertEquals(expected, result);
    }

    @Before
    public void mockRenameFile() {
        Mockito.when(cloudServiceRepositoryMock.renameFile(testFilename, testNewFilename, testId))
                .thenReturn(true);
    }

    @Test
    void testRenameFileCloudFilesService() throws IOException {
        mockRenameFile();
        Boolean result = cloudFilesService.renameFile(testAuthToken, testFilename, testNewFilename);
        Boolean expected = true;
        Assertions.assertEquals(expected, result);
    }

    @Before
    public void mockDownloadFile() {
        Mockito.when(cloudServiceRepositoryMock.downloadFileFromServer(testId, testFilename))
                .thenReturn(true);
    }

    @Test
    void testDownloadFileCloudFilesService() {
        mockDownloadFile();
        Boolean result = cloudFilesService.downloadFileFromServer(testAuthToken, testFilename);
        Boolean expected = true;
        Assertions.assertEquals(expected, result);
    }
}