package com.segniertomato.work.dao;


import com.segniertomato.work.model.Employee;
import com.segniertomato.work.model.Investigation;
import org.springframework.dao.DataAccessException;

import java.time.OffsetDateTime;
import java.util.List;

public interface EmployeeDao {

    public List<Employee> getAllEmployees(int offset, int count) throws DataAccessException;

    public List<Employee> getEmployeesBetweenPeriod(OffsetDateTime startDate,
                                                    OffsetDateTime endDate, int offset, int count) throws DataAccessException;

    public Integer addEmployee(Employee employee) throws DataAccessException;

    public void addInvestigations2Employee(Integer employeeId, List<Investigation> participateInvestigations) throws DataAccessException;

    public int updateEmployee(Employee employee) throws DataAccessException;

    public int updateEmployeeIvestigaitons(Integer employeeId, List<Integer> investigationsId) throws DataAccessException;

    public int deleteEmployeeById(Integer employeeId) throws DataAccessException;
}
