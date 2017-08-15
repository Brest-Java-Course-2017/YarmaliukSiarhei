package com.segniertomato.work.rest.controller;


import com.fasterxml.jackson.annotation.JsonView;
import com.segniertomato.work.model.Employee;
import com.segniertomato.work.model.Pair;
import com.segniertomato.work.profile.View;
import com.segniertomato.work.rest.RestControllerUtils;
import com.segniertomato.work.service.EmployeeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin
@RestController
public class EmployeeRestController {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private EmployeeService employeeService;


    // curl -v localhost:8088/api/v1/employees?limit=5\&offset=0
    @JsonView(View.Summary.class)
    @GetMapping(value = "/api/" + RestControllerUtils.VERSION + "/employees", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Employee> getEmployees(@RequestParam(name = "limit", defaultValue = RestControllerUtils.DEFAULT_LIMIT) int limit,
                                       @RequestParam(name = "offset", defaultValue = RestControllerUtils.DEFAULT_OFFSET) int offset) {
        LOGGER.debug("getEmployees(int, int)");
        return employeeService.getAllEmployees(offset, limit);
    }

    // curl -v localhost:8088/api/v1/employees/investigation/1?limit=5\&offset=0
    @JsonView(View.Summary.class)
    @GetMapping(value = "/api/" + RestControllerUtils.VERSION + "/employees/investigation/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Employee> getInvolvedEmployeesInInvestigation(@PathVariable int id,
                                                              @RequestParam(name = "limit", defaultValue = RestControllerUtils.DEFAULT_LIMIT) int limit,
                                                              @RequestParam(name = "offset", defaultValue = RestControllerUtils.DEFAULT_OFFSET) int offset) {
        LOGGER.debug("getInvolvedEmployeesInInvestigation(int, int, int)");
        return employeeService.getInvolvedEmployeesInInvestigation(id, offset, limit);
    }

    //curl -v localhost:8088/api/v1/employees/1
    @JsonView(View.Summary.class)
    @GetMapping(value = "/api/" + RestControllerUtils.VERSION + "/employees/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Employee getEmployeeById(@PathVariable int id) {

        LOGGER.debug("getEmployeeById(id) - id is: {}", id);
        return employeeService.getEmployeeById(id);
    }

    //curl -X POST -H 'Content-Type: application/json'
    // -d '{"employeeId":null,"name":"Eric Dickman","age":"1990-11-26","startWorkingDate":"2005-12-16"}' -v localhost:8088/api/v1/employees
    @PostMapping(value = "/api/" + RestControllerUtils.VERSION + "/employees", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public int addEmployee(@RequestBody Employee employee) {

        LOGGER.debug("addEmployee(Employee)");
        return employeeService.addEmployee(employee);
    }

    //curl -X POST -H 'Content-Type: application/json' -d '[1,2,3]' -v localhost:8088/api/v1/employees/1/investigations
    @PostMapping(value = "/api/" + RestControllerUtils.VERSION + "/employees/{id}/investigations", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void addInvestigations2Employee(@PathVariable int id, @RequestBody List<Integer> investigationsId) {

        LOGGER.debug("addInvestigations2Employee(int, List<Integer>)");
        employeeService.addInvestigations2Employee(id, investigationsId);
    }

    //curl -X PUT -H 'Content-Type:application/json'
    // -d '{"employeeId":2,"name":"Eric Dickman","age":"1990-11-26","startWorkingDate":"2005-12-16"}' -v localhost:8088/api/v1/employees
    @PutMapping(value = "/api/" + RestControllerUtils.VERSION + "/employees", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateEmployee(@RequestBody Employee employee) {

        LOGGER.debug("updateEmployee(Employee)");
        employeeService.updateEmployee(employee);
    }

    //curl -X PUT -H 'Content-Type:application/json' -d '[1,2,3]' localhost:8088/api/v1/employees/1/investigations
    @PutMapping(value = "/api/" + RestControllerUtils.VERSION + "/employees/{id}/investigations", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateInvestigationsInEmployee(@PathVariable int id, @RequestBody List<Integer> investigationsId) {

        LOGGER.debug("updateInvestigationsInEmployee(int, List<Integer>)");
        employeeService.updateEmployeeInvestigations(id, investigationsId);
    }

    //curl -X DELETE -v localhost:8088/api/v1/employees/1
    @DeleteMapping(value = "/api/" + RestControllerUtils.VERSION + "/employees/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployeeById(@PathVariable int id) {

        LOGGER.debug("deleteEmployeeById(int) - id is: {}", id);
        employeeService.deleteEmployeeById(id);
    }

    //curl -v localhost:8088/api/v1/employees/rating?limit=5\&offset=0
    @GetMapping(value = "/api/" + RestControllerUtils.VERSION + "/employees/rating")
    public List<Pair<Integer, Integer>> getEmployeesRating(@RequestParam(name = "limit", defaultValue = RestControllerUtils.DEFAULT_LIMIT) int limit,
                                                           @RequestParam(name = "offset", defaultValue = RestControllerUtils.DEFAULT_OFFSET) int offset) {

        LOGGER.debug("getEmployeesRating(int, int)");
        return employeeService.getEmployeesRating(offset, limit);
    }
}