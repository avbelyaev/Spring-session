package ru.belyaev;

import org.assertj.core.api.Assertions;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * @author avbelyaev
 */
@ActiveProfiles(profiles = "jdbc")
@ContextConfiguration(initializers = SessionIntegrationTests.Initializer.class)
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "liquibase.change-log=classpath:db/changelog/db.changelog-master.xml"
})
public class SessionIntegrationTests {

    private static final String SPRING_SESSION_TABLE_NAME = "spring_session_2_0_5";

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TestRestTemplate restTemplate;

    @ClassRule
    public static PostgreSQLContainer postgres = new PostgreSQLContainer();

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {

            EnvironmentTestUtils.addEnvironment("testcontainers", configurableApplicationContext.getEnvironment(),
                    "spring.datasource.url=" + postgres.getJdbcUrl(),
                    "spring.datasource.username=" + postgres.getUsername(),
                    "spring.datasource.password=" + postgres.getPassword()
            );
        }
    }

    @Test
    public void testSessionIsCreated() throws SQLException {
        // with basic auth
        TestRestTemplate testRestTemplate = this.restTemplate.withBasicAuth("admin", "qwerty");
        ResponseEntity<String> rsp = testRestTemplate.getForEntity("/hello", String.class);
        System.out.println("rsp: " + rsp.getStatusCodeValue());

        List<String> cookies = rsp.getHeaders().get("set-cookie");
        System.out.println("cookies: " + cookies);

        String sessionCookie = cookies.get(0).split(";")[0];

        // then
        assertThat(rsp.getStatusCodeValue()).isEqualTo(200);
        assertThat(rsp.getBody()).isNotBlank();
        assertThat(sessionCookie).isNotBlank();


        // and
        Connection connection = this.dataSource.getConnection();
        PreparedStatement preparedStatement = connection
                .prepareStatement("SELECT * FROM " + SPRING_SESSION_TABLE_NAME);
        ResultSet resultSet = preparedStatement.executeQuery();

        String sessionId = null;
        while (resultSet.next()) {
            sessionId = resultSet.getString("session_id");
        }
        System.out.println("sessionId in DB: " + sessionId);

        // cookie exists in db
        assertThat(sessionId).isNotNull();


        // cookie instead of auth
        HttpEntity<String> entity = new HttpEntity<>(new HttpHeaders() {{
            set("cookie", sessionCookie);
        }});
        rsp = this.restTemplate.exchange("/hello", HttpMethod.GET, entity, String.class);
        System.out.println("cookie rsp: " + rsp.getStatusCodeValue());

        assertThat(rsp.getStatusCodeValue()).isEqualTo(200);
        assertThat(rsp.getBody()).isNotBlank();
    }
}
