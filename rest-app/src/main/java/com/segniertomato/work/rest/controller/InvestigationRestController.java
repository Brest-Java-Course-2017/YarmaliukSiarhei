package com.segniertomato.work.rest.controller;


import com.segniertomato.work.model.Investigation;
import com.segniertomato.work.service.InvestigationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;


@RestController
public class InvestigationRestController {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String DEFAULT_LIMIT = "10";
    private static final String DEFAULT_OFFSET = "0";

    private static final String VERSION = "v1";

    @Autowired
    private InvestigationService investigationService;

    //  It is must look like /v1/investigations?limit=25&offset=0
//    Get user data like available investigations count and current page
//    Use default values for limit and offset

    @GetMapping(value = "/api/" + VERSION + "/investigations", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Investigation> getInvestigations(@RequestParam(name = "limit", defaultValue = DEFAULT_LIMIT) int limit,
                                                 @RequestParam(name = "offset", defaultValue = DEFAULT_OFFSET) int offset) {

        LOGGER.debug("getInvestigations(int, int)");
        return investigationService.getAllInvestigations(offset, limit);
    }

    @GetMapping(value = "api/" + VERSION + "/investigations/filter")
    public List<Investigation> getInvestigationsBetweenPeriod(@RequestParam(name = "startDate") OffsetDateTime startDate,
                                                              @RequestParam(name = "endDate") OffsetDateTime endDate,
                                                              @RequestParam(name = "limit", defaultValue = DEFAULT_LIMIT) int limit,
                                                              @RequestParam(name = "offset", defaultValue = DEFAULT_OFFSET) int offset) {

        LOGGER.debug("getInvestigationsBetweenPeriod(Offset, Offset, int, int)");
        return investigationService.getInvestigationsBetweenPeriod(startDate, endDate, offset, limit);
    }

    @GetMapping(value = "/api/" + VERSION + "/investigations/{id}")
    public Investigation getInvestigationById(@PathVariable(name = "id") int id) {

        LOGGER.debug("getInvestigationById(int)");

        return investigationService.getInvestigationById(id);
    }


    @GetMapping(value = "api/" + VERSION + "/investigations/employee/{id}")
    public List<Investigation> getEmployeeInvestigations(@PathVariable int id,
                                                         @RequestParam(name = "limit", defaultValue = DEFAULT_LIMIT) int limit,
                                                         @RequestParam(name = "offset", defaultValue = DEFAULT_OFFSET) int offset) {

        LOGGER.debug("getEmployeeInvestigations(int, int, int)");
        return investigationService.getEmployeeInvestigations(id, offset, limit);
    }

    @PostMapping(value = "api/" + VERSION + "/investigations", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public int addInvestigation(@RequestBody Investigation investigation) {

        LOGGER.debug("addInvestigation(Investigation)");
        return investigationService.addInvestigation(investigation);
    }

    @PostMapping(value = "api/" + VERSION + "/investigations/{id}/staff", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void addInvolvedStaff2Investigation(@PathVariable int id, @RequestBody List<Integer> employeesId) {

        LOGGER.debug("addInvolvedStaff2Investigation(int, List<Integer>)");
        investigationService.addInvolvedStaff2Investigation(id, employeesId);
    }

    @PutMapping(value = "api/" + VERSION + "/investigations", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateInvestigation(@RequestBody Investigation investigation) {

        LOGGER.debug("updateInvestigation(Investigation)");
        investigationService.updateInvestigation(investigation);
    }

    @PutMapping(value = "api/" + VERSION + "/investigations/{id}/staff", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateInvolvedStaffInInvestigation(@PathVariable int id, @RequestBody List<Integer> employeesId) {

        LOGGER.debug("updateInvolvedStaffInInvestigation(int, List<Integer>)");
        investigationService.updateInvolvedStaffInInvestigation(id, employeesId);
    }

    @DeleteMapping(value = "/api/" + VERSION + "/investigations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInvestigationById(@PathVariable int id) {

        LOGGER.debug("deleteInvestigationById(int)");
        investigationService.deleteInvestigationById(id);
    }


}