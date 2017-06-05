package com.segniertomato.work.service;


import com.segniertomato.work.dao.InvestigationDao;
import com.segniertomato.work.message.MessageError;
import com.segniertomato.work.model.Employee;
import com.segniertomato.work.model.Investigation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;


@Service
@Transactional
public class InvestigationServiceImpl implements InvestigationService {

    private static final Logger LOGGER = LogManager.getLogger();

    @Value("${sql.getCountEmployeesWithId}")
    private final String GET_COUNT_EMPLOYEES_WITH_ID = null;

    @Value("${sql.getCountInvestigationsWithId}")
    private final String GET_COUNT_INVESTIGATIONS_WITH_ID = null;


    private static final class NamedParameterNames {

        private static final String INVESTIGATION_ID = "investigation_id";
        private static final String EMPLOYEE_ID = "employee_id";
    }

    @Autowired(required = false)
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private InvestigationDao investigationDao;


    public InvestigationServiceImpl(DataSource dataSource, InvestigationDao investigationDao) {

        LOGGER.debug("constructor InvestigationServiceImpl(DataSource, InvestigationDao)");

        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.investigationDao = investigationDao;
    }

    public InvestigationServiceImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate, InvestigationDao investigationDao) {

        LOGGER.debug("constructor InvestigationServiceImpl(NamedParameterJDBCTemplate, InvestigationDao)");

        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.investigationDao = investigationDao;
    }

    @Override
    public List<Investigation> getAllInvestigations(int offset, int count) throws DataAccessException {

        LOGGER.debug("getAllInvestigations(int, int)");

        isTrue(offset >= 0, MessageError.InvalidIncomingParameters.OFFSET_CAN_NOT_BE_LOWER_THAN_ZERO);
        isTrue(count >= 0, MessageError.InvalidIncomingParameters.LIMIT_CAN_NOT_BE_LOWER_THAN_ZERO);

        return investigationDao.getAllInvestigations(offset, count);
    }

    @Override
    public List<Investigation> getInvestigationsBetweenPeriod(OffsetDateTime startDate, OffsetDateTime endDate, int offset, int count) throws DataAccessException {

        LOGGER.debug("getInvestigationsBetweenPeriod(OffsetDateTime, OffsetDateTime, int, int)");

        notNull(startDate, MessageError.InvalidIncomingParameters.START_DATE_IN_PERIOD_CAN_NOT_BE_NULL);
        notNull(endDate, MessageError.InvalidIncomingParameters.END_DATE_IN_PERIOD_CAN_NOT_BE_NULL);
        isTrue(isValidPeriod(startDate, endDate), MessageError.InvalidIncomingParameters.START_AND_END_DATES_SHOULD_MATCH_PATTERN);

        isTrue(offset >= 0, MessageError.InvalidIncomingParameters.OFFSET_CAN_NOT_BE_LOWER_THAN_ZERO);
        isTrue(count >= 0, MessageError.InvalidIncomingParameters.LIMIT_CAN_NOT_BE_LOWER_THAN_ZERO);

        return investigationDao.getInvestigationsBetweenPeriod(
                startDate.withOffsetSameInstant(ZoneOffset.UTC), endDate.withOffsetSameLocal(ZoneOffset.UTC), offset, count);
    }

    @Override
    public List<Investigation> getEmployeeInvestigations(Integer employeeId, int offset, int count) throws DataAccessException {

        LOGGER.debug("getEmployeeInvestigations(Integer, int, int)");

        notNull(employeeId, MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL);
        isTrue(employeeId > 0, MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO);

        isTrue(offset >= 0, MessageError.InvalidIncomingParameters.OFFSET_CAN_NOT_BE_LOWER_THAN_ZERO);
        isTrue(count >= 0, MessageError.InvalidIncomingParameters.LIMIT_CAN_NOT_BE_LOWER_THAN_ZERO);

        isTrue(isExists(employeeId, Employee.class), MessageError.EMPLOYEE_NOT_EXISTS);

        return investigationDao.getEmployeeInvestigations(employeeId, offset, count);
    }

    @Override
    public Investigation getInvestigationById(Integer investigationId) throws DataAccessException {

        LOGGER.debug("getInvestigationById(Integer)");

        notNull(investigationId, MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL);
        isTrue(investigationId > 0, MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO);
        isTrue(isExists(investigationId, Investigation.class), MessageError.INVESTIGATION_NOT_EXISTS);

        return investigationDao.getInvestigationById(investigationId);
    }

    @Override
    public Integer addInvestigation(Investigation investigation) throws DataAccessException {

        LOGGER.debug("addInvestigation(Investigation)");

        notNull(investigation, MessageError.InvalidIncomingParameters.INVESTIGATION_CAN_NOT_BE_NULL);
        isTrue(investigation.getInvestigationId() == null || investigation.getInvestigationId() == -1,
                MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_NULL_OR_MINUS_ONE);
        isTrue(isValidDates(investigation.getStartInvestigationDate(), investigation.getEndInvestigationDate()),
                MessageError.InvalidIncomingParameters.START_AND_END_DATES_SHOULD_MATCH_PATTERN);

        notNull(investigation.getDescription(), MessageError.InvalidIncomingParameters.INVESTIGATION_DESCRIPTION_CAN_NOT_BE_NULL);

        OffsetDateTime startInvestigationDateInUTC =
                investigation.getStartInvestigationDate().withOffsetSameInstant(ZoneOffset.UTC);
        investigation.setStartInvestigationDate(startInvestigationDateInUTC);

        if (investigation.getEndInvestigationDate() != null) {
            OffsetDateTime endDateInUTC =
                    investigation.getEndInvestigationDate().withOffsetSameInstant(ZoneOffset.UTC);
            investigation.setEndInvestigationDate(endDateInUTC);
        }

        List<Employee> involvedStaff = investigation.getInvolvedStaff();
        notNull(involvedStaff, MessageError.InvalidIncomingParameters.INVESTIGATION_INVOLVED_STAFF_CAN_NOT_BE_NULL);

        involvedStaff.forEach((item) -> {

            notNull(item, MessageError.InvalidIncomingParameters.EMPLOYEE_CAN_NOT_BE_NULL);
            Integer employeeId = item.getEmployeeId();

            notNull(employeeId, MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL);
            isTrue(employeeId > 0, MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO);
            isTrue(isExists(employeeId, Employee.class), MessageError.EMPLOYEE_NOT_EXISTS);
        });

        return investigationDao.addInvestigation(investigation);
    }

    @Override
    public void addInvolvedStaff2Investigation(Integer investigationId, List<Integer> employeesId) throws DataAccessException {

        LOGGER.debug("addInvolvedStaff2Investigation(Integer, List<Integer>)");

        notNull(investigationId, MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL);
        isTrue(investigationId > 0, MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO);
        isTrue(isExists(investigationId, Investigation.class), MessageError.INVESTIGATION_NOT_EXISTS);

        notNull(employeesId, MessageError.InvalidIncomingParameters.INVESTIGATION_INVOLVED_STAFF_CAN_NOT_BE_NULL);

        employeesId.forEach((item) -> {

            notNull(item, MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL);
            isTrue(item > 0, MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO);
            isTrue(isExists(item, Employee.class), MessageError.EMPLOYEE_NOT_EXISTS);
        });

        investigationDao.addInvolvedStaff2Investigation(investigationId, employeesId);
    }

    @Override
    public boolean updateInvestigation(Investigation investigation) throws DataAccessException {

        LOGGER.debug("updateInvestigation(Investigation)");

        notNull(investigation, MessageError.InvalidIncomingParameters.INVESTIGATION_CAN_NOT_BE_NULL);
        notNull(investigation.getInvestigationId(), MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL);
        isTrue(investigation.getInvestigationId() > 0, MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO);
        isTrue(isValidDates(investigation.getStartInvestigationDate(), investigation.getEndInvestigationDate()),
                MessageError.InvalidIncomingParameters.START_AND_END_DATES_SHOULD_MATCH_PATTERN);
        notNull(investigation.getDescription(), MessageError.InvalidIncomingParameters.INVESTIGATION_DESCRIPTION_CAN_NOT_BE_NULL);

        isTrue(isExists(investigation.getInvestigationId(), Investigation.class), MessageError.INVESTIGATION_NOT_EXISTS);

        OffsetDateTime startInvestigationDateInUTC =
                investigation.getStartInvestigationDate().withOffsetSameInstant(ZoneOffset.UTC);
        investigation.setStartInvestigationDate(startInvestigationDateInUTC);

        if (investigation.getEndInvestigationDate() != null) {
            OffsetDateTime endDateInUTC =
                    investigation.getEndInvestigationDate().withOffsetSameInstant(ZoneOffset.UTC);
            investigation.setEndInvestigationDate(endDateInUTC);
        }

        List<Employee> involvedStaff = investigation.getInvolvedStaff();
        notNull(involvedStaff, MessageError.InvalidIncomingParameters.INVESTIGATION_INVOLVED_STAFF_CAN_NOT_BE_NULL);

        involvedStaff.forEach((item) -> {

            notNull(item, MessageError.InvalidIncomingParameters.EMPLOYEE_CAN_NOT_BE_NULL);
            Integer employeeId = item.getEmployeeId();

            notNull(employeeId, MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL);
            isTrue(employeeId > 0, MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO);
            isTrue(isExists(employeeId, Employee.class), MessageError.EMPLOYEE_NOT_EXISTS);
        });

        return investigationDao.updateInvestigation(investigation) > 0;
    }

    @Override
    public boolean updateInvolvedStaffInInvestigation(Integer investigationId, List<Integer> employeesId) throws DataAccessException {

        LOGGER.debug("updateInvolvedStaffInInvestigation(Integer, List<Integer>)");

        notNull(investigationId, MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL);
        isTrue(investigationId > 0, MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO);
        isTrue(isExists(investigationId, Investigation.class), MessageError.INVESTIGATION_NOT_EXISTS);

        notNull(employeesId, MessageError.InvalidIncomingParameters.INVESTIGATION_INVOLVED_STAFF_CAN_NOT_BE_NULL);

        employeesId.forEach((item) -> {

            notNull(item, MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL);
            isTrue(item > 0, MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO);
            isTrue(isExists(item, Employee.class), MessageError.EMPLOYEE_NOT_EXISTS);
        });

        return investigationDao.updateInvolvedStaffInInvestigation(investigationId, employeesId) > 0;
    }

    @Override
    public boolean deleteInvestigationById(Integer investigationId) throws DataAccessException {

        LOGGER.debug("deleteInvestigationById(Integer)");

        notNull(investigationId, MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL);
        isTrue(investigationId > 0, MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO);
        isTrue(isExists(investigationId, Investigation.class), MessageError.INVESTIGATION_NOT_EXISTS);

        return investigationDao.deleteInvestigationById(investigationId) > 0;
    }

    private boolean isValidDates(OffsetDateTime startDate, OffsetDateTime endDate) {

        LOGGER.debug("isValidDates(OffsetDateTime, OffsetDateTime)");

        if (startDate == null) return false;
        return endDate == null ? startDate.isBefore(OffsetDateTime.now()) :
                startDate.isBefore(endDate) && endDate.isBefore(OffsetDateTime.now());
    }

    private boolean isValidPeriod(OffsetDateTime startDate, OffsetDateTime endDate) {

        LOGGER.debug("isValidPeriod(OffsetDateTime, OffsetDateTime)");

        if (startDate == null || endDate == null) return false;
        return startDate.isBefore(endDate) && endDate.isBefore(OffsetDateTime.now());
    }

    private boolean isExists(Integer id, Class classType) {

        LOGGER.debug("isExists(Integer, Class)");

        String sqlQuery;
        String parameterName;

        String typeName = classType.getName();

        if (typeName.equals(Employee.class.getName())) {
            sqlQuery = GET_COUNT_EMPLOYEES_WITH_ID;
            parameterName = NamedParameterNames.EMPLOYEE_ID;

        } else if (typeName.equals(Investigation.class.getName())) {
            sqlQuery = GET_COUNT_INVESTIGATIONS_WITH_ID;
            parameterName = NamedParameterNames.INVESTIGATION_ID;

        } else {
            throw new IllegalArgumentException("Not supported incoming class type. Incoming type should be Employee or Investigation.");
        }

        int count = namedParameterJdbcTemplate.queryForObject(sqlQuery, new MapSqlParameterSource(parameterName, id), Integer.class);
        return count != 0;
    }
}
