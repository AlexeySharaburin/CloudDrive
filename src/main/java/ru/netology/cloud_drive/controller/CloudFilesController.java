package ru.netology.cloud_drive.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloud_drive.exception.*;
import ru.netology.cloud_drive.model.ExceptionResponse;
import ru.netology.cloud_drive.model.FileRequest;
import ru.netology.cloud_drive.model.NewFilename;
import ru.netology.cloud_drive.service.CloudFilesService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/")
public class CloudFilesController {

    private final CloudFilesService cloudFilesService;

    public CloudFilesController(CloudFilesService cloudFilesService) {
        this.cloudFilesService = cloudFilesService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileRequest>> getAllFiles(@RequestHeader("auth-token") String authToken, @RequestParam("limit") int limit) {
        final List<FileRequest> files = cloudFilesService.getAllFiles(authToken, limit);
        files.forEach(System.out::println);
        return !files.isEmpty()
                ? new ResponseEntity<>(files, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/file")
    public ResponseEntity<String> uploadFileToServer(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String fileName, MultipartFile file) throws Exception {
        boolean successUpload = cloudFilesService.uploadFileToServer(authToken, fileName, file);
        return successUpload
                ? new ResponseEntity<>("Success upload " + fileName, HttpStatus.OK)
                : new ResponseEntity<>("Error input data", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/file")
    public ResponseEntity<String> deleteFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename) {
        boolean successDelete = cloudFilesService.deleteFile(authToken, filename);
        return successDelete
                ? new ResponseEntity<>("Success deleted " + filename, HttpStatus.OK)
                : new ResponseEntity<>("Error input data", HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/file")
    public ResponseEntity<String> renameFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String currentFilename, @RequestBody NewFilename filename) {
        String frontNewFilename = filename.getFilename();
        System.out.println("Controller_renameFiles. FileName: " + frontNewFilename);
        Boolean successRename = cloudFilesService.renameFile(authToken, currentFilename, frontNewFilename);
        return successRename
                ? new ResponseEntity<>("Success upload from" + currentFilename + " to " + frontNewFilename, HttpStatus.OK)
                : new ResponseEntity<>("Error input data", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/file")
    public ResponseEntity<String> downloadFileFromServer(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename) throws IOException {
        boolean successDownload = cloudFilesService.downloadFileFromServer(authToken, filename);
        return successDownload
                ? new ResponseEntity<>("Success downloaded " + filename, HttpStatus.OK)
                : new ResponseEntity<>("Error input data", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ErrorInputData.class)
    public ResponseEntity<ExceptionResponse> handleErrorInputData(ErrorInputData e) {
        String msgInput = "Error input data";
        System.out.println(msgInput);
        return new ResponseEntity<>(new ExceptionResponse(e.getMessage(), 400), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ErrorBadCredentials.class)
    public ResponseEntity<ExceptionResponse> handleErrorBadCredentials(ErrorBadCredentials e) {
        String msgInput = "Bad Credentials";
        System.out.println(msgInput);
        return new ResponseEntity<>(new ExceptionResponse(e.getMessage(), 400), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ErrorUnauthorized.class)
    public ResponseEntity<ExceptionResponse> handleErrorUnauthorized(ErrorUnauthorized e) {
        String msgTransfer = "Unauthorizated error";
        System.out.println(msgTransfer);
        return new ResponseEntity<>(new ExceptionResponse(e.getMessage(), 401), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ErrorDeleteFile.class)
    public ResponseEntity<ExceptionResponse> handleErrorDeleteFile(ErrorDeleteFile e) {
        String msgConfirmation = "Error delete file";
        System.out.println(msgConfirmation);
        return new ResponseEntity<>(new ExceptionResponse(e.getMessage(), 500), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ErrorUploadFile.class)
    public ResponseEntity<ExceptionResponse> handleErrorUploadFile(ErrorUploadFile e) {
        String msgConfirmation = "Error upload file";
        System.out.println(msgConfirmation);
        return new ResponseEntity<>(new ExceptionResponse(e.getMessage(), 500), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ErrorDownloadFile.class)
    public ResponseEntity<ExceptionResponse> handleErrorDownloadFile(ErrorDownloadFile e) {
        String msgConfirmation = "Error download file";
        System.out.println(msgConfirmation);
        return new ResponseEntity<>(new ExceptionResponse(e.getMessage(), 500), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ErrorGettingFileList.class)
    public ResponseEntity<ExceptionResponse> handleErrorGettingFileList(ErrorGettingFileList e) {
        String msgConfirmation = "Error getting file list";
        System.out.println(msgConfirmation);
        return new ResponseEntity<>(new ExceptionResponse(e.getMessage(), 500), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}













//    @PutMapping("/file")
//    public ResponseEntity<String> renameFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String currentFilename, @RequestBody NewFilename filename) {
//        String frontNewFilename = filename.getFilename();
//        System.out.println("Controller_renameFiles. FileName: " + frontNewFilename);
//        String newFilename = cloudFilesService.renameFile(authToken, currentFilename, frontNewFilename);
//        return (newFilename != null)
//                ? new ResponseEntity<>("Success upload from" + currentFilename + " to " + newFilename, HttpStatus.OK)
//                : new ResponseEntity<>("Error input data", HttpStatus.BAD_REQUEST);
//    }
