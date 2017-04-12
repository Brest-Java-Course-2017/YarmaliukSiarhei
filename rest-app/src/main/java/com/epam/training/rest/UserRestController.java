package com.epam.training.rest;


import com.epam.training.model.User;
import com.epam.training.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
* This annotation allows doesn't use a @ResponseBody annotations
* and when we use a @RestController annotation in all our @RequestMapping methods
* returned data will be converted by a MessageConverter implementation by implicitly.
* If doesn't use a @RestController annotation than returned data
* will be processed by a ViewResolver implementation, which will be
* return it in a response like some View.
*
* */
@RestController
public class UserRestController {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private UserService mUserService;

    //    curl -v localhost:8080/users
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<User> getUsers() {

        LOGGER.debug("getUsers()");
        return mUserService.getAllUsers();
    }

    //    curl -H "Content-Type: application/json" -X POST -d '{"login":"someLogin","password":"somePassword8*"}' -v localhost:8080/user
    @PostMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Integer addUser(@RequestBody User user) {

        LOGGER.debug("addUser(User)");
        return mUserService.addUser(user);
    }


    //    curl -X PUT -v localhost:8080/user/2/newLogin/new*Password7/someDescription
    @PutMapping(value = "/user/{id}/{login}/{password}/{description}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateUser(@PathVariable(value = "id") int id, @PathVariable(value = "description") String desc,
                           @PathVariable(value = "login") String login, @PathVariable String password) {

        LOGGER.debug("updateUser(int, String, String, String)");
        mUserService.updateUser(new User(id, login, password, desc));
    }

    //    curl -v localhost:8080/user/testUserLogin1
    @GetMapping(value = "/user/login/{login}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.FOUND)
    @ResponseBody
    public User getUserByLogin(@PathVariable String login) {

        LOGGER.debug("getUserByLogin(String) - login is: {}", login);
        return mUserService.getUserByLogin(login);
    }

    //    curl -v localhost:8080/user/1
    @GetMapping(value = "/user/id/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.FOUND)
    public User getUserById(@PathVariable("userId") int id) {

        LOGGER.debug("getUserById(int) - user id is: {}", id);
        return mUserService.getUserById(id);
    }

/*
* Instead returning User object, we can return
* a ResponseEntity<User> object.
* Difference between the ResponseEntity<User> object
* and the User object that ResponseEntity object allows
* set a headers to our response.
*
* */

/*
* Normally, when a handler method return a Java object (anything other than a String
* or implementation of View), that object ends up in the model for rendering in the view.
* But if you are going to employ message conversion, you need to tell Spring to skip
* the normal model/view flow and use a message converter instead.
* Use a @ResponseBody annotation for annotate the controller method, which must to use
* a message converter.
*
* */

/*
* Accept header using for specifies what media type client will accept.
*
* */

/*
* By default Jackson JSON libraries use reflection in producing the JSON resource
* representation from the returned object.
* But if you change/refactor the Java type by adding, removing, or renaming properties,
* then the produced JSON will be changed as well, that can break clients.
*
* But you can prevent this using a Jackson's mapping annotations on your used Java types.
*
* */

/*
* @ResponseEntity allows set response's headers, body and returned status.
*
* For setting response's headers use a HttpHeader class which implements a MultiValueMap interface.
* For URI building use a UriComponentsBuilder class, which you can create explicitly
* in a controller's method body or you can passing a UriComponentsBuilder object
* like a one of arguments.
* UriComponentsBuilder can given such information like the host, port and servlet content.
*
* */
}
