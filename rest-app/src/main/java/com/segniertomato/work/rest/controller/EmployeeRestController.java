package com.segniertomato.work.rest.controller;

import com.segniertomato.work.model.Employee;
import com.segniertomato.work.model.Investigation;
import com.segniertomato.work.model.Pair;
import com.segniertomato.work.service.EmployeeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class EmployeeRestController {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String DEFAULT_LIMIT = "10";
    private static final String DEFAULT_OFFSET = "0";

    private static final String VERSION = "v1";

    @Autowired
    private EmployeeService employeeService;

    @GetMapping(value = "/api/" + VERSION + "/employees", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Employee> getEmployees(@RequestParam(name = "limit", defaultValue = DEFAULT_LIMIT) int limit,
                                       @RequestParam(name = "offset", defaultValue = DEFAULT_OFFSET) int offset) {

        LOGGER.debug("getEmployees()");
        return employeeService.getAllEmployees(offset, limit);
    }

    @GetMapping(value = "api/" + VERSION + "/employees/investigation/{id}")
    public List<Employee> getInvolvedEmployeesInInvestigation(@PathVariable int id,
                                                                   @RequestParam(name = "limit", defaultValue = DEFAULT_LIMIT) int limit,
                                                                   @RequestParam(name = "offset", defaultValue = DEFAULT_OFFSET) int offset) {
        LOGGER.debug("getInvolvedEmployeesInInvestigation(int, int, int)");
        return employeeService.getInvolvedEmployeesInInvestigation(id, offset, limit);
    }

    @GetMapping(value = "/api/" + VERSION + "/employees/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.FOUND)
    public Employee getEmployeeById(@PathVariable int id) {

        LOGGER.debug("getEmployeeById(id) - id is: {}", id);
        return employeeService.getEmployeeById(id);
    }

    @PostMapping(value = "/api/" + VERSION + "/employees/", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public int addEmployee(@RequestBody Employee employee) {

        LOGGER.debug("addEmployee(Employee)");
        return employeeService.addEmployee(employee);
    }

    @PostMapping(value = "/api/" + VERSION + "/employees/{id}/investigations", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void addInvestigations2Employee(@PathVariable int id, @RequestBody List<Integer> investigationsId) {

        LOGGER.debug("addInvestigations2Employee(int, List<Integer>)");
        employeeService.addInvestigations2Employee(id, investigationsId);
    }

    @PutMapping(value = "/api/" + VERSION + "/employees", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateEmployee(@RequestBody Employee employee) {

        LOGGER.debug("updateEmployee(Employee)");
        employeeService.updateEmployee(employee);
    }

    @PutMapping(value = "/api/" + VERSION + "/employees/{id}/investigations", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateInvestigationsInEmployee(@PathVariable int id, @RequestBody List<Integer> investigationsId) {

        LOGGER.debug("updateInvestigationsInEmployee(int, List<Integer>)");
        employeeService.updateEmployeeInvestigations(id, investigationsId);
    }


    @DeleteMapping(value = "/api/" + VERSION + "/employees/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployeeById(@PathVariable int id) {

        LOGGER.debug("deleteEmployeeById(int) - id is: {}", id);
        employeeService.deleteEmployeeById(id);
    }

    @GetMapping(value = "/api/" + VERSION + "/employees/rating")
    public List<Pair<Integer, Integer>> getEmployeesRating(@RequestParam(name = "limit", defaultValue = DEFAULT_LIMIT) int limit,
                                                           @RequestParam(name = "offset", defaultValue = DEFAULT_OFFSET) int offset) {

        LOGGER.debug("getEmployeesRating(int, int)");
        return employeeService.getEmployeesRating(offset, limit);
    }

}
