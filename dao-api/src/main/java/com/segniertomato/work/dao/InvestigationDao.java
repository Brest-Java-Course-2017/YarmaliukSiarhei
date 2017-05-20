package com.segniertomato.work.dao;


import com.segniertomato.work.model.Investigation;
import org.springframework.dao.DataAccessException;

import java.time.OffsetDateTime;
import java.util.List;

public interface InvestigationDao {

    public List<Investigation> getAllInvestigation(int offset, int count) throws DataAccessException;

    public List<Investigation> getInvestigationsBetweenPeriod(OffsetDateTime startDate,
                                                              OffsetDateTime endDate, int offset, int count) throws DataAccessException;

    public Integer addInvestigation(Investigation investigation) throws DataAccessException;

    public void addInvolvedStaff2Investigation(Integer investigationId, List<Integer> employeesId) throws DataAccessException;

    public int updateInvestigation(Investigation investigation) throws DataAccessException;

    public int updateInvolvedStaffInInvestigaiton(Integer investigationId, List<Integer> employeesId) throws DataAccessException;

    public int deleteInvestigationById(Integer investigationId) throws DataAccessException;
}
