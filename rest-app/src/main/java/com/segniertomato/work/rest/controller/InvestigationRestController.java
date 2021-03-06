package com.segniertomato.work.rest.controller;


import com.fasterxml.jackson.annotation.JsonView;
import com.segniertomato.work.model.Investigation;
import com.segniertomato.work.profile.View;
import com.segniertomato.work.rest.RestControllerUtils;
import com.segniertomato.work.service.InvestigationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;


@CrossOrigin
@RestController
public class InvestigationRestController {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private InvestigationService investigationService;


    // curl -v localhost:8088/api/v1/investigations?limit=5\&offset=0
    @JsonView(View.Summary.class)
    @GetMapping(value = "/api/" + RestControllerUtils.VERSION + "/investigations", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Investigation> getInvestigations(@RequestParam(name = "limit", defaultValue = RestControllerUtils.DEFAULT_LIMIT) int limit,
                                                 @RequestParam(name = "offset", defaultValue = RestControllerUtils.DEFAULT_OFFSET) int offset) {

        LOGGER.debug("getInvestigations(int, int)");
        return investigationService.getAllInvestigations(offset, limit);
    }

    // If you want send time with offset encode '+' symbol like %2B
    // curl -v localhost:8088/api/v1/investigations/filter?startDate=2017-11-26T00:05:08Z\&endDate=2017-11-26T00:05:07%2B02:15\&limit=5\&offset=0
    @JsonView(View.Summary.class)
    @GetMapping(value = "/api/" + RestControllerUtils.VERSION + "/investigations/filter", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Investigation> getInvestigationsBetweenPeriod(@RequestParam(name = "startDate") OffsetDateTime startDate,
                                                              @RequestParam(name = "endDate") OffsetDateTime endDate,
                                                              @RequestParam(name = "limit", defaultValue = RestControllerUtils.DEFAULT_LIMIT) int limit,
                                                              @RequestParam(name = "offset", defaultValue = RestControllerUtils.DEFAULT_OFFSET) int offset) {

        LOGGER.debug("getInvestigationsBetweenPeriod(Offset, Offset, int, int)");
        return investigationService.getInvestigationsBetweenPeriod(startDate, endDate, offset, limit);
    }

    // curl -v localhost:8088/api/v1/investigations/1
    @JsonView(View.Summary.class)
    @GetMapping(value = "/api/" + RestControllerUtils.VERSION + "/investigations/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Investigation getInvestigationById(@PathVariable int id) {

        LOGGER.debug("getInvestigationById(int)");
        return investigationService.getInvestigationById(id);
    }

    // curl -v localhost:8088/api/v1/investigations/employee/1?limit=5\&offset=0
    @JsonView(View.Summary.class)
    @GetMapping(value = "/api/" + RestControllerUtils.VERSION + "/investigations/employee/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Investigation> getEmployeeInvestigations(@PathVariable int id,
                                                         @RequestParam(name = "limit", defaultValue = RestControllerUtils.DEFAULT_LIMIT) int limit,
                                                         @RequestParam(name = "offset", defaultValue = RestControllerUtils.DEFAULT_OFFSET) int offset) {

        LOGGER.debug("getEmployeeInvestigations(int, int, int)");
        return investigationService.getEmployeeInvestigations(id, offset, limit);
    }

    // curl -X POST -H 'Content-Type: application/json'
    // -d '{"investigationId":null,"number":null,"title":"Toy thief","description":"Someone stole a rabbit toy.",
    // "startInvestigationDate":"2017-05-26T02:00:15+03:00", "endInvestigationDate":null}' -v localhost:8088/api/v1/investigations
    @PostMapping(value = "/api/" + RestControllerUtils.VERSION + "/investigations", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public int addInvestigation(@RequestBody Investigation investigation) {

        LOGGER.debug("addInvestigation(Investigation)");
        return investigationService.addInvestigation(investigation);
    }

    // curl -X POST -H 'Content-Type: application/json' -d '[1,3]' -v localhost:8088/api/v1/investigations/1/staff
    @PostMapping(value = "/api/" + RestControllerUtils.VERSION + "/investigations/{id}/staff", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void addInvolvedStaff2Investigation(@PathVariable int id, @RequestBody List<Integer> employeesId) {

        LOGGER.debug("addInvolvedStaff2Investigation(int, List<Integer>)");
        investigationService.addInvolvedStaff2Investigation(id, employeesId);
    }

    // curl -X PUT -H 'Content-Type: application/json'
    // -d '{"investigationId":1,"number":null,"title":"Some title","description":"Some description",
    // "startInvestigationDate":"2011-05-26T15:56:45+03:00","endInvestigationDate":"2013-02-29T20:01:23Z"}',
    // "involvedStaff":[{"employeeId":2,"name":"Some name","age":"1965-05-16","startWorkingDate":"1980-04-16"}]
    // -v localhost:8088/api/v1/investigations
    @PutMapping(value = "/api/" + RestControllerUtils.VERSION + "/investigations", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateInvestigation(@RequestBody Investigation investigation) {

        LOGGER.debug("updateInvestigation(Investigation)");
        investigationService.updateInvestigation(investigation);
    }

    // curl -X PUT -H 'Content-Type: application/json' -d '[1,4]' -v localhost:8088/api/v1/investigations/1/staff
    @PutMapping(value = "/api/" + RestControllerUtils.VERSION + "/investigations/{id}/staff", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateInvolvedStaffInInvestigation(@PathVariable int id, @RequestBody List<Integer> employeesId) {

        LOGGER.debug("updateInvolvedStaffInInvestigation(int, List<Integer>)");
        investigationService.updateInvolvedStaffInInvestigation(id, employeesId);
    }

    // curl -X DELETE -v localhost:8088/api/v1/investigations/1
    @DeleteMapping(value = "/api/" + RestControllerUtils.VERSION + "/investigations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInvestigationById(@PathVariable int id) {

        LOGGER.debug("deleteInvestigationById(int)");
        investigationService.deleteInvestigationById(id);
    }
}
