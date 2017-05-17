package com.segniertomato.work.service;


import com.segniertomato.work.message.MessageError;
import com.segniertomato.work.model.Employee;
import com.segniertomato.work.model.Investigation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:service-test.xml"})
@Transactional
public class EmployeeServiceImplTest {

    private static final Logger LOGGER = LogManager.getLogger();

    @Rule
    public ExpectedException thrownException = ExpectedException.none();

    @Autowired
    private EmployeeService employeeService;

    private static final int NULL_OFFSET = 0;
    private static final int COUNT_ALL_EMPLOYEES = 4;

    private static final int INVALID_ID = -5;
    private static final int INVALID_OFFSET = -5;
    private static final int INVALID_COUNT_ALL_EMPLOYEES = -5;

    private static final int VALID_INVESTIGATION_NUMBER = 777;

    private static final int NOT_EXISTS_EMPLOYEE_ID = 5;
    private static final int NOT_EXISTS_INVESTIGATION_ID = 5;

    private static final Employee sExistsEmployee;

    private static final Investigation sFirstExistsInvestigation;
    private static final Investigation sSecondExistsInvestigation;
    private static final Investigation sThirdExistsInvestigation;

    static {

        sFirstExistsInvestigation = new Investigation(1, 1,
                "Murders of Sharon Tate",
                "American actress and sex symbol Sharon Tate was murdered on August 1969 by members of the Charles Manson’s family.",
                OffsetDateTime.parse("1969-09-13T16:09:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                OffsetDateTime.parse("1972-02-04T13:26:16Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        sSecondExistsInvestigation = new Investigation(2, 2,
                "Assassination of Martin Luther King Jr.",
                "'Martin Luther King Jr. was assassinated by James Earl Ray in Memphis, Tennessee on April 4, 1968.'",
                OffsetDateTime.parse("1968-04-04T08:50:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                OffsetDateTime.parse("1968-06-10T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        sThirdExistsInvestigation = new Investigation(3, 3,
                "Murders of Charles Moore and Henry Dee",
                "Charles Moore and Henry Dee were tortured and drowned by members of Ku Klux Klan in Franklin County, Mississippi.",
                OffsetDateTime.parse("1964-05-25T22:16:30Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                null);

        sExistsEmployee = new Employee(3, "Frank Columbo",
                LocalDate.parse("1936-04-16", DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse("1953-11-16", DateTimeFormatter.ISO_LOCAL_DATE),
                Arrays.asList(sFirstExistsInvestigation, sSecondExistsInvestigation, sThirdExistsInvestigation));
    }


    @Test
    public void successfulGetAllEmployeesTest() throws Exception {

        LOGGER.debug("successfulGetAllEmployeesTest()");

        List<Employee> employees = employeeService.getAllEmployees(NULL_OFFSET, COUNT_ALL_EMPLOYEES);

        assertNotNull(employees);
        assertTrue(employees.size() == COUNT_ALL_EMPLOYEES);

        employees.forEach(Assert::assertNotNull);
        employees.forEach(System.out::println);
    }

    @Test
    public void successfulGetAllEmployeesTest_WithSomeLimit() throws Exception {

        LOGGER.debug("successfulGetAllEmployeesTest_WithSomeLimit()");

        int allowCount = 2;
        List<Employee> employees = employeeService.getAllEmployees(NULL_OFFSET, allowCount);

        assertNotNull(employees);
        assertTrue(employees.size() >= allowCount);

        employees.forEach(Assert::assertNotNull);
        employees.forEach(System.out::println);
    }

    @Test
    public void successfulGetAllEmployeesTest_WithSomeOffsetAndLimit() throws Exception {

        LOGGER.debug("successfulGetAllEmployeesTest_WithSomeLimit()");

        int allowCount = 2;
        int offset = 1;
        List<Employee> employees = employeeService.getAllEmployees(offset, allowCount);

        assertNotNull(employees);
        assertTrue(employees.size() >= allowCount);

        for (Employee item : employees) {
            assertNotNull(item);
            assertTrue(item.getEmployeeId() == ++offset);
            System.out.println(item);
        }
    }

    @Test
    public void failureGetAllEmployeesTest_WithInvalidOffset() throws Exception {

        LOGGER.debug("failureGetAllEmployeesTest_WithInvalidOffset");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.OFFSET_CAN_NOT_BE_LOWER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.getAllEmployees(INVALID_OFFSET, COUNT_ALL_EMPLOYEES);
    }

    @Test
    public void failureGetAllEmployeesTest_WithInvalidLimit() throws Exception {

        LOGGER.debug("failureGetAllEmployeesTest_WithInvalidLimit");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.LIMIT_CAN_NOT_BE_LOWER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.getAllEmployees(NULL_OFFSET, INVALID_COUNT_ALL_EMPLOYEES);
    }


    @Test
    public void successfulGetInvolvedEmployeesInInvestigationTest() throws Exception {

        LOGGER.debug("successfulGetInvolvedEmployeesInInvestigationTest()");

        List<Employee> employees = employeeService.getInvolvedEmployeesInInvestigation(
                sFirstExistsInvestigation.getInvestigationId(), NULL_OFFSET, COUNT_ALL_EMPLOYEES);
        assertNotNull(employees);

        sExistsEmployee.setParticipatedInvestigation(Collections.emptyList());
        assertTrue(employees.contains(sExistsEmployee));

    }

    @Test
    public void failureGetInvolvedEmployeesInInvestigationTest_WithInvalidOffset() throws Exception {

        LOGGER.debug("failureGetInvolvedEmployeesInInvestigationTest_WithInvalidOffset");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.OFFSET_CAN_NOT_BE_LOWER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.getInvolvedEmployeesInInvestigation(
                sFirstExistsInvestigation.getInvestigationId(), INVALID_OFFSET, COUNT_ALL_EMPLOYEES);
    }

    @Test
    public void failureGetInvolvedEmployeesInInvestigationTest_WithInvalidLimit() throws Exception {

        LOGGER.debug("failureGetInvolvedEmployeesInInvestigationTest_WithInvalidLimit");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.LIMIT_CAN_NOT_BE_LOWER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.getInvolvedEmployeesInInvestigation(
                sFirstExistsInvestigation.getInvestigationId(), NULL_OFFSET, INVALID_COUNT_ALL_EMPLOYEES);
    }

    @Test
    public void failureGetInvolvedEmployeesInInvestigationTest_WithNotExistsInvestigation() throws Exception {

        LOGGER.debug("failureGetInvolvedEmployeesInInvestigationTest_WithNotExistsInvestigation");

        thrownException.expectMessage(MessageError.Database.INVESTIGATION_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.getInvolvedEmployeesInInvestigation(NOT_EXISTS_INVESTIGATION_ID, NULL_OFFSET, COUNT_ALL_EMPLOYEES);
    }

    @Test
    public void failureGetInvolvedEmployeesInInvestigationTest_WithNullInvestigationId() throws Exception {

        LOGGER.debug("failureGetInvolvedEmployeesInInvestigationTest_WithNullInvestigationId");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.getInvolvedEmployeesInInvestigation(null, NULL_OFFSET, COUNT_ALL_EMPLOYEES);
    }

    @Test
    public void failureGetInvolvedEmployeesInInvestigationTest_WithInvalidInvestigationId() throws Exception {

        LOGGER.debug("failureGetInvolvedEmployeesInInvestigationTest_WithInvalidInvestigationId");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.getInvolvedEmployeesInInvestigation(INVALID_ID, NULL_OFFSET, COUNT_ALL_EMPLOYEES);
    }

    @Test
    public void successfulGetEmployeeByIdTest() throws Exception {

        LOGGER.debug("successfulGetEmployeeByIdTest()");

        Employee returnedEmployee = employeeService.getEmployeeById(sExistsEmployee.getEmployeeId());

        sExistsEmployee.setParticipatedInvestigation(Collections.emptyList());
        assertEquals(sExistsEmployee, returnedEmployee);
    }

    @Test
    public void failureGetEmployeeByIdTest_WithNullEmployeeId() {

        LOGGER.debug("failureGetEmployeeByIdTest_WithNullEmployeeId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.getEmployeeById(null);
    }

    @Test
    public void failureGetEmployeeByIdTest_WithInvalidEmployeeId() {

        LOGGER.debug("failureGetEmployeeByIdTest_WithInvalidEmployeeId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.getEmployeeById(INVALID_ID);
    }

    @Test
    public void failureGetEmployeeByIdTest_WithNotExistsEmployeeId() {

        LOGGER.debug("failureGetEmployeeByIdTest_WithInvalidEmployeeId()");

        thrownException.expectMessage(MessageError.Database.EMPLOYEE_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.getEmployeeById(NOT_EXISTS_EMPLOYEE_ID);
    }

    @Test
    public void successfulAddEmployeeTest_WithoutInvolvedStaff() throws Exception {

        LOGGER.debug("successfulAddEmployeeTest_WithoutInvolvedStaff()");

        Employee newEmployee = new Employee("John Wake",
                LocalDate.parse("1992-05-26", DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse("2016-05-23", DateTimeFormatter.ISO_LOCAL_DATE));

        Integer newEmployeeId = employeeService.addEmployee(newEmployee);
        assertNotNull(newEmployeeId);
        assertTrue(newEmployeeId > 0);

        newEmployee.setEmployeeId(newEmployeeId);

        List<Employee> employees = employeeService.getAllEmployees(NULL_OFFSET, COUNT_ALL_EMPLOYEES + 1);

        assertTrue(employees.contains(newEmployee));
    }

    @Test
    public void successfulAddEmployeeTest_WithInvolvedStaff() throws Exception {

        LOGGER.debug("successfulAddEmployeeTest_WithInvolvedStaff()");

        Employee newEmployee = new Employee("John Wake",
                LocalDate.parse("1992-05-26", DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse("2016-05-23", DateTimeFormatter.ISO_LOCAL_DATE),
                Arrays.asList(sFirstExistsInvestigation, sSecondExistsInvestigation));

        Integer newEmployeeId = employeeService.addEmployee(newEmployee);
        assertNotNull(newEmployeeId);
        assertTrue(newEmployeeId > 0);

        newEmployee.setEmployeeId(newEmployeeId);
        newEmployee.setParticipatedInvestigation(Collections.emptyList());

        List<Employee> employees = employeeService.getAllEmployees(NULL_OFFSET, COUNT_ALL_EMPLOYEES + 1);
        assertTrue(employees.contains(newEmployee));

        employees = employeeService.getInvolvedEmployeesInInvestigation(
                sFirstExistsInvestigation.getInvestigationId(), NULL_OFFSET, COUNT_ALL_EMPLOYEES + 1);
        assertTrue(employees.contains(newEmployee));

    }

    @Test
    public void failureAddEmployeeTest_WithInvalidEmployeeId() throws Exception {

        LOGGER.debug("failureAddEmployeeTest_WithInvalidEmployeeId()");

        Employee newEmployee = new Employee(NOT_EXISTS_EMPLOYEE_ID, "John Wake",
                LocalDate.parse("1992-05-26", DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse("2016-05-23", DateTimeFormatter.ISO_LOCAL_DATE));

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_NULL_OR_MINUS_ONE);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.addEmployee(newEmployee);
    }

    @Test
    public void failureAddEmployeeTest_WithInvalidEmployeeName() throws Exception {

        LOGGER.debug("failureAddEmployeeTest_WithInvalidEmployeeName()");

        Employee newEmployee = new Employee(".John Wake",
                LocalDate.parse("1992-05-26", DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse("2016-05-23", DateTimeFormatter.ISO_LOCAL_DATE));

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_NAME_SHOULD_MATCH_PATTERN);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.addEmployee(newEmployee);
    }

    @Test
    public void failureAddEmployeeTest_WithNullEmployee() throws Exception {

        LOGGER.debug("failureAddEmployeeTest_WithNullEmployee()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.addEmployee(null);
    }

    @Test
    public void failureAddEmployeeTest_WithNotExistsParticipatedInvestigation() throws Exception {

        LOGGER.debug("failureAddEmployeeTest_WithNotExistsInvalidParticipatedInvestigations()");

        Investigation invalidInvestigation = new Investigation(
                NOT_EXISTS_INVESTIGATION_ID, VALID_INVESTIGATION_NUMBER,
                "Murder of soft rabbit.",
                "I founded dead body in my room. Who is kill my soft rabbit?",
                OffsetDateTime.now(), null);

        Employee newEmployee = new Employee("John Wake",
                LocalDate.parse("1992-05-26", DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse("2016-05-23", DateTimeFormatter.ISO_LOCAL_DATE),
                Arrays.asList(invalidInvestigation));

        thrownException.expectMessage(MessageError.Database.INVESTIGATION_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.addEmployee(newEmployee);
    }
    @Test
    public void failureAddEmployeeTest_WithInvalidParticipatedInvestigationId() throws Exception {

        LOGGER.debug("failureAddEmployeeTest_WithInvalidInvalidParticipatedInvestigations()");

        Investigation invalidInvestigation = new Investigation(
                INVALID_ID, VALID_INVESTIGATION_NUMBER,
                "Murder of soft rabbit.",
                "I founded dead body in my room. Who is kill my soft rabbit?",
                OffsetDateTime.now(), null);

        Employee newEmployee = new Employee("John Wake",
                LocalDate.parse("1992-05-26", DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse("2016-05-23", DateTimeFormatter.ISO_LOCAL_DATE),
                Arrays.asList(invalidInvestigation));

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.addEmployee(newEmployee);
    }

    @Test
    public void failureAddEmployeeTest_WithNullParticipatedInvestigationId() throws Exception {

        LOGGER.debug("failureAddEmployeeTest_WithNullInvalidParticipatedInvestigations()");

        Investigation invalidInvestigation = new Investigation(
                null, VALID_INVESTIGATION_NUMBER,
                "Murder of soft rabbit.",
                "I founded dead body in my room. Who is kill my soft rabbit?",
                OffsetDateTime.now(), null);

        Employee newEmployee = new Employee("John Wake",
                LocalDate.parse("1992-05-26", DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse("2016-05-23", DateTimeFormatter.ISO_LOCAL_DATE),
                Arrays.asList(invalidInvestigation));

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.addEmployee(newEmployee);
    }

}
