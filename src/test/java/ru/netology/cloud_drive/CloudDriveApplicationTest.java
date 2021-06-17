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
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ContextConfiguration(initializers = {CloudDriveApplicationTest.Initializer.class})
@Testcontainers
public class CloudDriveApplicationTest {

    private final String HOST = "http://localhost:";

    @Autowired
    TestRestTemplate restTemplate;

    static Network cloudNetwork = Network.newNetwork();

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres")
            .withDatabaseName("cloud_drive")
            .withUsername("alexey")
            .withPassword("123")
            .withNetwork(cloudNetwork)
            .withNetworkAliases("postgresContainer");

    @Container
    public static GenericContainer<?> backContainer = new GenericContainer<>("cloud_drive:5.0")
            .withNetwork(cloudNetwork)
            .withNetworkAliases("backContainer")
            .withExposedPorts(8080)
            .dependsOn(postgresContainer);

    @Container
    public static GenericContainer<?> frontContainer = new GenericContainer<>("cloud_front")
            .withNetwork(cloudNetwork)
            .withNetworkAliases("front_container")
            .withExposedPorts(8081)
            .dependsOn(backContainer);
//
//
//    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
//        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
//            TestPropertyValues.of(
//                    "spring.datasource.url=" + postgresContainer.getJdbcUrl(),
//                    "spring.datasource.username=" + postgresContainer.getUsername(),
//                    "spring.datasource.password=" + postgresContainer.getPassword()
//            ).applyTo(configurableApplicationContext.getEnvironment());
//        }
//    }


    @Test
    void contextPostgres() {
        var portDatabase = postgresContainer.getMappedPort(5432);
        System.out.println(postgresContainer.getJdbcUrl() + " " + postgresContainer.getDatabaseName() + " " + postgresContainer.getPassword());
        System.out.println(cloudNetwork.getId());
        System.out.println("Port CloudDrive Database: " + portDatabase);
        Assertions.assertTrue(postgresContainer.isRunning());
    }

    @Test
    void contextBack() {
        var portBack = backContainer.getMappedPort(8080);
        ResponseEntity<String> forEntityBackApp = restTemplate.getForEntity(HOST + portBack, String.class);
        System.out.println("\nPort Cloud Drive backend: " + portBack);
        System.out.println(forEntityBackApp.getBody());
        Assertions.assertTrue(backContainer.isRunning());
    }

    @Test
    void contextFront() {
        var portFront = frontContainer.getMappedPort(8081);
        ResponseEntity<String> forEntityFront = restTemplate.getForEntity(HOST + portFront, String.class);
        System.out.println("Port CloudDrive Frontend: " + portFront);
        System.out.println(forEntityFront.getBody());
        Assertions.assertTrue(frontContainer.isRunning());
    }

}








//    @Container
//    public static PostgreSQLContainer postgres_container = CloudPostgresContainer.getInstance();

//@ContextConfiguration(initializers = {CloudDriveApplicationTest.Initializer.class})

//    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext>
//    {public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
//        TestPropertyValues.of(
//                "spring.datasource.url=" + postgresContainer.getJdbcUrl(),
//                "spring.datasource.username=" + postgresContainer.getUsername(),
//                "spring.datasource.password=" + postgresContainer.getPassword()
//        ).applyTo(configurableApplicationContext.getEnvironment());
//    }}