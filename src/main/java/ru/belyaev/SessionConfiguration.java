package ru.belyaev;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

/**
 * @author anton.belyaev@bostongene.com
 */
public class SessionConfiguration {

//    @Configuration
//    @EnableRedisHttpSession
//    public class SessionConfig extends AbstractHttpSessionApplicationInitializer {
//
//        @Bean
//        public JedisConnectionFactory connectionFactory() {
//            return new JedisConnectionFactory();
//        }
//    }

    /**
     * spring_session table should be created manually https://stackoverflow.com/a/37741000/4504720
     */
    @Configuration
    @EnableJdbcHttpSession
    public static class JdbcSessionConfig extends AbstractHttpSessionApplicationInitializer {

    }
}
