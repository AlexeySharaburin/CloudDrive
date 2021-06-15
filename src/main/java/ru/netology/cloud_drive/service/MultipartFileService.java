package ru.netology.cloud_drive.service;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.nio.file.Files;

public class MultipartFileService {

    public static MultipartFile convertFiletoMultiPart(String pathFile) throws IOException {
        File file = new File(pathFile);
        System.out.println("Path: " + file.toPath());
        FileItem fileItem;
        fileItem = new DiskFileItem("mainFile",
                Files.probeContentType(file.toPath()),
                false,
                file.getName(),
                (int) file.length(),
                file.getParentFile());
        try (InputStream input = new FileInputStream(file);
             OutputStream os = fileItem.getOutputStream()) {
            IOUtils.copy(input, os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileItem.getOutputStream();
        System.out.println("FileItem: " + fileItem.toString());
        return new CommonsMultipartFile(fileItem);
    }
}
