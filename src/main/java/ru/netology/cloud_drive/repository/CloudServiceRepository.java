package ru.netology.cloud_drive.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloud_drive.CloudDriveApplication;
import ru.netology.cloud_drive.controller.CloudFilesController;
import ru.netology.cloud_drive.exception.ErrorDeleteFile;
import ru.netology.cloud_drive.exception.ErrorUnauthorized;
import ru.netology.cloud_drive.exception.ErrorUploadFile;
import ru.netology.cloud_drive.model.FileRequest;
import ru.netology.cloud_drive.model.Storage;
import ru.netology.cloud_drive.model.UserData;

import javax.transaction.Transactional;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
@Transactional
public class CloudServiceRepository {

    private final UserDataRepository userDataRepository;

    private final StorageRepository storageRepository;

    @Value("${general.path}")
    private String generalPath;
    @Value("${download.user.folder.path}")
    private String downloadUserPath;

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudServiceRepository.class);

    @Autowired
    public CloudServiceRepository(UserDataRepository userDataRepository,
                                  StorageRepository storageRepository) {
        this.userDataRepository = userDataRepository;
        this.storageRepository = storageRepository;
    }

    public CloudServiceRepository(UserDataRepository userDataRepository,
                                  StorageRepository storageRepository,
                                  String generalPath,
                                  String downloadUserPath) {
        this.userDataRepository = userDataRepository;
        this.storageRepository = storageRepository;
        this.generalPath = generalPath;
        this.downloadUserPath = downloadUserPath;
    }

    public UserData getUserByUsername(String username) {
        UserData currentUser = userDataRepository.findByUsername(username).orElseThrow(IllegalArgumentException::new);
        if (currentUser.getIsEnable()) {
            return currentUser;
        } else {
            LOGGER.error("User not found");
            throw new ErrorUnauthorized("Unauthorized error");
        }
    }

    public long getIdByUsername(String username) {
        UserData currentUser = userDataRepository.findByUsername(username).orElseThrow(IllegalArgumentException::new);
        if (currentUser.getIsEnable()) {
            return currentUser.getId();
        } else {
            LOGGER.error("User not found");
            throw new ErrorUnauthorized("Unauthorized error");
        }
    }

    public List<FileRequest> getFilenamesFromStorage(long userId) {
        List<Storage> listStorage = storageRepository.findByUserIdAndIsExist(userId, true);
        List<FileRequest> files = new ArrayList<>();
        for (Storage storage : listStorage) {
            if (storage.getIsExist()) {
                files.add(new FileRequest(storage.getFilename(), storage.getFileSize()));
            }
        }
        return files;
    }

    public Boolean saveFile(Storage storage) {
        var newStorage = storageRepository.save(storage);
        return newStorage != null;
    }

    public Boolean deleteFile(String filename, long userId) {
        if (!existFileStorage(userId, filename)) {
            LOGGER.error("File not found");
            throw new ErrorDeleteFile("Error delete file");
        }
        Storage currentStorage = storageRepository.findByFilenameAndUserId(filename, userId);
        long currentId = currentStorage.getId();
        File currentFile = new File(getAbsolutePathFile(userId, filename));
        storageRepository.deleteById(currentId);
        if (!storageRepository.existsById(currentId) && currentFile.delete()) {
            LOGGER.info("Repo_deleted. Deleted");
            return true;
        }
        LOGGER.error("Repo_deleted. No deleted");
        return false;
    }

    public Boolean renameFile(String currentFilename, String newFilename, long userId) {
        Storage currentStorage = storageRepository.findByFilenameAndUserId(currentFilename, userId);
        UserData currentUser = userDataRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
        if (!existFileStorage(userId, currentFilename)) {
            LOGGER.error("File not found");
            throw new ErrorUploadFile("Error upload file");
        }
        if (currentStorage.getIsExist() && (currentUser != null)) {
            File currentFile = new File(getAbsolutePathFile(userId, currentFilename));
            File newFile = new File(getAbsolutePath(userId) + File.separator + newFilename);
            if (currentFile.renameTo(newFile)) {
                currentStorage.setFilename(newFilename);
                LOGGER.info("Repo_rename. Success rename file {}", newFilename);
                return true;
            }
        }
        return false;
    }

    public boolean uploadFile(MultipartFile file, long userId, String filename) throws IOException {
        String checkedFilename = checkFilenameStorage(userId, filename);
        String dataPath = getAbsolutePath(userId);

        File directoryOfUser = new File(dataPath);
        if (!directoryOfUser.exists()) {
            directoryOfUser.mkdirs();
        }

        String checkNameUploadedFile = checkFilenameFolder(dataPath, filename);
        String pathUploadedFile = getAbsolutePathFile(userId, checkNameUploadedFile);
        File uploadedFile = new File(pathUploadedFile);

        byte[] bytes = file.getBytes();
        try (var out = new FileOutputStream(uploadedFile);
             var bos = new BufferedOutputStream(out)) {
            bos.write(bytes, 0, bytes.length);
            Storage newFile = Storage.builder()
                    .filename(checkedFilename)
                    .isExist(true)
                    .date(new Date())
                    .userId(userId)
                    .fileSize(file.getSize())
                    .build();
            saveFile(newFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean downloadFileFromServer(long userId, String filename) {
        if (!existFileStorage(userId, filename)) {
            LOGGER.error("Error download file");
            throw new ErrorUploadFile("Error download file");
        }
        String downloadPathUser = downloadUserPath + File.separator;

        String checkNameDownloadedUserFile = checkFilenameFolder(downloadPathUser, filename);
        String pathFile = getAbsolutePathFile(userId, filename);

        try (var fin = new FileInputStream(pathFile);
             var bis = new BufferedInputStream(fin, 1024);
             var outUser = new FileOutputStream(downloadPathUser + checkNameDownloadedUserFile);
             var bosUser = new BufferedOutputStream(outUser, 1024)
        ) {
            int i;
            while ((i = bis.read()) != -1) {
                bosUser.write(i);
            }
            LOGGER.info("Repository_download. Success download file {}", filename);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existFileStorage(long userId, String filename) {
        List<FileRequest> files = getFilenamesFromStorage(userId);
        for (FileRequest fileRequest : files) {
            if (fileRequest.getFilename().equals(filename)) {
                return true;
            }
        }
        return false;
    }

    public String checkFilenameStorage(long userId, String filename) {
        String newFilename = filename;
        if (existFileStorage(userId, filename)) {
            LOGGER.warn("File {} exist", filename);
            int i = 0;
            while (true) {
                i++;
                String[] partsName = filename.split("\\.");
                newFilename = partsName[0] + "(" + i + ")." + partsName[1];
                if (!existFileStorage(userId, newFilename)) {
                    LOGGER.info("File saved as {}", newFilename);
                    break;
                }
            }
        }
        return newFilename;
    }

    public boolean existFileFolder(String path, String filename) {
        File folder = new File(path);
        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (!file.isDirectory()) {
                    if (filename.equals(file.getName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String checkFilenameFolder(String path, String filename) {
        String newFilename = filename;
        if (existFileFolder(path, filename)) {
            int i = 0;
            while (true) {
                i++;
                String[] partsName = filename.split("\\.");
                newFilename = partsName[0] + "(" + i + ")." + partsName[1];
                if (!existFileFolder(path, newFilename)) {
                    LOGGER.info("File saved as {}", newFilename);
                    break;
                }
            }
        }
        return newFilename;
    }

    public String getLocalDataPath(long userId) {
        UserData currentUser = userDataRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
        return currentUser.getDataPath();
    }

    public String getAbsolutePath(long userId) {
        return generalPath + File.separator + getLocalDataPath(userId);
    }

    public String getAbsolutePathFile(long userId, String filename) {
        return generalPath + File.separator + getLocalDataPath(userId) + File.separator + filename;
    }
}



