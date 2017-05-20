package com.segniertomato.work.service;


import com.segniertomato.work.dao.EmployeeDao;
import com.segniertomato.work.message.MessageError;
import com.segniertomato.work.model.Employee;
import com.segniertomato.work.model.Investigation;
import com.segniertomato.work.model.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:service-test-mock.xml"})
public class EmployeeServiceImplMockTest {

    private static final Logger LOGGER = LogManager.getLogger();

    @Rule
    public ExpectedException thrownException = ExpectedException.none();

    @Autowired
    private EmployeeDao mockEmployeeDao;

    @Autowired
    private NamedParameterJdbcTemplate mockNamedParameterJdbcTemplate;

    @Autowired
    private EmployeeService employeeService;

    private static final int NULL_OFFSET = 0;
    private static final int COUNT_ALL_EMPLOYEES = 4;

    private static final int EXISTS_EMPLOYEE_ID = 2;
    private static final int EXISTS_INVESTIGATION_ID = 2;

    private static final int INVALID_ID = -5;
    private static final int INVALID_OFFSET = -5;
    private static final int INVALID_COUNT_ALL_EMPLOYEES = -5;

    private static final int NOT_EXISTS_EMPLOYEE_ID = 5;
    private static final int NOT_EXISTS_INVESTIGATION_ID = 5;

    private static final String VALID_EMPLOYEE_NAME = "Artur C. Clark";
    private static final String INVALID_EMPLOYEE_NAME = "Artur C.9Clark*";

    private static final LocalDate VALID_EMPLOYEE_AGE = LocalDate.parse("1989-05-15");
    private static final LocalDate VALID_EMPLOYEE_START_WORKING_DATE = LocalDate.parse("1992-06-25");

    private static final Employee sExpectedEmployee;

    static {

        sExpectedEmployee = new Employee(1, "Some name", VALID_EMPLOYEE_AGE, VALID_EMPLOYEE_START_WORKING_DATE);
    }

    @After
    public void clear() {

        verify(mockNamedParameterJdbcTemplate);
        reset(mockNamedParameterJdbcTemplate);

        verify(mockEmployeeDao);
        reset(mockEmployeeDao);
    }

    @Test
    public void successfulGetAllEmployeesTest() throws Exception {

        List<Employee> returnedEmployees = new ArrayList<>(COUNT_ALL_EMPLOYEES);

        replay(mockNamedParameterJdbcTemplate);

        expect(mockEmployeeDao.getAllEmployees(NULL_OFFSET, COUNT_ALL_EMPLOYEES)).andReturn(returnedEmployees);
        replay(mockEmployeeDao);

        List<Employee> employees = employeeService.getAllEmployees(NULL_OFFSET, COUNT_ALL_EMPLOYEES);

        assertNotNull(employees);
        employees.forEach(Assert::assertNotNull);
    }

    @Test
    public void successfulGetInvolvedEmployeesInInvestigationTest() throws Exception {

        LOGGER.debug("successfulGetInvolvedEmployeesInInvestigationTest()");

        List<Employee> expectedEmployees = Collections.emptyList();

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(1);
        replay(mockNamedParameterJdbcTemplate);

        expect(mockEmployeeDao.getInvolvedEmployeesInInvestigation(isA(Integer.class), anyInt(), anyInt())).andReturn(expectedEmployees);
        replay(mockEmployeeDao);

        List<Employee> employees = employeeService.getInvolvedEmployeesInInvestigation(EXISTS_INVESTIGATION_ID, NULL_OFFSET, COUNT_ALL_EMPLOYEES);

        assertNotNull(employees);
        employees.forEach(Assert::assertNotNull);
    }

    @Test
    public void failureGetInvolvedEmployeesInInvestigationTest_WithWrongOffset() throws Exception {

        LOGGER.debug("failureGetInvolvedEmployeesInInvestigationTest_WithWrongOffset()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.OFFSET_CAN_NOT_BE_LOWER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockEmployeeDao);

        employeeService.getInvolvedEmployeesInInvestigation(EXISTS_INVESTIGATION_ID, INVALID_OFFSET, COUNT_ALL_EMPLOYEES);
    }

    @Test
    public void failureGetInvolvedEmployeesInInvestigationTest_WithWrongLimit() throws Exception {

        LOGGER.debug("failureGetInvolvedEmployeesInInvestigationTest_WithWrongLimit()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.LIMIT_CAN_NOT_BE_LOWER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockEmployeeDao);

        employeeService.getInvolvedEmployeesInInvestigation(EXISTS_INVESTIGATION_ID, NULL_OFFSET, INVALID_COUNT_ALL_EMPLOYEES);
    }

    @Test
    public void failureGetInvolvedEmployeesInInvestigationTest_WithWrongInvestigationId() throws Exception {

        LOGGER.debug("failureGetInvolvedEmployeesInInvestigationTest_WithWrongInvestigationId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockEmployeeDao);

        employeeService.getInvolvedEmployeesInInvestigation(INVALID_ID, NULL_OFFSET, COUNT_ALL_EMPLOYEES);
    }

    @Test
    public void failureGetInvolvedEmployeesInInvestigationTest_WithNullInvestigationId() throws Exception {

        LOGGER.debug("failureGetInvolvedEmployeesInInvestigationTest_WithNullInvestigationId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockEmployeeDao);

        employeeService.getInvolvedEmployeesInInvestigation(null, NULL_OFFSET, COUNT_ALL_EMPLOYEES);
    }

    @Test
    public void failureGetInvolvedEmployeesInInvestigationTest_WithNotExistsInvestigationId() throws Exception {

        LOGGER.debug("failureGetInvolvedEmployeesInInvestigationTest_WithNotExistsInvestigationId()");

        thrownException.expectMessage(MessageError.INVESTIGATION_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(0);
        replay(mockNamedParameterJdbcTemplate);

        replay(mockEmployeeDao);

        employeeService.getInvolvedEmployeesInInvestigation(EXISTS_INVESTIGATION_ID, NULL_OFFSET, COUNT_ALL_EMPLOYEES);
    }

    @Test
    public void successfulGetEmployeeByIdTest() throws Exception {

        LOGGER.debug("successfulGetEmployeeByIdTest()");

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(1);
        replay(mockNamedParameterJdbcTemplate);

        expect(mockEmployeeDao.getEmployeeById(isA(Integer.class))).andReturn(sExpectedEmployee);
        replay(mockEmployeeDao);

        employeeService.getEmployeeById(sExpectedEmployee.getEmployeeId());
    }

    @Test
    public void failureGetEmployeeByIdTest_WithWrongEmployeeId() throws Exception {

        LOGGER.debug("failureGetEmployeeByIdTest_WithWrongEmployeeId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockEmployeeDao);

        employeeService.getEmployeeById(INVALID_ID);
    }

    @Test
    public void failureGetEmployeeByIdTest_WithNullEmployeeId() throws Exception {

        LOGGER.debug("failureGetEmployeeByIdTest_WithNullEmployeeId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockEmployeeDao);

        employeeService.getEmployeeById(null);
    }

    @Test
    public void failureGetEmployeeByIdTest_WithNotExistsEmployeeId() throws Exception {

        LOGGER.debug("failureGetEmployeeByIdTest_WithNotExistsEmployeeId()");

        thrownException.expectMessage(MessageError.EMPLOYEE_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(0);
        replay(mockNamedParameterJdbcTemplate);

        replay(mockEmployeeDao);

        employeeService.getEmployeeById(NOT_EXISTS_EMPLOYEE_ID);
    }

    @Test
    public void successfulAddEmployeeTest() throws Exception {

        LOGGER.debug("successfulAddEmployeeTest()");

        Integer newEmployeeId = 10;

        replay(mockNamedParameterJdbcTemplate);

        expect(mockEmployeeDao.addEmployee(isA(Employee.class))).andReturn(newEmployeeId);
        replay(mockEmployeeDao);

        employeeService.addEmployee(new Employee(VALID_EMPLOYEE_NAME, VALID_EMPLOYEE_AGE, VALID_EMPLOYEE_START_WORKING_DATE));
    }

    @Test
    public void failureAddEmployeeTest_WithInvalidEmployeeId() throws Exception {

        LOGGER.debug("failureAddEmployeeTest_WithInvalidEmployeeId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_NULL_OR_MINUS_ONE);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockEmployeeDao);

        employeeService.addEmployee(new Employee(INVALID_ID, VALID_EMPLOYEE_NAME, VALID_EMPLOYEE_AGE, VALID_EMPLOYEE_START_WORKING_DATE));
    }

    @Test
    public void failureAddEmployeeTest_WithInvalidEmployeeName() throws Exception {

        LOGGER.debug("failureAddEmployeeTest_WithInvalidEmployeeName()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_NAME_SHOULD_MATCH_PATTERN);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockEmployeeDao);

        employeeService.addEmployee(new Employee(INVALID_EMPLOYEE_NAME, VALID_EMPLOYEE_AGE, VALID_EMPLOYEE_START_WORKING_DATE));
    }

    @Test
    public void failureAddEmployeeTest_WithNullEmployee() throws Exception {

        LOGGER.debug("failureAddEmployeeTest_WithNullEmployee()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockEmployeeDao);

        employeeService.addEmployee(null);
    }

    @Test
    public void failureAddEmployeeTest_WithInvalidParticipatedInvestigations() throws Exception {

        LOGGER.debug("failureAddEmployeeTest_WithInvalidParticipatedInvestigations()");

        List<Investigation> invalidParticipatedInvestigation = Arrays.asList(
                new Investigation("Some description",
                        OffsetDateTime.parse("1965-06-12T15:06:45Z"), OffsetDateTime.parse("1969-06-12T15:06:45Z"))
        );

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockEmployeeDao);

        employeeService.addEmployee(
                new Employee(VALID_EMPLOYEE_NAME, VALID_EMPLOYEE_AGE, VALID_EMPLOYEE_START_WORKING_DATE, invalidParticipatedInvestigation));
    }

    @Test
    public void successfulAddInvestigations2EmployeeTest() throws Exception {

        LOGGER.debug("successfulAddInvestigations2EmployeeTest");

        List<Integer> addInvestigationIds = Arrays.asList(EXISTS_INVESTIGATION_ID);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class)))
                .andReturn(1)
                .atLeastOnce();
        replay(mockNamedParameterJdbcTemplate);

        mockEmployeeDao.addInvestigations2Employee(EXISTS_EMPLOYEE_ID, addInvestigationIds);
        expectLastCall();
        replay(mockEmployeeDao);

        employeeService.addInvestigations2Employee(EXISTS_EMPLOYEE_ID, addInvestigationIds);
    }

    @Test
    public void failureAddInvestigations2EmployeeTest_WithNullEmployeeId() throws Exception {

        LOGGER.debug("failureAddInvestigations2EmployeeTest_WithNullEmployeeId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockEmployeeDao);

        employeeService.addInvestigations2Employee(null, Arrays.asList(EXISTS_INVESTIGATION_ID));
    }

    @Test
    public void failureAddInvestigations2EmployeeTest_WithInvalidEmployeeId() throws Exception {

        LOGGER.debug("failureAddInvestigations2EmployeeTest_WithInvalidEmployeeId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockEmployeeDao);

        employeeService.addInvestigations2Employee(INVALID_ID, Arrays.asList(EXISTS_INVESTIGATION_ID));
    }

    @Test
    public void failureAddInvestigations2EmployeeTest_WithNotExistsEmployeeId() throws Exception {

        LOGGER.debug("failureAddInvestigations2EmployeeTest_WithNotExistsEmployeeId()");

        thrownException.expectMessage(MessageError.EMPLOYEE_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(0);
        replay(mockNamedParameterJdbcTemplate);
        replay(mockEmployeeDao);

        employeeService.addInvestigations2Employee(NOT_EXISTS_EMPLOYEE_ID, Arrays.asList(EXISTS_INVESTIGATION_ID));
    }

    @Test
    public void failureAddInvestigations2EmployeeTest_WithNullParticipatedInvestigations() throws Exception {

        LOGGER.debug("failureAddInvestigations2EmployeeTest_WithNullIParticipatedInvestigations()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_PARTICIPATED_INVESTIGATIONS_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(1);
        replay(mockNamedParameterJdbcTemplate);

        replay(mockEmployeeDao);

        employeeService.addInvestigations2Employee(EXISTS_EMPLOYEE_ID, null);
    }

    @Test
    public void failureAddInvestigations2EmployeeTest_WithNullInvestigationId() throws Exception {

        LOGGER.debug("failureAddInvestigations2EmployeeTest_WithNullInvestigationId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class)))
                .andReturn(1)
                .times(2);
        replay(mockNamedParameterJdbcTemplate);

        replay(mockEmployeeDao);

        employeeService.addInvestigations2Employee(EXISTS_EMPLOYEE_ID, Arrays.asList(EXISTS_INVESTIGATION_ID, null));
    }

    @Test
    public void failureAddInvestigations2EmployeeTest_WithNotExistsInvestigationId() throws Exception {

        LOGGER.debug("failureAddInvestigations2EmployeeTest_WithNotExistsInvestigationId()");

        thrownException.expectMessage(MessageError.INVESTIGATION_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(1);
        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(0);
        replay(mockNamedParameterJdbcTemplate);

        replay(mockEmployeeDao);

        employeeService.addInvestigations2Employee(EXISTS_EMPLOYEE_ID, Arrays.asList(NOT_EXISTS_EMPLOYEE_ID));
    }

    @Test
    public void successfulUpdateEmployeeTest_WithoutParticipatedInvestigations() throws Exception {

        LOGGER.debug("successfulUpdateEmployeeTest_WithoutParticipatedInvestigations()");

        Employee updatedEmployee = new Employee(sExpectedEmployee.getEmployeeId(),
                "Some new Name", LocalDate.parse("1990-11-05"), sExpectedEmployee.getStartWorkingDate());

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(1);
        replay(mockNamedParameterJdbcTemplate);

        expect(mockEmployeeDao.updateEmployee(isA(Employee.class))).andReturn(1);
        replay(mockEmployeeDao);

        boolean isUpdate = employeeService.updateEmployee(updatedEmployee);
        assertTrue(isUpdate);
    }

    @Test
    public void successfulUpdateEmployeeTest_WithParticipatedInvestigations() throws Exception {

        LOGGER.debug("successfulUpdateEmployeeTest_WithParticipatedInvestigations()");

        List<Investigation> participatedInvestigations = Arrays.asList(
                new Investigation(EXISTS_INVESTIGATION_ID, 114, "Some title", "Some description",
                        OffsetDateTime.parse("1965-06-12T15:06:45Z"), OffsetDateTime.parse("1969-06-12T15:06:45Z")),

                new Investigation(EXISTS_INVESTIGATION_ID + 1, 115, "Some other title", "Some other description",
                        OffsetDateTime.parse("1965-06-12T15:06:45Z"), OffsetDateTime.parse("1969-06-12T15:06:45Z"))
        );
        Employee updatedEmployee = new Employee(sExpectedEmployee.getEmployeeId(),
                "Some new Name", LocalDate.parse("1990-11-05"), sExpectedEmployee.getStartWorkingDate(), participatedInvestigations);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class)))
                .andReturn(1)
                .times(3);
        replay(mockNamedParameterJdbcTemplate);

        expect(mockEmployeeDao.updateEmployee(isA(Employee.class))).andReturn(1);
        replay(mockEmployeeDao);

        boolean isUpdate = employeeService.updateEmployee(updatedEmployee);
        assertTrue(isUpdate);
    }

    @Test
    public void failureUpdateEmployeeTest_WithNullEmployee() throws Exception {

        LOGGER.debug("failureUpdateEmployeeTest_WithNullEmployee()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockEmployeeDao);

        employeeService.updateEmployee(null);
    }

    @Test
    public void failureUpdateEmployeeTest_WithNullParticipatedInvestigations() throws Exception {

        LOGGER.debug("failureUpdateEmployeeTest_WithNullParticipatedInvestigations()");

        Employee updatedEmployee = new Employee(sExpectedEmployee.getEmployeeId(),
                "Some new Name", LocalDate.parse("1990-11-05"), sExpectedEmployee.getStartWorkingDate(), null);

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_PARTICIPATED_INVESTIGATIONS_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(1);
        replay(mockNamedParameterJdbcTemplate);
        replay(mockEmployeeDao);

        employeeService.updateEmployee(updatedEmployee);
    }

    @Test
    public void failureUpdateEmployeeTest_WithNotExistsEmployee() throws Exception {

        LOGGER.debug("failureUpdateEmployeeTest_WithNotExistsEmployee()");

        Employee updatedEmployee = new Employee(NOT_EXISTS_EMPLOYEE_ID,
                "Some new Name", LocalDate.parse("1990-11-05"), sExpectedEmployee.getStartWorkingDate());

        thrownException.expectMessage(MessageError.EMPLOYEE_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(0);
        replay(mockNamedParameterJdbcTemplate);
        replay(mockEmployeeDao);

        employeeService.updateEmployee(updatedEmployee);
    }

    @Test
    public void failureUpdateEmployeeTest_WithNotExistsInvestigation() throws Exception {

        LOGGER.debug("failureUpdateEmployeeTest_WithNotExistsInvestigation()");

        List<Investigation> participatedInvestigations = Arrays.asList(
                new Investigation(EXISTS_INVESTIGATION_ID, 114, "Some title", "Some description",
                        OffsetDateTime.parse("1965-06-12T15:06:45Z"), OffsetDateTime.parse("1969-06-12T15:06:45Z")),

                new Investigation(NOT_EXISTS_INVESTIGATION_ID, 115, "Some another title", "Some other description",
                        OffsetDateTime.parse("1965-06-12T15:06:45Z"), OffsetDateTime.parse("1969-06-12T15:06:45Z"))
        );
        Employee updatedEmployee = new Employee(NOT_EXISTS_EMPLOYEE_ID,
                "Some new Name", LocalDate.parse("1990-11-05"), sExpectedEmployee.getStartWorkingDate(), participatedInvestigations);

        thrownException.expectMessage(MessageError.INVESTIGATION_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class)))
                .andReturn(1)
                .times(2);
        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class)))
                .andReturn(0);
        replay(mockNamedParameterJdbcTemplate);

        replay(mockEmployeeDao);

        employeeService.updateEmployee(updatedEmployee);
    }

    @Test
    public void failureUpdateEmployeeTest_WithNullParticipatedInvestigationId() throws Exception {

        LOGGER.debug("failureUpdateEmployeeTest_WithNullParticipatedInvestigationId()");

        Investigation investigation = new Investigation("Some other description",
                OffsetDateTime.parse("1965-06-12T15:06:45Z"), OffsetDateTime.parse("1969-06-12T15:06:45Z"));
        investigation.setInvestigationId(null);

        Employee updatedEmployee = new Employee(NOT_EXISTS_EMPLOYEE_ID,
                "Some new Name", LocalDate.parse("1990-11-05"), sExpectedEmployee.getStartWorkingDate(), Arrays.asList(investigation));

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(1);
        replay(mockNamedParameterJdbcTemplate);

        replay(mockEmployeeDao);

        employeeService.updateEmployee(updatedEmployee);
    }

    @Test
    public void successfulUpdateEmployeeInvestigationsTest() throws Exception {

        LOGGER.debug("successfulUpdateEmployeeInvestigationsTest()");

        List<Integer> investigationsId = Arrays.asList(EXISTS_INVESTIGATION_ID);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class)))
                .andReturn(1)
                .atLeastOnce();
        replay(mockNamedParameterJdbcTemplate);

        expect(mockEmployeeDao.updateEmployeeInvestigations(EXISTS_EMPLOYEE_ID, investigationsId)).andReturn(2);
        replay(mockEmployeeDao);

        boolean isUpdate = employeeService.updateEmployeeInvestigations(EXISTS_EMPLOYEE_ID, investigationsId);
        assertTrue(isUpdate);
    }

    @Test
    public void failureUpdateEmployeeInvestigationsTest_WithNullEmployeeId() throws Exception {

        LOGGER.debug("failureUpdateEmployeeInvestigationsTest_WithNullEmployeeId(");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockEmployeeDao);

        employeeService.updateEmployeeInvestigations(null, Arrays.asList(EXISTS_INVESTIGATION_ID));
    }

    @Test
    public void failureUpdateEmployeeInvestigationsTest_WithInvalidEmployeeId() throws Exception {

        LOGGER.debug("failureUpdateEmployeeInvestigationsTest_WithInvalidEmployeeId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockEmployeeDao);

        employeeService.updateEmployeeInvestigations(INVALID_ID, Arrays.asList(EXISTS_INVESTIGATION_ID));
    }

    @Test
    public void failureUpdateEmployeeInvestigationsTest_WithNotExistsEmployeeId() throws Exception {

        LOGGER.debug("failureUpdateEmployeeInvestigationsTest_WithNotExistsEmployeeId()");

        thrownException.expectMessage(MessageError.EMPLOYEE_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(0);
        replay(mockNamedParameterJdbcTemplate);
        replay(mockEmployeeDao);

        employeeService.updateEmployeeInvestigations(NOT_EXISTS_EMPLOYEE_ID, Arrays.asList(EXISTS_INVESTIGATION_ID));
    }

    @Test
    public void failureUpdateEmployeeInvestigationsTest_WithNullParticipatedInvestigations() throws Exception {

        LOGGER.debug("failureUpdateEmployeeInvestigationsTest_WithNullParticipatedInvestigations()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_PARTICIPATED_INVESTIGATIONS_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(1);
        replay(mockNamedParameterJdbcTemplate);

        replay(mockEmployeeDao);

        employeeService.updateEmployeeInvestigations(EXISTS_EMPLOYEE_ID, null);
    }

    @Test
    public void failureUpdateEmployeeInvestigationsTest_WithNullInvestigationId() throws Exception {

        LOGGER.debug("failureUpdateEmployeeInvestigationsTest_WithNullInvestigationId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class)))
                .andReturn(1)
                .times(2);
        replay(mockNamedParameterJdbcTemplate);

        replay(mockEmployeeDao);

        employeeService.updateEmployeeInvestigations(EXISTS_EMPLOYEE_ID, Arrays.asList(EXISTS_INVESTIGATION_ID, null));
    }

    @Test
    public void failureUpdateEmployeeInvestigationsTest_WithNotExistsInvestigationId() throws Exception {

        LOGGER.debug("failureUpdateEmployeeInvestigationsTest_WithNotExistsInvestigationId()");

        thrownException.expectMessage(MessageError.INVESTIGATION_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(1);
        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(0);
        replay(mockNamedParameterJdbcTemplate);

        replay(mockEmployeeDao);

        employeeService.updateEmployeeInvestigations(EXISTS_EMPLOYEE_ID, Arrays.asList(NOT_EXISTS_EMPLOYEE_ID));
    }

    @Test
    public void successfulDeleteEmployeeByIdTest() throws Exception {

        LOGGER.debug("successfulDeleteEmployeeByIdTest()");

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(1);
        replay(mockNamedParameterJdbcTemplate);

        expect(mockEmployeeDao.deleteEmployeeById(isA(Integer.class))).andReturn(1);
        replay(mockEmployeeDao);

        boolean isDeleted = employeeService.deleteEmployeeById(EXISTS_EMPLOYEE_ID);
        assertTrue(isDeleted);
    }

    @Test
    public void failureDeleteEmployeeByIdTest_WithNotExistsEmployee() throws Exception {

        LOGGER.debug("failureDeleteEmployeeByIdTest_WithNotExistsEmployee()");

        thrownException.expectMessage(MessageError.EMPLOYEE_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(0);
        replay(mockNamedParameterJdbcTemplate);

        replay(mockEmployeeDao);

        employeeService.deleteEmployeeById(NOT_EXISTS_EMPLOYEE_ID);
    }

    @Test
    public void failureDeleteEmployeeByIdTest_WithNullEmployeeId() throws Exception {

        LOGGER.debug("failureDeleteEmployeeByIdTest_WithNullEmployeeId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockEmployeeDao);

        employeeService.deleteEmployeeById(null);
    }

    @Test
    public void failureDeleteEmployeeByIdTest_WithInvalidEmployeeId() throws Exception {

        LOGGER.debug("failureDeleteEmployeeByIdTest_WithInvalidEmployeeId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockEmployeeDao);

        employeeService.deleteEmployeeById(INVALID_ID);
    }

    @Test
    public void successfulGetEmployeeRatingsTest() throws Exception {

        LOGGER.debug("successfulGetEmployeesRatingsTest()");

        replay(mockNamedParameterJdbcTemplate);

        expect(mockEmployeeDao.getEmployeesRating(anyInt(), anyInt()))
                .andReturn(Arrays.asList(new Pair<>(EXISTS_EMPLOYEE_ID, 66), new Pair<>(EXISTS_EMPLOYEE_ID + 1, 87)));
        replay(mockEmployeeDao);

        List<Pair<Integer, Integer>> employeesRatings = employeeService.getEmployeesRating(NULL_OFFSET, COUNT_ALL_EMPLOYEES);

        assertNotNull(employeesRatings);
        employeesRatings.forEach((item) -> {

            assertNotNull(item);

            assertNotNull(item.first);
            assertTrue(item.first > 0);

            assertNotNull(item.second);
            assertTrue(item.second >= 0 && item.second <= 100);
        });


    }

    @Test
    public void failureGetEmployeesRatingsTest_WithInvalidOffset() throws Exception {

        LOGGER.debug("failureGetEmployeesRatingsTest_WithInvalidOffset()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.OFFSET_CAN_NOT_BE_LOWER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockEmployeeDao);

        employeeService.getEmployeesRating(INVALID_OFFSET, COUNT_ALL_EMPLOYEES);
    }

    @Test
    public void failureGetEmployeesRatingTest_WithInvalidLimit() throws Exception {

        LOGGER.debug("failureGetEmployeesRatingTest_WithInvalidLimit()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.LIMIT_CAN_NOT_BE_LOWER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockEmployeeDao);

        employeeService.getEmployeesRating(NULL_OFFSET, INVALID_COUNT_ALL_EMPLOYEES);
    }
}
