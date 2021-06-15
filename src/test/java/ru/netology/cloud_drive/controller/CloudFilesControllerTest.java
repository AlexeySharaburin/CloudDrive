package ru.netology.cloud_drive.controller;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloud_drive.model.FileRequest;
import ru.netology.cloud_drive.model.NewFilename;
import ru.netology.cloud_drive.service.CloudFilesService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CloudFilesControllerTest {

    CloudFilesService cloudFilesServiceMock = Mockito.mock(CloudFilesService.class);

    CloudFilesController cloudFilesController = new CloudFilesController(
            cloudFilesServiceMock
    );

    MultipartFile testMultipartFileMock = Mockito.mock(MultipartFile.class);

    int testLimit = 3;
    String testAuthToken = "TestAuthToken";
    String testFilename = "testFile1.txt";
    String testNewFilename = "testFile1.txt";
    NewFilename testNFN = new NewFilename(testNewFilename);
    List<FileRequest> testListFiles = addFiles();



    public static List<FileRequest> addFiles() {
        List<FileRequest> listFiles = new ArrayList<>();
        listFiles.add(new FileRequest("testFile1.txt", 2000));
        listFiles.add(new FileRequest("testFile2.txt", 3000));
        listFiles.add(new FileRequest("testFile3.txt", 4000));
        return listFiles;
    }

    @Before
    public void mockListFiles() {
        Mockito.when(cloudFilesServiceMock.getAllFiles(testAuthToken, testLimit))
                .thenReturn(testListFiles);
    }
    @Test
    void testListCloudFilesController() {
        mockListFiles();
        ResponseEntity<List<FileRequest>> result = new CloudFilesController(cloudFilesServiceMock).getAllFiles(testAuthToken, testLimit);
        ResponseEntity<List<FileRequest>> expected = new ResponseEntity<>(testListFiles, HttpStatus.OK);
        Assertions.assertEquals(expected, result);
    }

    @Before
    public void mockUploadFile() throws IOException {
        Mockito.when(cloudFilesServiceMock.uploadFileToServer(testAuthToken, testFilename, testMultipartFileMock))
                .thenReturn(true);
    }
    @Test
    void testUploadFileToServerCloudFilesController() throws Exception {
        mockUploadFile();
        ResponseEntity<String> result = new CloudFilesController(cloudFilesServiceMock).uploadFileToServer(testAuthToken, testFilename, testMultipartFileMock);
        ResponseEntity<String> expected = new ResponseEntity<>("Success upload " + testFilename, HttpStatus.OK);
        Assertions.assertEquals(expected, result);
    }

    @Before
    public void mockDeleteFile() {
        Mockito.when(cloudFilesServiceMock.deleteFile(testAuthToken, testFilename))
                .thenReturn(true);
    }
    @Test
    void testDeleteFileCloudFilesController() {
        mockDeleteFile();
        ResponseEntity<String> result = new CloudFilesController(cloudFilesServiceMock).deleteFile(testAuthToken, testFilename);
        ResponseEntity<String> expected = new ResponseEntity<>("Success deleted " + testFilename, HttpStatus.OK);
        Assertions.assertEquals(expected, result);
    }

    @Before
    public void mockRenameFile() {
        Mockito.when(cloudFilesServiceMock.renameFile(testAuthToken, testFilename, testNewFilename))
                .thenReturn(true);
    }
    @Test
    void testRenameFileCloudFilesController() {
        mockRenameFile();
        ResponseEntity<String> result = new CloudFilesController(cloudFilesServiceMock).renameFile(testAuthToken, testFilename, testNFN);
        ResponseEntity<String> expected = new ResponseEntity<>("Success upload from" + testFilename + " to " + testNewFilename, HttpStatus.OK);
        Assertions.assertEquals(expected, result);
    }

    @Before
    public void mockDownloadFile() {
        Mockito.when(cloudFilesServiceMock.downloadFileFromServer(testAuthToken, testFilename))
                .thenReturn(true);
    }
    @Test
    void testDownloadFileCloudFilesController() throws IOException {
        mockDownloadFile();
        ResponseEntity<String> result = new CloudFilesController(cloudFilesServiceMock).downloadFileFromServer(testAuthToken, testFilename);
        ResponseEntity<String> expected = new ResponseEntity<>("Success downloaded " + testFilename, HttpStatus.OK);
        Assertions.assertEquals(expected, result);
    }
}