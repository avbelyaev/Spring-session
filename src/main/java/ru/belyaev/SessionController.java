package ru.belyaev;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

/**
 * @author anton.belyaev@bostongene.com
 */
@RestController
public class SessionController {

    @Autowired
    DataSource dataSource;

    @GetMapping("/hello")
    public String hello() {
        return "hello tacker";
    }
}
