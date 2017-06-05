package com.segniertomato.work.service;


import com.segniertomato.work.dao.EmployeeDao;
import com.segniertomato.work.message.MessageError;
import com.segniertomato.work.model.Employee;
import com.segniertomato.work.model.Investigation;
import com.segniertomato.work.model.Pair;
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
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.regex.Pattern;

import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;


@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOGGER = LogManager.getLogger();

    @Value("${sql.getCountEmployeesWithId}")
    private final String GET_COUNT_EMPLOYEES_WITH_ID = null;

    @Value("${sql.getCountInvestigationsWithId}")
    private final String GET_COUNT_INVESTIGATIONS_WITH_ID = null;

    @Value("${pattern.employeeName}")
    private final String EMPLOYEE_NAME_PATTERN = null;

    @Value("${pattern.jobAgeRequirement}")
    private final int REQUIREMENT_AGE = 0;

    private static final class NamedParameterNames {

        private static final String INVESTIGATION_ID = "investigation_id";
        private static final String EMPLOYEE_ID = "employee_id";
    }

    @Autowired(required = false)
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private EmployeeDao employeeDao;

    public EmployeeServiceImpl(DataSource dataSource, EmployeeDao employeeDao) {

        LOGGER.debug("constructor EmployeeServiceImpl(DataSource, EmployeeDao)");

        this.employeeDao = employeeDao;
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public EmployeeServiceImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate, EmployeeDao employeeDao) {

        LOGGER.debug("constructor EmployeeServiceImpl(NamedParameterJdbcTemplate, EmployeeDao)");

        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.employeeDao = employeeDao;
    }

    @Override
    public List<Employee> getAllEmployees(int offset, int count) throws DataAccessException {

        LOGGER.debug("getAllEmployees(int, int)");

        isTrue(count >= 0, MessageError.InvalidIncomingParameters.LIMIT_CAN_NOT_BE_LOWER_THAN_ZERO);
        isTrue(offset >= 0, MessageError.InvalidIncomingParameters.OFFSET_CAN_NOT_BE_LOWER_THAN_ZERO);

        return employeeDao.getAllEmployees(offset, count);
    }

    @Override
    public List<Employee> getInvolvedEmployeesInInvestigation(Integer investigationId, int offset, int count) throws DataAccessException {

        LOGGER.debug("getInvolvedEmployeesInInvestigation(Integer, int, int)");

        notNull(investigationId, MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL);
        isTrue(investigationId > 0, MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO);
        isTrue(count >= 0, MessageError.InvalidIncomingParameters.LIMIT_CAN_NOT_BE_LOWER_THAN_ZERO);
        isTrue(offset >= 0, MessageError.InvalidIncomingParameters.OFFSET_CAN_NOT_BE_LOWER_THAN_ZERO);
        isTrue(isExists(investigationId, Investigation.class), MessageError.INVESTIGATION_NOT_EXISTS);

        return employeeDao.getInvolvedEmployeesInInvestigation(investigationId, offset, count);
    }

    @Override
    public Employee getEmployeeById(Integer employeeId) throws DataAccessException {

        LOGGER.debug("getEmployeeById(Integer)");

        notNull(employeeId, MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL);
        isTrue(employeeId > 0, MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO);
        isTrue(isExists(employeeId, Employee.class), MessageError.EMPLOYEE_NOT_EXISTS);

        return employeeDao.getEmployeeById(employeeId);
    }

    @Override
    public Integer addEmployee(Employee employee) throws DataAccessException {

        LOGGER.debug("addEmployee(Employee)");

        notNull(employee, MessageError.InvalidIncomingParameters.EMPLOYEE_CAN_NOT_BE_NULL);
        isTrue(employee.getEmployeeId() == null || employee.getEmployeeId() == -1,
                MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_NULL_OR_MINUS_ONE);

        isTrue(isValidName(employee.getName()), MessageError.InvalidIncomingParameters.EMPLOYEE_NAME_SHOULD_MATCH_PATTERN);
        isTrue(isValidDates(employee.getAge(), employee.getStartWorkingDate()), MessageError.InvalidIncomingParameters.EMPLOYEE_AGE_AND_WORKING_DATES_SHOULD_MATCH_PATTERN);

        List<Investigation> investigations = employee.getParticipatedInvestigations();
        notNull(investigations, MessageError.InvalidIncomingParameters.EMPLOYEE_PARTICIPATED_INVESTIGATIONS_CAN_NOT_BE_NULL);

        investigations.forEach((item) -> {

            notNull(item, MessageError.InvalidIncomingParameters.INVESTIGATION_CAN_NOT_BE_NULL);
            Integer investigationId = item.getInvestigationId();

            notNull(investigationId, MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL);
            isTrue(investigationId > 0, MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO);
            isTrue(isExists(investigationId, Investigation.class), MessageError.INVESTIGATION_NOT_EXISTS);
        });

//        TODO: in next version - change checking existing investigations from per id to batch.

        return employeeDao.addEmployee(employee);
    }

    @Override
    public void addInvestigations2Employee(Integer employeeId, List<Integer> participateInvestigations) throws DataAccessException {

        LOGGER.debug("addInvestigations2Employee(Integer, List<Integer>)");

        notNull(employeeId, MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL);
        isTrue(employeeId > 0, MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO);
        isTrue(isExists(employeeId, Employee.class), MessageError.EMPLOYEE_NOT_EXISTS);

        notNull(participateInvestigations, MessageError.InvalidIncomingParameters.EMPLOYEE_PARTICIPATED_INVESTIGATIONS_CAN_NOT_BE_NULL);

        participateInvestigations.forEach((item) -> {

            notNull(item, MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL);
            isTrue(item > 0, MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO);
            isTrue(isExists(item, Investigation.class), MessageError.INVESTIGATION_NOT_EXISTS);
        });

        employeeDao.addInvestigations2Employee(employeeId, participateInvestigations);
    }

    @Override
    public boolean updateEmployee(Employee employee) throws DataAccessException {

        LOGGER.debug("updateEmployee(Employee)");

        notNull(employee, MessageError.InvalidIncomingParameters.EMPLOYEE_CAN_NOT_BE_NULL);
        notNull(employee.getEmployeeId(), MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL);

        isTrue(employee.getEmployeeId() > 0, MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO);
        isTrue(isValidName(employee.getName()), MessageError.InvalidIncomingParameters.EMPLOYEE_NAME_SHOULD_MATCH_PATTERN);
        isTrue(isValidDates(employee.getAge(), employee.getStartWorkingDate()), MessageError.InvalidIncomingParameters.EMPLOYEE_AGE_AND_WORKING_DATES_SHOULD_MATCH_PATTERN);
        isTrue(isExists(employee.getEmployeeId(), Employee.class), MessageError.EMPLOYEE_NOT_EXISTS);

        List<Investigation> investigations = employee.getParticipatedInvestigations();
        notNull(investigations, MessageError.InvalidIncomingParameters.EMPLOYEE_PARTICIPATED_INVESTIGATIONS_CAN_NOT_BE_NULL);

        investigations.forEach((item) -> {

            notNull(item, MessageError.InvalidIncomingParameters.INVESTIGATION_CAN_NOT_BE_NULL);
            Integer investigationId = item.getInvestigationId();

            notNull(investigationId, MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL);
            isTrue(investigationId > 0, MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO);
            isTrue(isExists(investigationId, Investigation.class), MessageError.INVESTIGATION_NOT_EXISTS);
        });

//        TODO: in next version - change checking existing investigations from per id to batch.

        return employeeDao.updateEmployee(employee) > 0;
    }

    @Override
    public boolean updateEmployeeInvestigations(Integer employeeId, List<Integer> investigationsId) throws DataAccessException {

        LOGGER.debug("updateEmployeeInvestigations(Integer, List<Integer>)");

        notNull(employeeId, MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL);
        isTrue(employeeId > 0, MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO);
        isTrue(isExists(employeeId, Employee.class), MessageError.EMPLOYEE_NOT_EXISTS);

        notNull(investigationsId, MessageError.InvalidIncomingParameters.EMPLOYEE_PARTICIPATED_INVESTIGATIONS_CAN_NOT_BE_NULL);

        investigationsId.forEach((item) -> {

            notNull(item, MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL);
            isTrue(item > 0, MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO);
            isTrue(isExists(item, Investigation.class), MessageError.INVESTIGATION_NOT_EXISTS);
        });

        return employeeDao.updateEmployeeInvestigations(employeeId, investigationsId) > 0;
    }

    @Override
    public boolean deleteEmployeeById(Integer employeeId) throws DataAccessException {

        LOGGER.debug("deleteEmployeeById(Integer)");

        notNull(employeeId, MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL);
        isTrue(employeeId > 0, MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO);
        isTrue(isExists(employeeId, Employee.class), MessageError.EMPLOYEE_NOT_EXISTS);

        return employeeDao.deleteEmployeeById(employeeId) > 0;
    }

    @Override
    public List<Pair<Integer, Integer>> getEmployeesRating(int offset, int count) throws DataAccessException {

        LOGGER.debug("getEmployeesRating(int, int)");

        isTrue(count >= 0, MessageError.InvalidIncomingParameters.LIMIT_CAN_NOT_BE_LOWER_THAN_ZERO);
        isTrue(offset >= 0, MessageError.InvalidIncomingParameters.OFFSET_CAN_NOT_BE_LOWER_THAN_ZERO);

        return employeeDao.getEmployeesRating(offset, count);
    }

    private boolean isValidDates(LocalDate age, LocalDate startWorkingDate) {

        LOGGER.debug("isValidDate(localDate, LocalDate)");

        if (age != null && startWorkingDate != null) {

            if (age.isBefore(startWorkingDate) &&
                    startWorkingDate.isBefore(LocalDate.now())) {
                return Period.between(age, startWorkingDate).getYears() >= REQUIREMENT_AGE;
            }
        }

        return false;
    }

    private boolean isValidName(String name) {

        LOGGER.debug("isValidName(String)");

        if (name != null) {
            Pattern namePattern = Pattern.compile(EMPLOYEE_NAME_PATTERN);
            return namePattern.matcher(name).matches();
        }
        return false;
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
