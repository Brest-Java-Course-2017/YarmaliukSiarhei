package com.segniertomato.work.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;


@ControllerAdvice(basePackages = {"com.segniertomato.work.rest"})
public class RestErrorHandler {

    private static final Logger LOGGER = LogManager.getLogger();

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handlerDataAccessException(DataAccessException ex) {

        LOGGER.debug("handlerDataAccessException(DataAccessException) - DataAccessException: {}" + ex);
        return "Error message: DataAccessException - " + ex.getLocalizedMessage();
    }


    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ResponseBody
    public String handlerIllegalArgumentException(IllegalArgumentException ex) {

        LOGGER.debug("handlerIllegalArgumentException(IllegalArgumentException) - IllegalArgumentException: {}" + ex);
        return "Error message: IllegalArgumentException - " + ex.getLocalizedMessage();
    }
}
