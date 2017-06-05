package com.segniertomato.work.dao;


import com.segniertomato.work.model.Employee;
import com.segniertomato.work.model.Investigation;
import com.segniertomato.work.model.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;


public class InvestigationDaoImpl implements InvestigationDao {

    private static final Logger LOGGER = LogManager.getLogger();

    @Value("${sql.getAllInvestigations}")
    private final String GET_ALL_INVESTIGATIONS = null;

    @Value("${sql.getInvestigationsBetweenPeriod}")
    private final String GET_INVESTIGATIONS_BETWEEN_PERIOD = null;

    @Value("${sql.getEmployeeInvestigations}")
    private final String GET_EMPLOYEE_INVESTIGATIONS = null;

    @Value("${sql.getInvestigationById}")
    private final String GET_INVESTIGATION_BY_ID = null;

    @Value("${sql.addInvestigation}")
    private final String ADD_INVESTIGATION = null;

    @Value("${sql.addInvestigationEmployeeRelations}")
    private final String ADD_INVESTIGATION_EMPLOYEE_RELATIONS = null;

    @Value("${sql.updateInvestigation}")
    private final String UPDATE_INVESTIGATION = null;

    @Value("${sql.getInvestigation'sEmployeesId}")
    private final String GET_INVESTIGATION_EMPLOYEES_ID = null;

    @Value("${sql.deleteInvestigationEmployeeRelations}")
    private final String DELETE_INVESTIGATION_EMPLOYEE_RELATIONS = null;

    @Value("${sql.deleteInvestigationById}")
    private final String DELETE_INVESTIGATION_BY_ID = null;

    private static final class NamedParameterNames {

        private static final String INVESTIGATION_ID = "investigation_id";
        private static final String EMPLOYEE_ID = "employee_id";
        private static final String LIMIT = "limit";
        private static final String OFFSET = "offset";
        private static final String START_PERIOD = "start_period";
        private static final String END_PERIOD = "end_period";
    }

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public InvestigationDaoImpl(DataSource dataSource) {

        LOGGER.debug("constructor InvestigationDaoImpl(DataSource)");
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<Investigation> getAllInvestigations(int offset, int count) throws DataAccessException {

        LOGGER.debug("getAllInvestigations(int, int)");

        Map<String, Object> namedParams = new HashMap<>();
        namedParams.put(NamedParameterNames.OFFSET, offset);
        namedParams.put(NamedParameterNames.LIMIT, count);

        return namedParameterJdbcTemplate.query(GET_ALL_INVESTIGATIONS, new MapSqlParameterSource(namedParams), new InvestigationRowMapper());
    }

    @Override
    public List<Investigation> getInvestigationsBetweenPeriod(OffsetDateTime startDate, OffsetDateTime endDate, int offset, int count) throws DataAccessException {

        LOGGER.debug("getInvestigationsBetweenPeriod(OffsetDateTime, OffsetDateTime, int, int)");

        Map<String, Object> namedParams = new HashMap<>();
        namedParams.put(NamedParameterNames.START_PERIOD, startDate);
        namedParams.put(NamedParameterNames.END_PERIOD, endDate);
        namedParams.put(NamedParameterNames.OFFSET, offset);
        namedParams.put(NamedParameterNames.LIMIT, count);

        return namedParameterJdbcTemplate.query(GET_INVESTIGATIONS_BETWEEN_PERIOD, namedParams, new InvestigationRowMapper());
    }

    @Override
    public List<Investigation> getEmployeeInvestigations(Integer employeeId, int offset, int count) throws DataAccessException {

        LOGGER.debug("getEmployeeInvestigations(Integer, int, int)");

        Map<String, Object> namedParams = new HashMap<>();
        namedParams.put(NamedParameterNames.EMPLOYEE_ID, employeeId);
        namedParams.put(NamedParameterNames.OFFSET, offset);
        namedParams.put(NamedParameterNames.LIMIT, count);
        return namedParameterJdbcTemplate.query(GET_EMPLOYEE_INVESTIGATIONS, namedParams, new InvestigationRowMapper());
    }

    @Override
    public Investigation getInvestigationById(Integer investigationId) throws DataAccessException {

        LOGGER.debug("getInvestigationById(Integer)");
        return namedParameterJdbcTemplate.queryForObject(GET_INVESTIGATION_BY_ID,
                new MapSqlParameterSource(NamedParameterNames.INVESTIGATION_ID, investigationId), new InvestigationRowMapper());
    }

    @Override
    public Integer addInvestigation(Investigation investigation) throws DataAccessException {

        LOGGER.debug("addInvestigation(Investigation)");

        KeyHolder keyHolder = new GeneratedKeyHolder();

        LOGGER.debug("addInvestigation(Investigation) - create SqlParameterSource");
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(investigation);

        namedParameterJdbcTemplate.update(ADD_INVESTIGATION, namedParameters, keyHolder);
        Integer investigationId = keyHolder.getKey().intValue();

        List<Employee> involvedStaff = investigation.getInvolvedStaff();

        if (involvedStaff != null && !involvedStaff.isEmpty()) {

            List<Integer> addEmployeesId = new ArrayList<>(involvedStaff.size());
            involvedStaff.forEach(item -> addEmployeesId.add(item.getEmployeeId()));

            addInvolvedStaff2Investigation(investigationId, addEmployeesId);
        }

        return investigationId;
    }

    @Override
    public void addInvolvedStaff2Investigation(Integer investigationId, List<Integer> employeesId) throws DataAccessException {

        LOGGER.debug("addInvolvedStaff2Investigation(Integer, List<Integer>)");

        List<Integer> existsEmployeesId = namedParameterJdbcTemplate.queryForList(GET_INVESTIGATION_EMPLOYEES_ID,
                new MapSqlParameterSource(NamedParameterNames.INVESTIGATION_ID, investigationId), Integer.class);

        Pair<List<Integer>, List<Integer>> lists = Utils.getNotEqualsElementsInLists(employeesId, existsEmployeesId);

        Map<String, Integer>[] batchValues = createBatchForInvestigationEmployee(investigationId, lists.first);
        namedParameterJdbcTemplate.batchUpdate(ADD_INVESTIGATION_EMPLOYEE_RELATIONS, batchValues);
    }

    @Override
    public int updateInvestigation(Investigation investigation) throws DataAccessException {

        LOGGER.debug("updateInvestigation(Investigation)");

        List<Employee> involvedStaff = investigation.getInvolvedStaff();

        if (involvedStaff != null) {

            List<Integer> employeesId = new ArrayList<>(involvedStaff.size());
            involvedStaff.forEach(item -> employeesId.add(item.getEmployeeId()));

            updateInvolvedStaffInInvestigation(investigation.getInvestigationId(), employeesId);
        }

        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(investigation);
        return namedParameterJdbcTemplate.update(UPDATE_INVESTIGATION, namedParameters);
    }

    @Override
    public int updateInvolvedStaffInInvestigation(Integer investigationId, List<Integer> employeesId) throws DataAccessException {

        LOGGER.debug("updateInvolvedStaffInInvestigation(Integer, List<Integer>)");

        int updatedRow = 0;

        List<Integer> removeEmployeeCandidates = namedParameterJdbcTemplate.queryForList(GET_INVESTIGATION_EMPLOYEES_ID,
                new MapSqlParameterSource(NamedParameterNames.INVESTIGATION_ID, investigationId), Integer.class);

        if (!employeesId.isEmpty()) {

            Pair<List<Integer>, List<Integer>> listPair = Utils.getNotEqualsElementsInLists(employeesId, removeEmployeeCandidates);

            List<Integer> addEmployees = listPair.first;
            if (!addEmployees.isEmpty()) {
                addInvolvedStaff2Investigation(investigationId, addEmployees);
                updatedRow += addEmployees.size();
            }

            removeEmployeeCandidates = listPair.second;
        }

        if (!removeEmployeeCandidates.isEmpty()) {
            Map<String, Integer>[] batchValues = createBatchForInvestigationEmployee(investigationId, removeEmployeeCandidates);
            namedParameterJdbcTemplate.batchUpdate(DELETE_INVESTIGATION_EMPLOYEE_RELATIONS, batchValues);

            updatedRow += removeEmployeeCandidates.size();
        }

        return updatedRow;
    }

    @Override
    public int deleteInvestigationById(Integer investigationId) throws DataAccessException {

        LOGGER.debug("deleteInvestigationByID(Integer)");

        return namedParameterJdbcTemplate.update(
                DELETE_INVESTIGATION_BY_ID,
                new MapSqlParameterSource(NamedParameterNames.INVESTIGATION_ID, investigationId)
        );
    }

    private Map<String, Integer>[] createBatchForInvestigationEmployee(Integer investigationId, List<Integer> employeesId) {

        return Utils.getBatchValues(
                (item) -> {
                    Map<String, Integer> rowNamedParameters = new HashMap<>();
                    rowNamedParameters.put(NamedParameterNames.INVESTIGATION_ID, investigationId);
                    rowNamedParameters.put(NamedParameterNames.EMPLOYEE_ID, employeesId.get(item));
                    return rowNamedParameters;
                },
                employeesId.size());
    }

    private static final class InvestigationRowMapper implements RowMapper<Investigation> {

        private static final Logger LOGGER = LogManager.getLogger();

        private static final DateTimeFormatter sDateTimeFormatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd HH:mm:ss")
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
                .toFormatter();

        private static final class BindNames {

            private static final String INVESTIGATION_ID = "investigation_id";
            private static final String INVESTIGATION_NUMBER = "investigation_number";
            private static final String TITLE = "title";
            private static final String DESCRIPTION = "description";
            private static final String START_DATE = "start_date";
            private static final String END_DATE = "end_date";
        }

        @Override
        public Investigation mapRow(ResultSet resultSet, int i) throws SQLException {

            LOGGER.debug("mapRow(ResultSet, int) - mapping returned ResultSet into Investigation object.");

            return new Investigation(
                    resultSet.getInt(BindNames.INVESTIGATION_ID),
                    resultSet.getInt(BindNames.INVESTIGATION_NUMBER),
                    resultSet.getString(BindNames.TITLE),
                    resultSet.getString(BindNames.DESCRIPTION),
                    covertString2OffsetDateTime(resultSet.getString(BindNames.START_DATE)),
                    covertString2OffsetDateTime(resultSet.getString(BindNames.END_DATE))
            );
        }

        private OffsetDateTime covertString2OffsetDateTime(String date) {

            if (date == null) return null;

            LocalDateTime localDateTime = LocalDateTime.parse(date, sDateTimeFormatter);
            return OffsetDateTime.of(localDateTime, ZoneOffset.UTC);
        }
    }
}
