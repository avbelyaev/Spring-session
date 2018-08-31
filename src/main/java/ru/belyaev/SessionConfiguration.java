package ru.belyaev;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

/**
 * @author anton.belyaev@bostongene.com
 */
public class SessionConfiguration {

    @Profile("redis")
    @Configuration
    @EnableRedisHttpSession
    public class SessionConfig extends AbstractHttpSessionApplicationInitializer {

        @Bean
        public JedisConnectionFactory connectionFactory() {
            return new JedisConnectionFactory();
        }
    }

    @Profile("jdbc")
    @Configuration
    @EnableJdbcHttpSession
    public static class JdbcSessionConfig extends AbstractHttpSessionApplicationInitializer {

    }
}
