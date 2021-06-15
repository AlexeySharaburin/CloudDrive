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
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class CloudDriveApplicationTest {

    private final String HOST = "http://localhost:";

    @Autowired
    TestRestTemplate restTemplate;

    static Network cloudNetwork = Network.newNetwork();

    @Container
    public static GenericContainer<?> front_container = new GenericContainer<>("cloud_front")
            .withNetwork(cloudNetwork)
            .withNetworkAliases("front_container")
            .withExposedPorts(8080);


    @Container
    public static PostgreSQLContainer<?> postgres_container = new PostgreSQLContainer<>("postgres")
            .withDatabaseName("cloud_drive")
            .withUsername("alexey")
            .withPassword("123")
            .withNetwork(cloudNetwork)
            .withNetworkAliases("postgres_container");
//            .waitingFor(Wait.forListeningPort());


    @Test
    void context_front() {
        var port_front = front_container.getMappedPort(8080);
        ResponseEntity<String> forEntity_front_container = restTemplate.getForEntity(HOST + port_front, String.class);
        System.out.println("Port CloudDrive Frontend: " + port_front);
        System.out.println(forEntity_front_container.getBody());
        Assertions.assertTrue(front_container.isRunning());
    }

    @Test
    void context_postgres() {
        var port_db = postgres_container.getMappedPort(5432);
        System.out.println(postgres_container.getJdbcUrl() + " " + postgres_container.getDatabaseName() + " " + postgres_container.getPassword());
        System.out.println(cloudNetwork.getId());
        System.out.println("Port CloudDrive Database: " + port_db);
        Assertions.assertTrue(postgres_container.isRunning());
    }


//    @Container
//    public static GenericContainer<?> back_container = new GenericContainer<>("cloud_drive:4.0")
//            .withNetwork(cloudNetwork)
//            .withNetworkAliases("back_container")
//            .withExposedPorts(8080)
//            .waitingFor(Wait.forListeningPort());
//
//    @Test
//    void context_back() {
//        var port_back = back_container.getMappedPort(8080);
//        ResponseEntity<String> forEntity_back_app = restTemplate.getForEntity(HOST + port_back, String.class);
//        System.out.println("\nPort Cloud Drive backend: " + port_back);
//        System.out.println(forEntity_back_app.getBody());
//        Assertions.assertTrue(back_container.isRunning());
//    }

}


//    @Container
//    public static PostgreSQLContainer postgres_container = CloudPostgresContainer.getInstance();

//@ContextConfiguration(initializers = {CloudDriveApplicationTest.Initializer.class})

//    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext>
//    {public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
//        TestPropertyValues.of(
//                "spring.datasource.url=" + postgres_container.getJdbcUrl(),
//                "spring.datasource.username=" + postgres_container.getUsername(),
//                "spring.datasource.password=" + postgres_container.getPassword()
//        ).applyTo(configurableApplicationContext.getEnvironment());
//    }}