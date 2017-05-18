package com.epam.training.web_app.controllers;

import com.epam.training.client.rest.api.UsersConsumer;
import com.epam.training.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class UsersController {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    UsersConsumer mUsersConsumer;


    @GetMapping(value = "/")
    public String defaultPageRedirect() {
        return "redirect:users";
    }

    @GetMapping(value = "/users")
    public String users(Model model) {

        LOGGER.debug("/users page");

        model.addAttribute("usersList", mUsersConsumer.getAllUsers());
        return "users";
    }

    @GetMapping(value = "/user")
    public String editUser(@RequestParam("id") Integer userId, Model model) {

        LOGGER.debug("/user({})", userId);

        User user = mUsersConsumer.getUserById(userId);
        model.addAttribute("user", user);

        return "user";
    }
}
