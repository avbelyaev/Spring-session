package ru.belyaev;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author anton.belyaev@bostongene.com
 */
@RestController
public class SessionController {

    @GetMapping("/hello")
    public String hello(HttpServletRequest servletRequest) {
        System.out.println("Bingo! Session id: " + servletRequest.getSession().getId());
        return "duck";
    }
}
