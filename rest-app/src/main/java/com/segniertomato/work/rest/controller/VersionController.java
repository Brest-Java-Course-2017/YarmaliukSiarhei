package com.segniertomato.work.rest.controller;


import com.segniertomato.work.rest.RestControllerUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin
@RestController
public class VersionController {

    private static final Logger LOGGER = LogManager.getLogger();

    // curl -v localhost:8088/api/version
    @GetMapping("/api/version")
    public String getVersion() {

        LOGGER.debug("getVersion()");
        return "\"" + RestControllerUtils.VERSION + "\"";
    }
}
