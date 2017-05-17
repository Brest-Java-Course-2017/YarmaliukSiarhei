package com.segniertomato.work.service;


import com.segniertomato.work.model.Investigation;
import org.springframework.dao.DataAccessException;

import java.time.OffsetDateTime;
import java.util.List;

public interface InvestigationService {

    public List<Investigation> getAllInvestigations(int offset, int count) throws DataAccessException;

    public List<Investigation> getInvestigationsBetweenPeriod(OffsetDateTime startDate,
                                                              OffsetDateTime endDate, int offset, int count) throws DataAccessException;

    public List<Investigation> getEmployeeInvestigations(Integer employeeId, int offset, int count) throws DataAccessException;

    public Investigation getInvestigationById(Integer investigationId) throws DataAccessException;

    public Integer addInvestigation(Investigation investigation) throws DataAccessException;

    public void addInvolvedStaff2Investigation(Integer investigationId, List<Integer> employeesId) throws DataAccessException;

    public boolean updateInvestigation(Investigation investigation) throws DataAccessException;

    public boolean updateInvolvedStaffInInvestigation(Integer investigationId, List<Integer> employeesId) throws DataAccessException;

    public boolean deleteInvestigationById(Integer investigationId) throws DataAccessException;
}
