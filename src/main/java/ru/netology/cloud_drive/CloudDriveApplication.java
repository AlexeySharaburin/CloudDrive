package ru.netology.cloud_drive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CloudDriveApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudDriveApplication.class, args);
        System.out.println("Willkomen zum CloudDrive!");
        System.out.println("CloudDrive is running...");
    }

}
