package ru.itis.controllers.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class UsersController {

    @GetMapping("/login")
    public String getAuthPage(){
        return "/html/authenticationPage.html";
    }
}
