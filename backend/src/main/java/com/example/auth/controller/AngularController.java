package com.example.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AngularController {

    @RequestMapping(value = {"/"})
    public String index() {
        return "forward:/index.html";
    }

    @RequestMapping(value = {"/login", "/signup", "/dashboard"})
    public String app() {
        return "forward:/index.html";
    }
}