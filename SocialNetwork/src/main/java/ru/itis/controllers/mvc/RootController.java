package ru.itis.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = {"/", "/app"})
public class RootController {
    @GetMapping
    public String redirectToApp(){
        return "redirect:/app/login";
    }
}
