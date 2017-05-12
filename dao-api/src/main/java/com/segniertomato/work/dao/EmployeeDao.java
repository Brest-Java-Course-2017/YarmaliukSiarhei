package com.segniertomato.work.dao;


import com.segniertomato.work.model.Employee;
import com.segniertomato.work.model.Investigation;
import org.springframework.dao.DataAccessException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;


public interface EmployeeDao {

    public List<Employee> getAllEmployees(int offset, int count) throws DataAccessException;

    public List<Employee> getInvolvedEmployeesInInvestigation(Integer investigationId, int offset, int count) throws DataAccessException;

    public Employee getEmployeeById(Integer employeeId) throws DataAccessException;

    public Integer addEmployee(Employee employee) throws DataAccessException;

    public void addInvestigations2Employee(Integer employeeId, List<Integer> participateInvestigations) throws DataAccessException;

    public int updateEmployee(Employee employee) throws DataAccessException;

    public int updateEmployeeInvestigations(Integer employeeId, List<Integer> investigationsId) throws DataAccessException;

    public int deleteEmployeeById(Integer employeeId) throws DataAccessException;

    public Map<Integer, Integer> getEmployeesRating(int offset, int count) throws DataAccessException;
}
