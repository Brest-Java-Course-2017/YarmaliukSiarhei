package com.epam.training.web_app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class HelloController {

    @GetMapping(value = "/hello")
    public String hello(@RequestParam(value = "name", required = false, defaultValue = "Word!") String name, Model model) {

        model.addAttribute("name", name);
        return "hello";
    }
}