package com.segniertomato.work.model;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;


public class Employee {

    private static Logger LOGGER = LogManager.getLogger();

    private Integer employeeId = -1;
    private String firstName;

    private OffsetDateTime age;
    private OffsetDateTime startWorkingDate;

    private List<Investigation> participatedInvestigation = new LinkedList<>();


    public Employee(String firstName, OffsetDateTime age, OffsetDateTime startWorkingDate) {

        LOGGER.debug("constructor Employee(String, OffsetDateTime, OffsetDateTime)");

        this.firstName = firstName;
        this.age = age;
        this.startWorkingDate = startWorkingDate;
    }

    public Employee(String firstName, OffsetDateTime age, OffsetDateTime startWorkingDate, List<Investigation> participatedInvestigation) {

        LOGGER.debug("constructor Employee(String, OffsetDateTime, OffsetDateTime, List<Investigation>)");

        this.firstName = firstName;
        this.age = age;
        this.startWorkingDate = startWorkingDate;
        this.participatedInvestigation = participatedInvestigation;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {

        LOGGER.debug("setEmployeeId(Integer)");
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {

        LOGGER.debug("setFirstName(String)");
        this.firstName = firstName;
    }

    public OffsetDateTime getAge() {
        return age;
    }

    public void setAge(OffsetDateTime age) {

        LOGGER.debug("setAge(OffsetDateTime)");
        this.age = age;
    }

    public OffsetDateTime getStartWorkingDate() {
        return startWorkingDate;
    }

    public void setStartWorkingDate(OffsetDateTime startWorkingDate) {

        LOGGER.debug("setStartWorkingDate(OffsetDateTime)");
        this.startWorkingDate = startWorkingDate;
    }

    public List<Investigation> getParticipatedInvestigation() {
        return participatedInvestigation;
    }

    public void setParticipatedInvestigation(List<Investigation> participatedInvestigation) {

        LOGGER.debug("setParticipatedInvestigation(List<Investigation>)");
        this.participatedInvestigation = participatedInvestigation;
    }

    @Override
    public boolean equals(Object o) {

        LOGGER.debug("equals(Object)");
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee employee = (Employee) o;
        return Objects.equals(employeeId, employee.employeeId) &&
                Objects.equals(firstName, employee.firstName) &&
                Objects.equals(age, employee.age) &&
                Objects.equals(startWorkingDate, employee.startWorkingDate) &&
                Objects.equals(participatedInvestigation, employee.participatedInvestigation);
    }

    @Override
    public int hashCode() {

        LOGGER.debug("hashCode()");
        return Objects.hash(employeeId, firstName, age, startWorkingDate, participatedInvestigation);
    }

    @Override
    public String toString() {

        LOGGER.debug("toString()");
        return "Employee{" +
                "employeeId=" + employeeId +
                ", firstName='" + firstName + '\'' +
                ", age=" + age +
                ", startWorkingDate=" + startWorkingDate +
                ", participatedInvestigation=" + participatedInvestigation +
                '}';
    }
}
