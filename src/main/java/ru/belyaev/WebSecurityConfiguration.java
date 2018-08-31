package ru.belyaev;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author anton.belyaev@bostongene.com
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("admin").password("qwerty").roles("ADMIN")
//                .and()
//                .withUser("superadmin").password("qazxsw").roles("ADMIN", "SUPERADMIN");
//    }

    /**
     * Spring Boot 2.0 requires PasswordEncoder instance. but it can also be replaced with the following
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        auth.inMemoryAuthentication()
                .withUser(User
                        .withUsername("admin")
                        .password("qwerty")
                        .roles("ADMIN")
                        .passwordEncoder(encoder::encode))
                .withUser(User
                        .withUsername("superadmin")
                        .password("qazxsw")
                        .roles("ADMIN", "SUPERADMIN")
                        .passwordEncoder(encoder::encode));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().and()
                .authorizeRequests().antMatchers("/hello").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                // browser could be sharing session across tab
                // so accessing app from another tab may not create new session
                .sessionManagement().maximumSessions(2).maxSessionsPreventsLogin(true);
    }
}
