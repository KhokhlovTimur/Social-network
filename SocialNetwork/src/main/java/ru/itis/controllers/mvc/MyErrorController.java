package ru.itis.controllers.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.itis.dto.other.ExceptionDto;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class MyErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @RequestMapping("/error")
    public String error(HttpServletRequest request, Model model) {
        Integer code = (Integer) request.getAttribute("javax.servlet.error.status_code");

        model.addAttribute("code", code);
        String reason = HttpStatus.valueOf(code).toString();
        model.addAttribute("message", reason.substring(reason.indexOf(" ")));

        if (code.toString().startsWith("5")) {
            model.addAttribute("message", "Something went wrong");
        }

        return "error";
    }
}

