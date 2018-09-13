package org.springframework.samples.petclinic.system;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class WelcomeController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping("/")
    public String welcome() {
        logger.info("Showing welcome page");
        return "welcome";
    }
}
