package com.example.demo.DjavidMustafaev.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    // Все UI-маршруты отдаём React-приложению
    @GetMapping(value = {"/", "/dashboard"})
    public String reactApp() {
        return "forward:/react/index.html";
    }

}