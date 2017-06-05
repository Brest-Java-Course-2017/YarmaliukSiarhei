package com.segniertomato.work.dao;


import com.segniertomato.work.model.Employee;
import com.segniertomato.work.model.Investigation;
import com.segniertomato.work.model.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;


public class EmployeeDaoImpl implements EmployeeDao {

    private static final Logger LOGGER = LogManager.getLogger();

    @Value("${sql.getAllEmployees}")
    private final String GET_ALL_EMPLOYEES = null;

    @Value("${sql.getInvolvedEmployeesInInvestigation}")
    private final String GET_INVOLVED_EMPLOYEES_IN_INVESTIGATION = null;

    @Value("${sql.getEmployeeById}")
    private final String GET_EMPLOYEE_BY_ID = null;

    @Value("${sql.addEmployee}")
    private final String ADD_EMPLOYEE = null;

    @Value("${sql.addInvestigationEmployeeRelations}")
    private final String ADD_INVESTIGATION_EMPLOYEE_RELATIONS = null;

    @Value("${sql.updateEmployee}")
    private final String UPDATE_EMPLOYEE = null;

    @Value("${sql.getEmployee'sInvestigationsId}")
    private final String GET_EMPLOYEE_INVESTIGATIONS_ID = null;

    @Value("${sql.deleteInvestigationEmployeeRelations}")
    private final String DELETE_INVESTIGATION_EMPLOYEE_RELATIONS = null;

    @Value("${sql.deleteEmployeeById}")
    private final String DELETE_EMPLOYEE_BY_ID = null;

    @Value("${sql.getEmployeesRating}")
    private final String GET_EMPLOYEES_RATING = null;

    private static final class NamedParameterNames {

        private static final String EMPLOYEE_ID = "employee_id";
        private static final String INVESTIGATION_ID = "investigation_id";
        private static final String OFFSET = "offset";
        private static final String LIMIT = "limit";
    }

    private static final class BindsNames {

        private static final String EMPLOYEE_ID = "employee_id";
        private static final String RATING = "rating";
    }

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public EmployeeDaoImpl(DataSource dataSource) {

        LOGGER.debug("constructor EmployeeDaoImpl(DataSource)");
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<Employee> getAllEmployees(int offset, int count) throws DataAccessException {

        LOGGER.debug("getAllEmployees(int, int)");

        Map<String, Object> namedParams = new HashMap<>();
        namedParams.put(NamedParameterNames.OFFSET, offset);
        namedParams.put(NamedParameterNames.LIMIT, count);

        return namedParameterJdbcTemplate.query(GET_ALL_EMPLOYEES, new MapSqlParameterSource(namedParams), new EmployeeRowMapper());
    }

    @Override
    public List<Employee> getInvolvedEmployeesInInvestigation(Integer investigationId, int offset, int count) throws DataAccessException {

        LOGGER.debug("getInvolvedEmployeesInInvestigation(Integer, int, int)");

        Map<String, Object> namedParams = new HashMap<>();
        namedParams.put(NamedParameterNames.INVESTIGATION_ID, investigationId);
        namedParams.put(NamedParameterNames.OFFSET, offset);
        namedParams.put(NamedParameterNames.LIMIT, count);

        return namedParameterJdbcTemplate.query(GET_INVOLVED_EMPLOYEES_IN_INVESTIGATION, new MapSqlParameterSource(namedParams), new EmployeeRowMapper());
    }

    @Override
    public Employee getEmployeeById(Integer employeeId) throws DataAccessException {

        LOGGER.debug("getEmployeeById(Integer)");
        return namedParameterJdbcTemplate.queryForObject(GET_EMPLOYEE_BY_ID,
                new MapSqlParameterSource(NamedParameterNames.EMPLOYEE_ID, employeeId), new EmployeeRowMapper());
    }

    @Override
    public Integer addEmployee(Employee employee) throws DataAccessException {

        LOGGER.debug("addEmployee(Employee)");

        KeyHolder keyHolder = new GeneratedKeyHolder();

        LOGGER.debug("addEmployee(Employee) - create SqlParameterSource");
        SqlParameterSource namedParams = new BeanPropertySqlParameterSource(employee);

        namedParameterJdbcTemplate.update(ADD_EMPLOYEE, namedParams, keyHolder);
        Integer employeeId = keyHolder.getKey().intValue();

        List<Investigation> investigations = employee.getParticipatedInvestigations();

        if (investigations != null && !investigations.isEmpty()) {

            List<Integer> investigationsId = new ArrayList<>(investigations.size());
            investigations.forEach(item -> investigationsId.add(item.getInvestigationId()));

            addInvestigations2Employee(employeeId, investigationsId);
        }

        return employeeId;
    }

    @Override
    public void addInvestigations2Employee(Integer employeeId, List<Integer> participateInvestigations) throws DataAccessException {

        LOGGER.debug("addInvestigations2Employee(Integer, List<Investigation>)");

        List<Integer> existsInvestigationsId = namedParameterJdbcTemplate.queryForList(GET_EMPLOYEE_INVESTIGATIONS_ID,
                new MapSqlParameterSource(NamedParameterNames.EMPLOYEE_ID, employeeId), Integer.class);

        Pair<List<Integer>, List<Integer>> lists = Utils.getNotEqualsElementsInLists(participateInvestigations, existsInvestigationsId);

        LOGGER.debug("Elements are: " );
        lists.first.forEach(LOGGER::debug);

        Map<String, Integer>[] batchValues = createBatchForEmployeeInvestigations(employeeId, lists.first);
        namedParameterJdbcTemplate.batchUpdate(ADD_INVESTIGATION_EMPLOYEE_RELATIONS, batchValues);
    }

    @Override
    public int updateEmployee(Employee employee) throws DataAccessException {

        LOGGER.debug("updateEmployee(Employee)");

        List<Investigation> investigations = employee.getParticipatedInvestigations();

        if (investigations != null) {

            List<Integer> investigationsId = new ArrayList<>();
            investigations.forEach(item -> investigationsId.add(item.getInvestigationId()));

            updateEmployeeInvestigations(employee.getEmployeeId(), investigationsId);
        }

        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(employee);
        return namedParameterJdbcTemplate.update(UPDATE_EMPLOYEE, namedParameters);
    }

    @Override
    public int updateEmployeeInvestigations(Integer employeeId, List<Integer> investigationsId) throws DataAccessException {

        LOGGER.debug("updateEmployeeInvestigations(Integer, List<Integer>)");

        int updatedRow = 0;

        List<Integer> removeInvestigationCandidates = namedParameterJdbcTemplate.queryForList(GET_EMPLOYEE_INVESTIGATIONS_ID,
                new MapSqlParameterSource(NamedParameterNames.EMPLOYEE_ID, employeeId), Integer.class);

        if (!investigationsId.isEmpty()) {

            Pair<List<Integer>, List<Integer>> listPair = Utils.getNotEqualsElementsInLists(investigationsId, removeInvestigationCandidates);

            List<Integer> addInvestigations = listPair.first;
            if (!addInvestigations.isEmpty()) {
                addInvestigations2Employee(employeeId, addInvestigations);
                updatedRow += addInvestigations.size();
            }

            removeInvestigationCandidates = listPair.second;
        }

        if (!removeInvestigationCandidates.isEmpty()) {
            Map<String, Integer>[] batchValues = createBatchForEmployeeInvestigations(employeeId, removeInvestigationCandidates);
            namedParameterJdbcTemplate.batchUpdate(DELETE_INVESTIGATION_EMPLOYEE_RELATIONS, batchValues);

            updatedRow += removeInvestigationCandidates.size();
        }

        return updatedRow;
    }

    @Override
    public int deleteEmployeeById(Integer employeeId) throws DataAccessException {

        LOGGER.debug("deleteEmployeeById(Integer)");
        return namedParameterJdbcTemplate.update(
                DELETE_EMPLOYEE_BY_ID,
                new MapSqlParameterSource(NamedParameterNames.EMPLOYEE_ID, employeeId));
    }

    @Override
    public List<Pair<Integer, Integer>> getEmployeesRating(int offset, int count) throws DataAccessException {

        LOGGER.debug("getEmployeesRating(int, int)");

        Map<String, Integer> namedParams = new HashMap<>();
        namedParams.put(NamedParameterNames.OFFSET, offset);
        namedParams.put(NamedParameterNames.LIMIT, count);

        List<Map<String, Object>> returnedValues = namedParameterJdbcTemplate.queryForList(GET_EMPLOYEES_RATING, new MapSqlParameterSource(namedParams));

        List<Pair<Integer, Integer>> employeesRating = new ArrayList<>(returnedValues.size());

        returnedValues.forEach((item) -> {

            employeesRating.add(new Pair<>(
                    (Integer) item.get(BindsNames.EMPLOYEE_ID),
                    ((Double) item.get(BindsNames.RATING)).intValue()
            ));
        });

        return employeesRating;
    }


    private Map<String, Integer>[] createBatchForEmployeeInvestigations(Integer employeeId, List<Integer> investigationsId) {

        return Utils.getBatchValues((item) -> {

                    Map<String, Integer> rowNamedParameter = new HashMap<>();
                    rowNamedParameter.put(NamedParameterNames.EMPLOYEE_ID, employeeId);
                    rowNamedParameter.put(NamedParameterNames.INVESTIGATION_ID, investigationsId.get(item));
                    return rowNamedParameter;
                },
                investigationsId.size());
    }

    private static final class EmployeeRowMapper implements RowMapper<Employee> {

        private static final Logger LOGGER = LogManager.getLogger();

        private static final class BindNames {

            private static final String EMPLOYEE_ID = "employee_id";
            private static final String NAME = "name";
            private static final String AGE = "age";
            private static final String START_WORKING_DATE = "start_working_date";
        }

        @Override
        public Employee mapRow(ResultSet resultSet, int i) throws SQLException {

            LOGGER.debug("mapRow(ResultSet, int) - mapping returned ResultSet into Employee object.");

            return new Employee(
                    resultSet.getInt(BindNames.EMPLOYEE_ID),
                    resultSet.getString(BindNames.NAME),
                    convertDate2LocalDate(resultSet.getDate(BindNames.AGE)),
                    convertDate2LocalDate(resultSet.getDate(BindNames.START_WORKING_DATE)));
        }

        private LocalDate convertDate2LocalDate(Date date) {
            return date.toLocalDate();
        }
    }
}
