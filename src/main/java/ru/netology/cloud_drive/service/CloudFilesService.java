package ru.netology.cloud_drive.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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
            System.out.println("File not found");
            throw new ErrorUnauthorized("Error input data");
        }
        if (cloudServiceRepository.uploadFile(file, userId, filename)) {
            System.out.println("Service_upload. Success upload " + filename);
            return true;
        }
        return false;
    }

    public Boolean deleteFile(String authToken, String filename) {
        long userId = getUserId(authToken);
        if (cloudServiceRepository.deleteFile(filename, userId)) {
            System.out.println("Service_delete. Success deleted " + filename);
            return true;
        }
        return false;
    }

    public Boolean renameFile(String authToken, String currentFilename, String newFilename) {
        long userId = getUserId(authToken);
        if (cloudServiceRepository.renameFile(currentFilename, newFilename, userId)) {
            System.out.println("Service_rename. Success rename file " + newFilename);
            return true;
        }
        return false;
    }

    public boolean downloadFileFromServer(String authToken, String filename) {
        long userId = getUserId(authToken);
        if (cloudServiceRepository.downloadFileFromServer(userId, filename)) {
            System.out.println("Service_download. Success download file " + filename);
            return true;
        }
        return false;
    }
}















//        String username = cloudAuthenticationService.tokenRepository.get(authToken.substring(7));
//        return cloudServiceRepository.getUserByUsername(username).getId();


//    public String renameFile(String authToken, String currentFilename, String newFilename) {
//        long userId = getUserId(authToken);
//        if (cloudServiceRepository.renameFile(currentFilename, newFilename, userId) != null) {
//            System.out.println("Service_rename. Success rename file " + newFilename);
//            return newFilename;
//        }
//        return null;
//    }

//    public Boolean uploadFileToServer(String authToken, String filename, MultipartFile file) throws IOException {
//        long userId = getUserId(authToken);
//        if (file.isEmpty()) {
//            System.out.println("File not found");
//            throw new ErrorUnauthorized("Error input data");
//        }
//        File uploadedFile = cloudServiceRepository.makeUploadedFile(userId, filename);
//        if (uploadFile(uploadedFile, file, userId, filename)) {
//            System.out.println("Service_upload. Success upload " + filename);
//            return true;
//        }
//        return false;
//    }

//    public boolean uploadFile(File uploadedFile, MultipartFile file, long userId, String filename) throws IOException {
//        byte[] bytes = file.getBytes();
//        try (var out = new FileOutputStream(uploadedFile);
//             var bos = new BufferedOutputStream(out)) {
//            bos.write(bytes, 0, bytes.length);
//            Storage newFile = Storage.builder()
//                    .filename(filename)
//                    .isExist(true)
//                    .date(new Date())
//                    .userId(userId)
//                    .fileSize(file.getSize())
//                    .build();
//            if (cloudServiceRepository.saveFile(newFile)) {
//                return true;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }


//    public boolean downloadFileFromServer(String authToken, String filename) {
//        long userId = getUserId(authToken);
//        if (cloudServiceRepository.downloadFileFromServer(userId, filename)) {
//            System.out.println("Service_download. Success download file " + filename);
//            return true;
//        }
//        return false;
//    }

//@Service
//public class CloudFilesService {
//
////    @Autowired
////    private AuthenticationManager authenticationManager;
////
////    @Autowired
////    private JwtTokenUtil jwtTokenUtil;
////
////    @Autowired
////    private JwtUserDetailsService jwtUserDetailsService;
//
//    private final CloudServiceRepository cloudServiceRepository;
//    private final CloudAuthenticationService cloudAuthenticationService;
//
//    public CloudFilesService(CloudServiceRepository cloudServiceRepository, CloudAuthenticationService cloudAuthenticationService) {
//        this.cloudServiceRepository = cloudServiceRepository;
//        this.cloudAuthenticationService = cloudAuthenticationService;
//    }
//
//    //    public CloudFilesService(CloudServiceRepository cloudServiceRepository) {
////        this.cloudServiceRepository = cloudServiceRepository;
////    }
//
////    public Map<String, String> tokenRepository = new ConcurrentHashMap<>();
////                                           /Users/alexey/Desktop/Netology Java/Java Diplom/cloud_service
////        private final String generalPath = "/Users/alexey/Desktop/Netology Java/Java Diplom/cloud_service/cloud_drive";
////    private final String generalPath = "/Users/alexey/Desktop/Cloud";
//
//    @Value("${general.path}")
//    private String generalPath;
//
//    public List<FileRequest> getAllFiles(String authToken, int limit) {
//
////        String token = authToken.substring(7);
////        String username = tokenRepository.get(token);
////        String username = getUsername(authToken);
////        long userId = cloudServiceRepository.getUser(username).getId();
//
//        UserData currentUser = getCurrentUser(authToken);
//        List<FileRequest> files = cloudServiceRepository.getFilenamesFromStorage(currentUser.getId());
//        return files.stream()
//                .limit(limit)
//                .sorted(Comparator.naturalOrder())
//                .collect(Collectors.toList());
//    }
//
//    public Boolean uploadFileToServer(String authToken, String filename, MultipartFile file) {
//
////        String token = authToken.substring(7);
////        System.out.println("Service_upload. Token: " + token);
////        String username = tokenRepository.get(token);
////        System.out.println("Service_upload. Username: " + username);
////        String username = getUsername(authToken);
////        UserData currentUser = cloudServiceRepository.getUser(username);
////        long currentUserId = currentUser.getId();
//
//        UserData currentUser = getCurrentUser(authToken);
//        if (!file.isEmpty()) {
//            try {
//                byte[] bytes = file.getBytes();
//
//                List<FileRequest> files = cloudServiceRepository.getFilenamesFromStorage(currentUser.getId());
//
//                if (existFile(files, filename)) {
//                    filename = filename + "(1)";
//                }
//                String dataPath = generalPath + File.separator + currentUser.getDataPath();
//                System.out.println("DataPath: " + dataPath);
//
////                    File directoryOfUser = new File(dataPath);
//                File directoryOfUser = new File(dataPath);
//                System.out.println("Absolute path: " + directoryOfUser.getAbsolutePath());
//
//                if (!directoryOfUser.exists()) {
//                    directoryOfUser.mkdirs();
//                }
//
//
////                    File uploadedFile = new File(directoryOfUser.getAbsolutePath() + File.separator + filename);
//                File uploadedFile = new File(dataPath + File.separator + filename);
//                System.out.println("File path:" + uploadedFile.getAbsolutePath());
//                try (var out = new FileOutputStream(uploadedFile);
//                     var bos = new BufferedOutputStream(out)) {
//                    bos.write(bytes, 0, bytes.length);
//
//                    Storage newFile = Storage.builder()
//                            .filename(filename)
//                            .isExist(true)
//                            .date(new Date())
//                            .userId(currentUser.getId())
//                            .fileSize(file.getSize())
//                            .build();
//                    if (cloudServiceRepository.saveFile(newFile)) {
//                        System.out.println("Service_upload. Success upload " + dataPath + File.separator + filename);
//                        return true;
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            System.out.println("File not found");
//            throw new ErrorUnauthorized("Error input data");
//        }
//
////            System.out.println("User not found");
////            throw new ErrorUnauthorized("Unauthorized error");
//
//        return false;
//    }
//
//    public Boolean deleteFile(String authToken, String filename) {
//
////        String token = authToken.substring(7);
////        System.out.println("Service_delete. Token: " + token);
////        String username = tokenRepository.get(token);
////        System.out.println("Service_delete. Username: " + username);
////        String username = getUsername(authToken);
////        UserData currentUser = cloudServiceRepository.getUser(username);
////        long currentUserId = currentUser.getId();
//
//        UserData currentUser = getCurrentUser(authToken);
//        List<FileRequest> files = cloudServiceRepository.getFilenamesFromStorage(currentUser.getId());
//
//        if (existFile(files, filename)) {
//            if (cloudServiceRepository.deleteFile(filename, currentUser.getId())) {
//                System.out.println("Service_delete. Success deleted " + filename);
//                return true;
//            } else {
//                System.out.println("File not found");
//                throw new ErrorDeleteFile("Error delete file");
//            }
////                System.out.println("User not found");
////                throw new ErrorUnauthorized("Unauthorized error");
//
//        }
//        return false;
//    }
//
//
//    public String renameFile(String authToken, String currentFilename, String newFilename) {
//
////        Scanner scanner = new Scanner(System.in);
////        String token = authToken.substring(7);
////        System.out.println("Service_rename. Token: " + token);
////        String username = tokenRepository.get(token);
////        System.out.println("Service_rename. Username: " + username);
////        String username = getUsername(authToken);
////        UserData currentUser = cloudServiceRepository.getUser(username);
////        long currentUserId = currentUser.getId();
//
//        UserData currentUser = getCurrentUser(authToken);
//        List<FileRequest> files = cloudServiceRepository.getFilenamesFromStorage(currentUser.getId());
//
//        if (existFile(files, currentFilename)) {
//
////                System.out.printf("Rename file %s?(y/n):", currentFilename);
////                String answer = scanner.next();
////                if (answer.equals("y")) {
////                    String newFilename;
////                    while (true) {
////                        System.out.print("Input new filename:");
////                        newFilename = scanner.next();
////                        if (!existFile(files, newFilename)) {
////                            break;
////                        } else {
////                            System.out.println("This filename already exist!");
////                            throw new ErrorUploadFile("Error upload file");
////                        }
////                    }
//            if (cloudServiceRepository.renameFile(currentFilename, newFilename, currentUser.getId(), currentUser.getUsername()) != null) {
//                System.out.println("Service_rename. Success rename " + newFilename);
//                return newFilename;
//            }
////                } else {
////                    System.out.println("File name remains unchanged");
////                    throw new ErrorUnauthorized("Error input data");
////                }
//        } else {
//            System.out.println("File not found");
//            throw new ErrorUploadFile("Error upload file");
//        }
////            System.out.println("User not found");
////            throw new ErrorUnauthorized("Unauthorized error");
//        return null;
//    }
//
//
//    public Boolean downloadFile(String authToken, String filename) throws MalformedURLException {
//
////        String token = authToken.substring(7);
////        System.out.println("Service_download. Token: " + token);
////        String username = tokenRepository.get(token);
////        System.out.println("Service_download. Username: " + username);
////        String username = getUsername(authToken);
////        UserData currentUser = cloudServiceRepository.getUser(username);
////        long currentUserId = currentUser.getId();
//
//        UserData currentUser = getCurrentUser(authToken);
//
//        List<FileRequest> files = cloudServiceRepository.getFilenamesFromStorage(currentUser.getId());
//
//        if (existFile(files, filename)) {
//
//            String dataPath = generalPath + File.separator + currentUser.getDataPath();
//
//            String pathFile = dataPath + File.separator + filename;
//
//            try (var fin = new FileInputStream(pathFile);
//                 var bis = new BufferedInputStream(fin, 1024);
//                 var out = new FileOutputStream(filename);
//                 var bos = new BufferedOutputStream(out, 1024)) {
//                int i;
//                while ((i = bis.read()) != -1) {
//                    bos.write(i);
//                }
//                System.out.println("Service_download. Success download " + filename);
//                return true;
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        } else {
//            System.out.println("File not found");
//            throw new ErrorUploadFile("Error download file");
//        }
////            System.out.println("User not found");
////            throw new ErrorUnauthorized("Unauthorized error");
//        return false;
//    }
//
//    public boolean existFile(List<FileRequest> files, String filename) {
//        for (FileRequest fileRequest : files) {
//            if (fileRequest.getFilename().equals(filename)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public UserData getCurrentUser(String authToken) {
//        String username = cloudAuthenticationService.tokenRepository.get(authToken.substring(7));
//        return cloudServiceRepository.getUser(username);
//    }
//
//    public long getUserId(String authToken) {
//        String username = cloudAuthenticationService.tokenRepository.get(authToken.substring(7));
//        return cloudServiceRepository.getUser(username).getId();
//    }
//}


//    public String createAuthenticationToken(AuthRequest authRequest) throws Exception {
//        System.out.println("Service_login. Username: " + authRequest.getLogin());
//        String username = authRequest.getLogin();
//        authenticate(username, authRequest.getPassword());
//        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
//        final String token = jwtTokenUtil.generateToken(userDetails);
//        System.out.println("Service_login. Token: " + token);
//        tokenRepository.put(token, username);
//        return token;
//    }


//    private void authenticate(String username, String password) throws Exception {
//        try {
//            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
//        } catch (DisabledException e) {
//            throw new ErrorUnauthorized("Unauthorized error");
//        } catch (BadCredentialsException e) {
//            throw new ErrorBadCredentials("Bad Credentials");
//        }
//    }
//
//    public Boolean removeToken(String authToken) {
//        String token = authToken.substring(7);
//        System.out.println("Service_logout. Token: " + token);
//        System.out.println("Service_logout");
//        return tokenRepository.remove(token) != null;
////        if (tokenRepository.remove(token) != null) {
////            return true;
////        }
////        return false;
//    }


//    public List<FileRequest> getAllFiles(String authToken, int limit) {
//        String token = authToken.substring(7);
//        System.out.println("Service_list. Token: " + token);
//        String username = tokenRepository.get(token);
//        System.out.println("Service_list. Username: " + username);
//        List<FileRequest> files = cloudServiceRepository.getAllFiles(username, limit);
//        if (!files.isEmpty()) {
//            return files;
//        }
//        return null;
//    }