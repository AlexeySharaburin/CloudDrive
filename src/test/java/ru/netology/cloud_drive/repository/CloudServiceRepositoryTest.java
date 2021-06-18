package ru.netology.cloud_drive.repository;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloud_drive.model.FileRequest;
import ru.netology.cloud_drive.model.Storage;
import ru.netology.cloud_drive.model.UserData;
import ru.netology.cloud_drive.service.MultipartFileService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

// Перед запуском тестов в папке drives/test_folder должны находиться файлы testFile1.txt, testFile2.txt,
// и testFile4.txt. Копии этих файлов, а также файл testFile5.txt хранятся в папке /downloads
public class CloudServiceRepositoryTest {

    UserDataRepository userDataRepositoryMock = Mockito.mock(UserDataRepository.class);
    StorageRepository storageRepositoryMock = Mockito.mock(StorageRepository.class);

    String testGeneralPath = "/Users/alexey/Desktop/CloudDrive/cloud_drive/drives";
    String testDownloadPath = "/Users/alexey/Desktop/CloudDrive/cloud_drive/downloads";

    CloudServiceRepository cloudServiceRepository = new CloudServiceRepository(
            userDataRepositoryMock,
            storageRepositoryMock,
            testGeneralPath,
            testDownloadPath

    );

    List<FileRequest> testListFiles = addFiles();

    public CloudServiceRepositoryTest() throws IOException {
    }

    public static List<FileRequest> addFiles() {
        List<FileRequest> listFiles = new ArrayList<>();
        listFiles.add(new FileRequest("testFile1.txt", 2000));
        listFiles.add(new FileRequest("testFile2.txt", 3000));
        listFiles.add(new FileRequest("testFile3.txt", 4000));
        listFiles.add(new FileRequest("testFile4.txt", 5000));
        listFiles.add(new FileRequest("testFile5.txt", 12));
        return listFiles;
    }

    Storage testStorage_1 = new Storage(1L, "testFile1.txt", true, new Date(), 1L, 2000);
    Storage testStorage_2 = new Storage(2L, "testFile2.txt", true, new Date(), 2L, 3000);
    Storage testStorage_3 = new Storage(3L, "testFile3.txt", true, new Date(), 1L, 4000);
    Storage testStorage_4 = new Storage(4L, "testFile4.txt", true, new Date(), 1L, 5000);
    Storage testStorage_5 = new Storage(5L, "testFile5.txt", true, new Date(), 1L, 12);

    List<Storage> testStorageListFiles = addStorageFiles();

    public List<Storage> addStorageFiles() {
        List<Storage> listStorageFiles = new ArrayList<>();
        listStorageFiles.add(testStorage_1);
        listStorageFiles.add(testStorage_2);
        listStorageFiles.add(testStorage_3);
        listStorageFiles.add(testStorage_4);
        listStorageFiles.add(testStorage_5);
        return listStorageFiles;
    }

    String testUsername = "Username";
    String testLocalPath = "test_folder";
    long testId = 1L;
    UserData testCurrentUser = new UserData(testId, testUsername, "111", testLocalPath, true);
    Boolean testExist = true;


    String testFilename_1 = "testFile1.txt";
    String testFilename_2 = "testFile2.txt";
    String testFilename_3 = "testFile3.txt";
    String testFilename_4 = "testFile4.txt";
    String testFilename_5 = "testFile5.txt";
    String testFilenameExpected = "testFile1(1).txt";
    String testNewFilename = "testNewFilename.txt";

    String testAbsolutePath = "/Users/alexey/Desktop/CloudDrive/cloud_drive/drives/test_folder";
    String testFilePath = "/Users/alexey/Desktop/CloudDrive/cloud_drive/drives/test_folder/testFile1.txt";
    String testUploadFilePath = "/Users/alexey/Desktop/CloudDrive/cloud_drive/downloads/testFile5.txt";

    MultipartFile testMultipartFile = MultipartFileService.convertFiletoMultiPart(testUploadFilePath);

    @BeforeEach
    public void mockBefore() {
        Mockito.when(userDataRepositoryMock.findByUsername(testUsername))
                .thenReturn(Optional.of(testCurrentUser));
        Mockito.when(storageRepositoryMock.findByUserIdAndIsExist(testId, testExist))
                .thenReturn(testStorageListFiles);
        Mockito.when(userDataRepositoryMock.findById(testId))
                .thenReturn(Optional.of(testCurrentUser));
        Mockito.when(storageRepositoryMock.existsById(testId))
                .thenReturn(false);
    }

    @Test
    void testGetUserByUsernameCloudServiceRepository() {
        UserData result = cloudServiceRepository.getUserByUsername(testUsername);
        UserData expected = testCurrentUser;
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testGetIdByUsernameCloudServiceRepository() {
        long result = cloudServiceRepository.getIdByUsername(testUsername);
        long expected = testId;
        Assertions.assertEquals(expected, result);
    }

    @Before
    public void mockGetFilenamesFromStorage() {
        Mockito.when(storageRepositoryMock.findByUserIdAndIsExist(testId, testExist))
                .thenReturn(testStorageListFiles);
    }

    @Test
    void testGetFilenamesFromStorageCloudServiceRepository() {
        mockGetFilenamesFromStorage();
        List<FileRequest> result = cloudServiceRepository.getFilenamesFromStorage(testId);
        List<FileRequest> expected = testListFiles;
        Assertions.assertEquals(expected, result);
    }

    @Before
    public void mockSaveFile() {
        Mockito.when(storageRepositoryMock.save(testStorage_4))
                .thenReturn(testStorage_4);
    }

    @Test
    void testSaveFileStorageCloudServiceRepository() {
        mockSaveFile();
        Boolean result = cloudServiceRepository.saveFile(testStorage_4);
        Boolean expected = true;
        Assertions.assertEquals(expected, result);
    }

    @Before
    public void mockDeleteFile() {
        Mockito.when(storageRepositoryMock.findByFilenameAndUserId(testFilename_3, testId))
                .thenReturn(testStorage_3);
    }

    @Test
    void testDeleteFileCloudServiceRepository() {
        mockDeleteFile();
        Boolean result = cloudServiceRepository.deleteFile(testFilename_3, testId);
        Boolean expected = true;
        Assertions.assertEquals(expected, result);
    }

    @Before
    public void mockRenameFile() {
        Mockito.when(storageRepositoryMock.findByFilenameAndUserId(testFilename_2, testId))
                .thenReturn(testStorage_2);
    }

    @Test
    void testRenameFileCloudServiceRepository() {
        mockRenameFile();
        Boolean result = cloudServiceRepository.renameFile(testFilename_2, testNewFilename, testId);
        Boolean expected = true;
        Assertions.assertEquals(expected, result);
    }

    @Before
    public void mockUploadFile() {
        Mockito.when(storageRepositoryMock.save(testStorage_5))
                .thenReturn(testStorage_5);
    }

    @Test
    void testUploadFileCloudServiceRepository() throws IOException {
        mockUploadFile();
        Boolean result = cloudServiceRepository.uploadFile(testMultipartFile, testId, testFilename_5);
        Boolean expected = true;
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testDownloadFileCloudServiceRepository() {
        Boolean result = cloudServiceRepository.downloadFileFromServer(testId, testFilename_4);
        Boolean expected = true;
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testExistFileStorageCloudServiceRepository() {
        Boolean result = cloudServiceRepository.existFileStorage(testId, testFilename_1);
        Boolean expected = true;
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testCheckFilenameStorageCloudServiceRepository() {
        String result = cloudServiceRepository.checkFilenameStorage(testId, testFilename_1);
        String expected = testFilenameExpected;
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testExistFileFolderCloudServiceRepository() {
        Boolean result = cloudServiceRepository.existFileFolder(testAbsolutePath, testFilename_1);
        Boolean expected = true;
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testCheckFilenameFolderCloudServiceRepository() {
        String result = cloudServiceRepository.checkFilenameFolder(testAbsolutePath, testFilename_1);
        String expected = testFilenameExpected;
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testGetLocalDataPathCloudServiceRepository() {
        String result = cloudServiceRepository.getLocalDataPath(testId);
        String expected = testLocalPath;
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testGetAbsolutePathCloudServiceRepository() {
        String result = cloudServiceRepository.getAbsolutePath(testId);
        String expected = testAbsolutePath;
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testGetAbsolutePathFileCloudServiceRepository() {
        String result = cloudServiceRepository.getAbsolutePathFile(testId, testFilename_1);
        String expected = testFilePath;
        Assertions.assertEquals(expected, result);
    }
}