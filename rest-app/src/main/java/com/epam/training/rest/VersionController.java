package com.epam.training.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class VersionController {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String VERSION = "1.0";

    @GetMapping("/version")
    public String getVersion() {

        LOGGER.debug("getVersion()");
        return VERSION;
    }

    /*
    * This is actual for Spring Web.
    * We can pass in arguments a Model object.
    * The Model is essential a Map that will be
    * handled off (ru: передано) to the view.
    * Instead using Model, you can use a Map.
    *
    * Returned String type you can replace on
    * your customize type.
    * Rather than return a logical view name
    * and explicitly setting the model can
    * return a List<UserType> or UserType
    * object instead. In this case,
    * the value returned is put into the model,
    * and the model key is inferred (ru: выыеденный)
    * from ist type (example: returned List<User>,
    * inferred userList key name).
    *
    * */
}