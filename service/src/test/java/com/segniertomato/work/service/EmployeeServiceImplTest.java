package com.segniertomato.work.service;


import com.segniertomato.work.message.MessageError;
import com.segniertomato.work.model.Employee;
import com.segniertomato.work.model.Investigation;
import com.segniertomato.work.model.Pair;
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

import static org.junit.Assert.*;


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

    private static final Employee sFirstExistsEmployee;
    private static final Employee sSecondExistsEmployee;

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
                "Martin Luther King Jr. was assassinated by James Earl Ray in Memphis, Tennessee on April 4, 1968.",
                OffsetDateTime.parse("1968-04-04T08:50:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                OffsetDateTime.parse("1968-06-10T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        sThirdExistsInvestigation = new Investigation(3, 3,
                "Murders of Charles Moore and Henry Dee",
                "Charles Moore and Henry Dee were tortured and drowned by members of Ku Klux Klan in Franklin County, Mississippi.",
                OffsetDateTime.parse("1964-05-25T22:16:30Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                null);

        sFirstExistsEmployee = new Employee(3, "Frank Columbo",
                LocalDate.parse("1936-04-16", DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse("1953-11-16", DateTimeFormatter.ISO_LOCAL_DATE),
                Arrays.asList(sFirstExistsInvestigation, sSecondExistsInvestigation, sThirdExistsInvestigation));

        sSecondExistsEmployee = new Employee(1, "Nick Jeyrom", LocalDate.parse("1936-03-26", DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse("1956-09-25", DateTimeFormatter.ISO_LOCAL_DATE),
                Arrays.asList(sFirstExistsInvestigation, sThirdExistsInvestigation));
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

        sFirstExistsEmployee.setParticipatedInvestigations(Collections.emptyList());
        assertTrue(employees.contains(sFirstExistsEmployee));

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

        thrownException.expectMessage(MessageError.INVESTIGATION_NOT_EXISTS);
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

        Employee returnedEmployee = employeeService.getEmployeeById(sFirstExistsEmployee.getEmployeeId());

        sFirstExistsEmployee.setParticipatedInvestigations(Collections.emptyList());
        assertEquals(sFirstExistsEmployee, returnedEmployee);
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

        thrownException.expectMessage(MessageError.EMPLOYEE_NOT_EXISTS);
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
        newEmployee.setParticipatedInvestigations(Collections.emptyList());

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

        thrownException.expectMessage(MessageError.INVESTIGATION_NOT_EXISTS);
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

    @Test
    public void successfulAddInvestigations2EmployeeTest() throws Exception {

        LOGGER.debug("successfulAddInvestigations2EmployeeTest()");

        sSecondExistsEmployee.setParticipatedInvestigations(Collections.emptyList());

        List<Employee> involvedEmployees = employeeService.getInvolvedEmployeesInInvestigation(
                sThirdExistsInvestigation.getInvestigationId(), NULL_OFFSET, COUNT_ALL_EMPLOYEES);

        assertFalse(involvedEmployees.contains(sSecondExistsEmployee));

        employeeService.addInvestigations2Employee(sSecondExistsEmployee.getEmployeeId(), Arrays.asList(sThirdExistsInvestigation.getInvestigationId()));

        involvedEmployees = employeeService.getInvolvedEmployeesInInvestigation(
                sThirdExistsInvestigation.getInvestigationId(), NULL_OFFSET, COUNT_ALL_EMPLOYEES);

        assertTrue(involvedEmployees.contains(sSecondExistsEmployee));

    }

    @Test
    public void failureAddInvestigations2EmployeeTest_WithNullEmployeeId() throws Exception {

        LOGGER.debug("failureAddInvestigations2EmployeeTest_WithNullEmployeeId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.addInvestigations2Employee(null, Arrays.asList(sThirdExistsInvestigation.getInvestigationId()));
    }

    @Test
    public void failureAddInvestigations2EmployeeTest_WithInvalidEmployeeId() throws Exception {

        LOGGER.debug("failureAddInvestigations2EmployeeTest_WithInvalidEmployeeId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.addInvestigations2Employee(INVALID_ID, Arrays.asList(sThirdExistsInvestigation.getInvestigationId()));
    }

    @Test
    public void failureAddInvestigations2EmployeeTest_WithNotExistsEmployeeId() throws Exception {

        LOGGER.debug("failureAddInvestigations2EmployeeTest_WithNotExistsEmployeeId()");

        thrownException.expectMessage(MessageError.EMPLOYEE_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.addInvestigations2Employee(NOT_EXISTS_EMPLOYEE_ID, Arrays.asList(sThirdExistsInvestigation.getInvestigationId()));
    }

    @Test
    public void failureAddInvestigations2EmployeeTest_WithNullParticipatedInvestigations() throws Exception {

        LOGGER.debug("failureAddInvestigations2EmployeeTest_WithNullParticipatedInvestigations()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_PARTICIPATED_INVESTIGATIONS_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.addInvestigations2Employee(sSecondExistsEmployee.getEmployeeId(), null);
    }

    @Test
    public void failureAddInvestigations2EmployeeTest_WithNullInvestigationId() throws Exception {

        LOGGER.debug("failureAddInvestigations2EmployeeTest_WithNullInvestigationId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.addInvestigations2Employee(sSecondExistsEmployee.getEmployeeId(), Arrays.asList(sThirdExistsInvestigation.getInvestigationId(), null));
    }

    @Test
    public void failureAddInvestigations2EmployeeTest_WithNotExistsInvestigationId() throws Exception {

        LOGGER.debug("failureAddInvestigations2EmployeeTest_WithNotExistsInvestigationId()");

        thrownException.expectMessage(MessageError.INVESTIGATION_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.addInvestigations2Employee(sSecondExistsEmployee.getEmployeeId(), Arrays.asList(NOT_EXISTS_EMPLOYEE_ID));
    }

    @Test
    public void successfulUpdateEmployeeTest_WithoutParticipatedInvestigations() throws Exception {

        LOGGER.debug("successfulUpdateEmployeeTest_WithoutParticipatedInvestigations()");

        Employee updatedEmployee = new Employee(sFirstExistsEmployee.getEmployeeId(),
                "Lieutenant " + sFirstExistsEmployee.getName(),
                sFirstExistsEmployee.getAge().plusYears(2),
                sFirstExistsEmployee.getStartWorkingDate().plusYears(2),
                Collections.emptyList()
        );

        boolean isUpdated = employeeService.updateEmployee(updatedEmployee);
        assertTrue(isUpdated);

        List<Employee> allEmployees = employeeService.getAllEmployees(NULL_OFFSET, COUNT_ALL_EMPLOYEES);
        assertTrue(allEmployees.contains(updatedEmployee));

        sFirstExistsEmployee.getParticipatedInvestigations().forEach((item) -> {
            List<Employee> employees = employeeService.getInvolvedEmployeesInInvestigation(item.getInvestigationId(), NULL_OFFSET, COUNT_ALL_EMPLOYEES);
            assertFalse(employees.contains(updatedEmployee));
        });
    }

    @Test
    public void successfulUpdateEmployeeTest_WithParticipatedInvestigations() throws Exception {

        LOGGER.debug("successfulUpdateEmployeeTest_WithParticipatedInvestigations()");

        Employee updatedEmployee = new Employee(sFirstExistsEmployee.getEmployeeId(),
                "Lieutenant " + sFirstExistsEmployee.getName(),
                sFirstExistsEmployee.getAge().plusYears(2),
                sFirstExistsEmployee.getStartWorkingDate().plusYears(2),
                Arrays.asList(sFirstExistsInvestigation)
        );

        boolean isUpdated = employeeService.updateEmployee(updatedEmployee);
        assertTrue(isUpdated);

        updatedEmployee.setParticipatedInvestigations(Collections.emptyList());

        List<Employee> allEmployees = employeeService.getAllEmployees(NULL_OFFSET, COUNT_ALL_EMPLOYEES);
        assertTrue(allEmployees.contains(updatedEmployee));

        sFirstExistsEmployee.getParticipatedInvestigations().forEach((item) -> {
            List<Employee> employees = employeeService.getInvolvedEmployeesInInvestigation(item.getInvestigationId(), NULL_OFFSET, COUNT_ALL_EMPLOYEES);

            if (item.getInvestigationId().equals(sFirstExistsInvestigation.getInvestigationId())) {
                assertTrue(employees.contains(updatedEmployee));
            } else {
                assertFalse(employees.contains(updatedEmployee));
            }
        });
    }

    @Test
    public void failureUpdateEmployeeTest_WithNullEmployee() throws Exception {

        LOGGER.debug("failureUpdateEmployeeTest_WithNullEmployee()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.updateEmployee(null);
    }

    @Test
    public void failureUpdateEmployeeTest_WithNullParticipatedInvestigations() throws Exception {

        LOGGER.debug("failureUpdateEmployeeTest_WithNullParticipatedInvestigations()");

        Employee updatedEmployee = new Employee(sFirstExistsEmployee.getEmployeeId(),
                "Lieutenant " + sFirstExistsEmployee.getName(),
                sFirstExistsEmployee.getAge().plusYears(2),
                sFirstExistsEmployee.getStartWorkingDate().plusYears(2),
                null
        );

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_PARTICIPATED_INVESTIGATIONS_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.updateEmployee(updatedEmployee);
    }

    @Test
    public void failureUpdateEmployeeTest_WithNotExistsEmployee() throws Exception {

        LOGGER.debug("failureUpdateEmployeeTest_WithNotExistsEmployee()");

        Employee updatedEmployee = new Employee(NOT_EXISTS_EMPLOYEE_ID,
                "Lieutenant " + sFirstExistsEmployee.getName(),
                sFirstExistsEmployee.getAge().plusYears(2),
                sFirstExistsEmployee.getStartWorkingDate().plusYears(2),
                Arrays.asList(sFirstExistsInvestigation)
        );

        thrownException.expectMessage(MessageError.EMPLOYEE_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.updateEmployee(updatedEmployee);
    }

    @Test
    public void failureUpdateEmployeeTest_WithInvalidEmployeeId() throws Exception {

        LOGGER.debug("failureUpdateEmployeeTest_WithInvalidEmployeeId()");

        Employee updatedEmployee = new Employee(INVALID_ID,
                "Lieutenant " + sFirstExistsEmployee.getName(),
                sFirstExistsEmployee.getAge().plusYears(2),
                sFirstExistsEmployee.getStartWorkingDate().plusYears(2),
                Arrays.asList(sFirstExistsInvestigation)
        );

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.updateEmployee(updatedEmployee);
    }

    @Test
    public void failureUpdateEmployeeTest_WithNotExistsInvestigation() throws Exception {

        LOGGER.debug("failureUpdateEmployeeTest_WithNotExistsInvestigation()");

        List<Investigation> participatedInvestigations = Arrays.asList(
                new Investigation(NOT_EXISTS_INVESTIGATION_ID, 114, "Some title", "Some description",
                        OffsetDateTime.parse("1965-06-12T15:06:45Z"), OffsetDateTime.parse("1969-06-12T15:06:45Z"))
        );

        Employee updatedEmployee = new Employee(sFirstExistsEmployee.getEmployeeId(),
                "Lieutenant " + sFirstExistsEmployee.getName(),
                sFirstExistsEmployee.getAge().plusYears(2),
                sFirstExistsEmployee.getStartWorkingDate().plusYears(2),
                participatedInvestigations
        );

        thrownException.expectMessage(MessageError.INVESTIGATION_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.updateEmployee(updatedEmployee);
    }

    @Test
    public void failureUpdateEmployeeTest_WithInvalidParticipatedInvestigationId() throws Exception {

        LOGGER.debug("failureUpdateEmployeeTest_WithInvalidParticipatedInvestigationId()");

        Investigation investigation = new Investigation("Some other description",
                OffsetDateTime.parse("1965-06-12T15:06:45Z"), OffsetDateTime.parse("1969-06-12T15:06:45Z"));
        investigation.setInvestigationId(INVALID_ID);

        Employee updatedEmployee = new Employee(sFirstExistsEmployee.getEmployeeId(),
                "Lieutenant " + sFirstExistsEmployee.getName(),
                sFirstExistsEmployee.getAge().plusYears(2),
                sFirstExistsEmployee.getStartWorkingDate().plusYears(2),
                Arrays.asList(investigation)
        );

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.updateEmployee(updatedEmployee);
    }

    @Test
    public void failureUpdateEmployeeTest_WithNullParticipatedInvestigationId() throws Exception {

        LOGGER.debug("failureUpdateEmployeeTest_WithNullParticipatedInvestigationId()");

        Investigation investigation = new Investigation("Some other description",
                OffsetDateTime.parse("1965-06-12T15:06:45Z"), OffsetDateTime.parse("1969-06-12T15:06:45Z"));
        investigation.setInvestigationId(null);

        Employee updatedEmployee = new Employee(sFirstExistsEmployee.getEmployeeId(),
                "Lieutenant " + sFirstExistsEmployee.getName(),
                sFirstExistsEmployee.getAge().plusYears(2),
                sFirstExistsEmployee.getStartWorkingDate().plusYears(2),
                Arrays.asList(investigation)
        );

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.updateEmployee(updatedEmployee);
    }

    @Test
    public void successfulUpdateEmployeeInvestigationsTest() throws Exception {

        LOGGER.debug("successfulUpdateEmployeeInvestigationsTest()");

        List<Integer> investigationsId = Arrays.asList(sSecondExistsInvestigation.getInvestigationId());

        boolean isUpdate = employeeService.updateEmployeeInvestigations(sFirstExistsEmployee.getEmployeeId(), investigationsId);
        assertTrue(isUpdate);

        sFirstExistsEmployee.setParticipatedInvestigations(Collections.emptyList());

        List<Employee> allEmployees = employeeService.getAllEmployees(NULL_OFFSET, COUNT_ALL_EMPLOYEES);
        assertTrue(allEmployees.contains(sFirstExistsEmployee));

        Arrays.asList(sFirstExistsInvestigation.getInvestigationId(), sSecondExistsInvestigation.getInvestigationId(),
                sThirdExistsInvestigation.getInvestigationId()).forEach((item) -> {

            List<Employee> employees = employeeService.getInvolvedEmployeesInInvestigation(item, NULL_OFFSET, COUNT_ALL_EMPLOYEES);

            if (item.equals(sSecondExistsInvestigation.getInvestigationId())) {
                assertTrue(employees.contains(sFirstExistsEmployee));
            } else {
                assertFalse(employees.contains(sFirstExistsEmployee));
            }
        });
    }

    @Test
    public void failureUpdateEmployeeInvestigationsTest_WithNullEmployeeId() throws Exception {

        LOGGER.debug("failureUpdateEmployeeInvestigationsTest_WithNullEmployeeId(");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        List<Integer> investigationsId = Arrays.asList(sFirstExistsInvestigation.getInvestigationId());

        employeeService.updateEmployeeInvestigations(null, investigationsId);
    }

    @Test
    public void failureUpdateEmployeeInvestigationsTest_WithInvalidEmployeeId() throws Exception {

        LOGGER.debug("failureUpdateEmployeeInvestigationsTest_WithInvalidEmployeeId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        List<Integer> investigationsId = Arrays.asList(sFirstExistsInvestigation.getInvestigationId());

        employeeService.updateEmployeeInvestigations(INVALID_ID, investigationsId);
    }

    @Test
    public void failureUpdateEmployeeInvestigationsTest_WithNotExistsEmployeeId() throws Exception {

        LOGGER.debug("failureUpdateEmployeeInvestigationsTest_WithNotExistsEmployeeId()");

        thrownException.expectMessage(MessageError.EMPLOYEE_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        List<Integer> investigationsId = Arrays.asList(sFirstExistsInvestigation.getInvestigationId());

        employeeService.updateEmployeeInvestigations(NOT_EXISTS_EMPLOYEE_ID, investigationsId);
    }

    @Test
    public void failureUpdateEmployeeInvestigationsTest_WithNullParticipatedInvestigations() throws Exception {

        LOGGER.debug("failureUpdateEmployeeInvestigationsTest_WithNullParticipatedInvestigations()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_PARTICIPATED_INVESTIGATIONS_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.updateEmployeeInvestigations(sFirstExistsEmployee.getEmployeeId(), null);
    }

    @Test
    public void failureUpdateEmployeeInvestigationsTest_WithNullInvestigationId() throws Exception {

        LOGGER.debug("failureUpdateEmployeeInvestigationsTest_WithNullInvestigationId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        List<Integer> investigationsId = Arrays.asList(sFirstExistsInvestigation.getInvestigationId(), null);

        employeeService.updateEmployeeInvestigations(sFirstExistsEmployee.getEmployeeId(), investigationsId);
    }

    @Test
    public void failureUpdateEmployeeInvestigationsTest_WithNotExistsInvestigationId() throws Exception {

        LOGGER.debug("failureUpdateEmployeeInvestigationsTest_WithNotExistsInvestigationId()");

        thrownException.expectMessage(MessageError.INVESTIGATION_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.updateEmployeeInvestigations(sFirstExistsEmployee.getEmployeeId(), Arrays.asList(NOT_EXISTS_EMPLOYEE_ID));
    }

    @Test
    public void successfulDeleteEmployeeByIdTest() throws Exception {

        LOGGER.debug("successfulDeleteEmployeeByIdTest()");

        boolean isDeleted = employeeService.deleteEmployeeById(sSecondExistsEmployee.getEmployeeId());
        assertTrue(isDeleted);

        List<Employee> allEmployees = employeeService.getAllEmployees(NULL_OFFSET, COUNT_ALL_EMPLOYEES);
        assertFalse(allEmployees.contains(sSecondExistsEmployee));
    }

    @Test
    public void failureDeleteEmployeeByIdTest_WithNotExistsEmployee() throws Exception {

        LOGGER.debug("failureDeleteEmployeeByIdTest_WithNotExistsEmployee()");

        thrownException.expectMessage(MessageError.EMPLOYEE_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.deleteEmployeeById(NOT_EXISTS_EMPLOYEE_ID);
    }

    @Test
    public void failureDeleteEmployeeByIdTest_WithNullEmployeeId() throws Exception {

        LOGGER.debug("failureDeleteEmployeeByIdTest_WithNullEmployeeId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.deleteEmployeeById(null);
    }

    @Test
    public void failureDeleteEmployeeByIdTest_WithInvalidEmployeeId() throws Exception {

        LOGGER.debug("failureDeleteEmployeeByIdTest_WithInvalidEmployeeId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.deleteEmployeeById(INVALID_ID);
    }

    @Test
    public void successfulGetEmployeeRatingsTest() throws Exception {

        LOGGER.debug("successfulGetEmployeesRatingsTest()");

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
    public void failureGetEmployeesRatingsTest_WithWrongOffset() throws Exception {

        LOGGER.debug("failureGetEmployeesRatingsTest_WithInvalidOffset()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.OFFSET_CAN_NOT_BE_LOWER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.getEmployeesRating(INVALID_OFFSET, COUNT_ALL_EMPLOYEES);
    }

    @Test
    public void failureGetEmployeesRatingTest_WithInvalidLimit() throws Exception {

        LOGGER.debug("failureGetEmployeesRatingTest_WithInvalidLimit()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.LIMIT_CAN_NOT_BE_LOWER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        employeeService.getEmployeesRating(NULL_OFFSET, INVALID_COUNT_ALL_EMPLOYEES);
    }

}
