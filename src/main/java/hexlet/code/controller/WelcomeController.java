package hexlet.code.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/welcome")
public class WelcomeController {

    @GetMapping
    public String welcome() {
        return "Welcome to Spring";
    }
}
