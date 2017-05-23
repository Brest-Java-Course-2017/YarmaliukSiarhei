package com.segniertomato.work.rest.controller;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class VersionController {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String VERSION = "1.0";

    @GetMapping("/api/version")
    public String getVersion() {

        LOGGER.debug("getVersion()");
        return VERSION;
    }
}
