package com.segniertomato.work.service;


import com.segniertomato.work.model.Employee;
import com.segniertomato.work.model.Pair;
import org.springframework.dao.DataAccessException;

import java.util.List;


public interface EmployeeService {

    public List<Employee> getAllEmployees(int offset, int count) throws DataAccessException;

    public List<Employee> getInvolvedEmployeesInInvestigation(Integer investigationId, int offset, int count) throws DataAccessException;

    public Employee getEmployeeById(Integer employeeId) throws DataAccessException;

    public Integer addEmployee(Employee employee) throws DataAccessException;

    public void addInvestigations2Employee(Integer employeeId, List<Integer> participateInvestigations) throws DataAccessException;

    public boolean updateEmployee(Employee employee) throws DataAccessException;

    public boolean updateEmployeeInvestigations(Integer employeeId, List<Integer> investigationsId) throws DataAccessException;

    public boolean deleteEmployeeById(Integer employeeId) throws DataAccessException;

    public List<Pair<Integer, Integer>> getEmployeesRating(int offset, int count) throws DataAccessException;
}
