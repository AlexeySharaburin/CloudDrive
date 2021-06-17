package ru.netology.cloud_drive;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class CloudDriveApplicationTest {

    private final String HOST = "http://localhost:";

    @Autowired
    TestRestTemplate restTemplate;

    static Network cloudNetwork = Network.newNetwork();

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres")
            .withNetwork(cloudNetwork)
            .withNetworkAliases("postgresDB")
            .withDatabaseName("cloud_drive")
            .withUsername("alexey")
            .withPassword("123");

    @Container
    public static GenericContainer<?> backContainer = new GenericContainer<>("cloud_drive:5.0")
            .withNetwork(cloudNetwork)
            .withExposedPorts(8080)
            .withEnv(Map.of("jdbc:postgresql://localhost:5432/cloud_drive", "jdbc:postgresql://postgresDB:5432/cloud_drive"))
            .dependsOn(postgresContainer);


    @Test
    void contextPostgres() {
        var portDatabase = postgresContainer.getMappedPort(5432);
        System.out.println(postgresContainer.getJdbcUrl() + " " + postgresContainer.getDatabaseName() + " " + postgresContainer.getPassword());
        System.out.println("Network ID: " + cloudNetwork.getId());
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

}





//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
////@ContextConfiguration(initializers = {CloudDriveApplicationTest.Initializer.class})
//@Testcontainers
//public class CloudDriveApplicationTest {
//
//    private final String HOST = "http://localhost:";
//
//    @Autowired
//    TestRestTemplate restTemplate;
//
//    static Network cloudNetwork = Network.newNetwork();
//
//    @Container
//    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres")
//            .withNetwork(cloudNetwork)
//            .withNetworkAliases("postgresDB")
//            .withDatabaseName("cloud_drive")
//            .withUsername("alexey")
//            .withPassword("123")
////            .withExposedPorts(5432)
//            ;
////
//
//    @Container
//    public static GenericContainer<?> backContainer = new GenericContainer<>("cloud_drive:5.0")
//            .withNetwork(cloudNetwork)
////            .withNetworkAliases("backContainer")
//            .withExposedPorts(8080)
//            .withEnv(Map.of("jdbc:postgresql://localhost:5432/cloud_drive", "jdbc:postgresql://postgresDB:5432/cloud_drive"))
//            .dependsOn(postgresContainer);
//
////    @Container
////    public static GenericContainer<?> frontContainer = new GenericContainer<>("cloud_front")
////            .withNetwork(cloudNetwork)
////            .withNetworkAliases("front_container")
////            .withExposedPorts(8080);
////            .dependsOn(backContainer);
////
////
////    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
////        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
////            TestPropertyValues.of(
////                    "spring.datasource.url=" + postgresContainer.getJdbcUrl(),
////                    "spring.datasource.username=" + postgresContainer.getUsername(),
////                    "spring.datasource.password=" + postgresContainer.getPassword()
////            ).applyTo(configurableApplicationContext.getEnvironment());
////        }
////    }
//
//
//    @Test
//    void contextPostgres() {
////        var portDatabase = postgresContainer.getMappedPort(5432);
////        System.out.println(postgresContainer.getJdbcUrl() + " " + postgresContainer.getDatabaseName() + " " + postgresContainer.getPassword());
////        System.out.println("Network ID: " + cloudNetwork.getId());
////        System.out.println("Port CloudDrive Database: " + portDatabase);
//        Assertions.assertTrue(postgresContainer.isRunning());
//    }
//
//    @Test
//    void contextBack() {
//        var portBack = backContainer.getMappedPort(8080);
//        ResponseEntity<String> forEntityBackApp = restTemplate.getForEntity(HOST + portBack, String.class);
//        System.out.println("\nPort Cloud Drive backend: " + portBack);
//        System.out.println(forEntityBackApp.getBody());
//        Assertions.assertTrue(backContainer.isRunning());
//    }
//
////    @Test
////    void contextFront() {
////        var portFront = frontContainer.getMappedPort(8080);
////        ResponseEntity<String> forEntityFront = restTemplate.getForEntity(HOST + portFront, String.class);
////        System.out.println("Port CloudDrive Frontend: " + portFront);
////        System.out.println("Network ID: " + cloudNetwork.getId());
////        System.out.println(forEntityFront.getBody());
////        Assertions.assertTrue(frontContainer.isRunning());
////    }
//
//}


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