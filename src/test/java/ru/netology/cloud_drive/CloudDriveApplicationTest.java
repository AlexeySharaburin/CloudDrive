package ru.netology.cloud_drive;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {CloudDriveApplicationTest.Initializer.class})
@Testcontainers
public class CloudDriveApplicationTest {

    private final String HOST = "http://localhost:";

    @Autowired
    TestRestTemplate restTemplate;

    static Network cloudNetwork = Network.newNetwork();

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres")
            .withNetwork(cloudNetwork)
            .withNetworkAliases("postgres_db")
            .withDatabaseName("cloud_drive")
            .withUsername("alexey")
            .withPassword("123");

    @Container
    public static GenericContainer<?> backContainer = new GenericContainer<>("cloud_drive_app:latest")
            .withNetwork(cloudNetwork)
            .withExposedPorts(8080)
            .withEnv(Map.of("SPRING_DATASOURCE_URL", "jdbc:postgresql://postgres_db:5432/cloud_drive"))
            .dependsOn(postgresContainer);

    @Test
    void contextPostgres() {
        var portDatabase = postgresContainer.getMappedPort(5432);
        System.out.println(postgresContainer.getJdbcUrl() + " " + postgresContainer.getDatabaseName() + " " + postgresContainer.getPassword());
        System.out.println("Network ID: " + cloudNetwork.getId());
        System.out.println("CloudDrive Database -> port: " + portDatabase);
        Assertions.assertTrue(postgresContainer.isRunning());
    }

    @Container
    public static GenericContainer<?> frontContainer = new GenericContainer<>("cloud_drive_front:latest")
            .withNetwork(cloudNetwork)
            .withNetworkAliases("front_container")
            .withExposedPorts(8080)
            .dependsOn(backContainer);

    @Test
    void contextBack() {
        var portBack = backContainer.getMappedPort(8080);
        ResponseEntity<String> forEntityBackApp = restTemplate.getForEntity(HOST + portBack, String.class);
        System.out.println("Network ID: " + cloudNetwork.getId());
        System.out.println("CloudDrive Backend -> port: " + portBack);
        System.out.println(forEntityBackApp.getBody());
        Assertions.assertTrue(backContainer.isRunning());
    }

    @Test
    void contextFront() {
        var portFront = frontContainer.getMappedPort(8080);
        ResponseEntity<String> forEntityFront = restTemplate.getForEntity(HOST + portFront, String.class);
        System.out.println("Network ID: " + cloudNetwork.getId());
        System.out.println("CloudDrive Frontend -> port: " + portFront);
        System.out.println(forEntityFront.getBody());
        Assertions.assertTrue(frontContainer.isRunning());
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgresContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgresContainer.getUsername(),
                    "spring.datasource.password=" + postgresContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
