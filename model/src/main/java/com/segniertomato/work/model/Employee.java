package com.segniertomato.work.model;


import com.fasterxml.jackson.annotation.JsonView;
import com.segniertomato.work.profile.View;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;


public class Employee {

    private static final Logger LOGGER = LogManager.getLogger();

    @JsonView(View.Summary.class)
    private Integer employeeId = -1;

    @JsonView(View.Summary.class)
    private String name;

    @JsonView(View.Summary.class)
    private LocalDate age;

    @JsonView(View.Summary.class)
    private LocalDate startWorkingDate;

    private List<Investigation> participatedInvestigations = new LinkedList<>();

    public Employee(Integer employeeId, String name, LocalDate age, LocalDate startWorkingDate) {

        LOGGER.debug("constructor Employee(Integer, String, LocalDate, LocalDate)");

        this.employeeId = employeeId;
        this.name = name;
        this.age = age;
        this.startWorkingDate = startWorkingDate;
    }

    public Employee(Integer employeeId, String name, LocalDate age, LocalDate startWorkingDate, List<Investigation> participatedInvestigations) {

        LOGGER.debug("constructor Employee(Integer, String, LocalDate, LocalDate, List<Investigation>)");

        this.employeeId = employeeId;
        this.name = name;
        this.age = age;
        this.startWorkingDate = startWorkingDate;
        this.participatedInvestigations = participatedInvestigations;
    }

    public Employee(String name, LocalDate age, LocalDate startWorkingDate) {

        LOGGER.debug("constructor Employee(String, LocalDate, LocalDateTime)");

        this.name = name;
        this.age = age;
        this.startWorkingDate = startWorkingDate;
    }

    public Employee(String name, LocalDate age, LocalDate startWorkingDate, List<Investigation> participatedInvestigation) {

        LOGGER.debug("constructor Employee(String, LocalDate, LocalDate, List<Investigation>)");

        this.name = name;
        this.age = age;
        this.startWorkingDate = startWorkingDate;
        this.participatedInvestigations = participatedInvestigation;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {

        LOGGER.debug("setEmployeeId(Integer)");
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {

        LOGGER.debug("setTitle(String)");
        this.name = name;
    }

    public LocalDate getAge() {
        return age;
    }

    public void setAge(LocalDate age) {

        LOGGER.debug("setAge(LocalDate)");
        this.age = age;
    }

    public LocalDate getStartWorkingDate() {
        return startWorkingDate;
    }

    public void setStartWorkingDate(LocalDate startWorkingDate) {

        LOGGER.debug("setStartWorkingDate(LocalDate)");
        this.startWorkingDate = startWorkingDate;
    }

    public List<Investigation> getParticipatedInvestigations() {
        return participatedInvestigations;
    }

    public void setParticipatedInvestigations(List<Investigation> participatedInvestigations) {

        LOGGER.debug("setParticipatedInvestigations(List<Investigation>)");
        this.participatedInvestigations = participatedInvestigations;
    }

    @Override
    public boolean equals(Object o) {

        LOGGER.debug("equals(Object)");
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee employee = (Employee) o;
        return Objects.equals(employeeId, employee.employeeId) &&
                Objects.equals(name, employee.name) &&
                Objects.equals(age, employee.age) &&
                Objects.equals(startWorkingDate, employee.startWorkingDate) &&
                Objects.equals(participatedInvestigations, employee.participatedInvestigations);
    }

    @Override
    public int hashCode() {

        LOGGER.debug("hashCode()");
        return Objects.hash(employeeId, name, age, startWorkingDate, participatedInvestigations);
    }

    @Override
    public String toString() {

        LOGGER.debug("toString()");
        return "Employee{" +
                "employeeId=" + employeeId +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", startWorkingDate=" + startWorkingDate +
                ", participatedInvestigations=" + participatedInvestigations +
                '}';
    }
}
