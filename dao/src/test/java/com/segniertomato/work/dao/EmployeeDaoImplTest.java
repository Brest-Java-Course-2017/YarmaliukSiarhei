package com.segniertomato.work.dao;


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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
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
@ContextConfiguration(locations = {"classpath*:test-spring-dao.xml"})
@Transactional
public class EmployeeDaoImplTest {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private EmployeeDao employeeDao;

    @Rule
    public ExpectedException thrownException = ExpectedException.none();


    private static final int COUNT_ALL_EMPLOYEE = 4;
    private static final int NULL_OFFSET = 0;
    private static final int NOT_EXISTS_EMPLOYEE_ID = 5;

    private static final Employee sFirstExistsEmployee;
    private static final Investigation sFirstExistsInvestigation;
    private static final Investigation sSecondExistsInvestigation;


    static {
        sFirstExistsEmployee = new Employee(1, "Nick Jeyrom", LocalDate.parse("1936-03-26", DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse("1956-09-25", DateTimeFormatter.ISO_LOCAL_DATE));

        sFirstExistsInvestigation = new Investigation("Murders of Sharon Tate",
                "American actress and sex symbol Sharon Tate was murdered on August 1969 by members of the Charles Manson’s family.",
                OffsetDateTime.parse("1969-09-13T16:09:00Z"), OffsetDateTime.parse("1972-02-04T13:26:16Z"));
        sFirstExistsInvestigation.setInvestigationId(1);

        sSecondExistsInvestigation = new Investigation("Assassination of Martin Luther King Jr.",
                "Martin Luther King Jr. was assassinated by James Earl Ray in Memphis, Tennessee on April 4, 1968.",
                OffsetDateTime.parse("1968-04-04T08:50:00Z"), OffsetDateTime.parse("1968-04-10T00:00:00Z"));
        sSecondExistsInvestigation.setInvestigationId(2);
    }

    @Test
    public void successfulGetAllEmployeesTest() throws Exception {

        LOGGER.debug("successfulGetAllEmployeesTest()");

        List<Employee> returnedEmployees = employeeDao.getAllEmployees(NULL_OFFSET, COUNT_ALL_EMPLOYEE);

        assertNotNull(returnedEmployees);
        assertTrue(returnedEmployees.size() == COUNT_ALL_EMPLOYEE);

        LOGGER.debug("successfulGetAllEmployeesTest() - count of returned Employees: {}", returnedEmployees.size());

        returnedEmployees.forEach(Assert::assertNotNull);

    }

    @Test
    public void successfulGetAllEmployeesTest_WithOffsetAndLimit() throws Exception {

        LOGGER.debug("successfulGetAllEmployeesTest_WithOffsetAndLimit()");

        int allowsReturnedEmployees = 2;
        List<Employee> returnedEmployees = employeeDao.getAllEmployees(NULL_OFFSET + 1, allowsReturnedEmployees);

        assertNotNull(returnedEmployees);
        assertTrue(returnedEmployees.size() <= allowsReturnedEmployees);

        LOGGER.debug("successfulGetAllEmployeesTest_WithOffsetAndLimit() - count of returned Employees: {}", returnedEmployees.size());

        returnedEmployees.forEach(Assert::assertNotNull);
    }

    @Test
    public void successfulGetAllEmployeesTest_WithOutOfBoundsRange() throws Exception {

        LOGGER.debug("successfulGetAllEmployeesTest_WithOutOfBoundsRange()");

        int allowsReturnedEmployeesCount = 5;
        List<Employee> returnedEmployees = employeeDao.getAllEmployees(NULL_OFFSET + COUNT_ALL_EMPLOYEE, allowsReturnedEmployeesCount);

        assertNotNull(returnedEmployees);

        LOGGER.debug("successfulGetAllInvestigationsTest_WithOutOfBoundsRange() - count of returned Employees: {}", returnedEmployees.size());
        assertTrue(returnedEmployees.isEmpty());
    }

    @Test
    public void successfulGetInvolvedEmployeesInInvestigationTest() throws Exception {

        LOGGER.debug("successfulGetInvolvedEmployeesInInvestigationTest()");

        int investigationId = 2;
        int expectedEmployeesCount = 3;

        List<Employee> returnedEmployees = employeeDao.getInvolvedEmployeesInInvestigation(investigationId, NULL_OFFSET, COUNT_ALL_EMPLOYEE);

        assertNotNull(returnedEmployees);

        LOGGER.debug("successfulGetInvolvedEmployeesInInvestigationTest() - count of returned Employees: {}", returnedEmployees.size());

        assertTrue(returnedEmployees.size() == expectedEmployeesCount);

    }

    @Test
    public void successfulGetInvolvedEmployeesInInvestigationTest_WithNotExistsInvestigationId() throws Exception {

        LOGGER.debug("successfulGetInvolvedEmployeesInInvestigationTest_WithNotExistsInvestigationId()");

        List<Employee> returnedEmployees = employeeDao.getInvolvedEmployeesInInvestigation(NOT_EXISTS_EMPLOYEE_ID, NULL_OFFSET, COUNT_ALL_EMPLOYEE);
        assertNotNull(returnedEmployees);

        LOGGER.debug("successfulGetInvolvedEmployeesInInvestigationTest() - count of returned Employees: {}", returnedEmployees.size());
        assertTrue(returnedEmployees.isEmpty());

    }

    @Test
    public void successfulGetInvolvedEmployeesInInvestigationTest_WithNullInvestigationId() throws Exception {

        LOGGER.debug("successfulGetInvolvedEmployeesInInvestigationTest_WithNullInvestigationId()");

        List<Employee> returnedEmployees = employeeDao.getInvolvedEmployeesInInvestigation(null, NULL_OFFSET, COUNT_ALL_EMPLOYEE);
        assertNotNull(returnedEmployees);

        LOGGER.debug("successfulGetInvolvedEmployeesInInvestigationTest() - count of returned Employees: {}", returnedEmployees.size());
        assertTrue(returnedEmployees.isEmpty());

    }

    @Test
    public void successfulGetEmployeeByIdTest() throws Exception {

        LOGGER.debug("successfulGetEmployeeByIdTest()");

        Employee returnedEmployee = employeeDao.getEmployeeById(sFirstExistsEmployee.getEmployeeId());

        assertNotNull(returnedEmployee);
        assertEquals(sFirstExistsEmployee, returnedEmployee);
    }

    @Test
    public void failureGetEmployeeByIdTest_WithNotExistsEmployeeId() throws Exception {

        LOGGER.debug("failureGetEmployeeByIdTest_WithNotExistsEmployeeId()");

        thrownException.expect(EmptyResultDataAccessException.class);
        employeeDao.getEmployeeById(NOT_EXISTS_EMPLOYEE_ID);
    }

    @Test
    public void failureGetEmployeeByIdTest_WithNullEmployeeId() throws Exception {

        LOGGER.debug("failureGetEmployeeByIdTest_WithNullEmployeeId()");

        thrownException.expect(EmptyResultDataAccessException.class);
        employeeDao.getEmployeeById(null);
    }

    @Test
    public void successfulAddEmployeeTest_WithoutParticipatedInvestigations() throws Exception {

        LOGGER.debug("successfulAddEmployeeTest_WithoutParticipatedInvestigations()");

        Employee newEmployee = new Employee(
                "Artur Clark",
                LocalDate.parse("1917-12-16", DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse("1937-05-26", DateTimeFormatter.ISO_LOCAL_DATE));

        Integer returnedEmployeeId = employeeDao.addEmployee(newEmployee);

        assertNotNull(returnedEmployeeId);

        LOGGER.debug("successfulAddEmployeeTest() - returned Employee id: {}", returnedEmployeeId);
        assertTrue(returnedEmployeeId > 0);

        newEmployee.setEmployeeId(returnedEmployeeId);

        List<Employee> employees = employeeDao.getAllEmployees(NULL_OFFSET, COUNT_ALL_EMPLOYEE + 1);
        assertTrue(employees.contains(newEmployee));

        Employee returnedEmployee = employeeDao.getEmployeeById(newEmployee.getEmployeeId());
        assertEquals(newEmployee, returnedEmployee);
    }

    @Test
    public void successfulAddEmployeeTest_WithParticipatedInvestigations() throws Exception {

        LOGGER.debug("successfulAddEmployeeTest_WithParticipatedInvestigations()");

        Employee newEmployee = new Employee(
                "Artur Clark",
                LocalDate.parse("1917-12-16", DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse("1937-05-26", DateTimeFormatter.ISO_LOCAL_DATE),
                Arrays.asList(sFirstExistsInvestigation, sSecondExistsInvestigation));

        Integer returnedEmployeeId = employeeDao.addEmployee(newEmployee);

        assertNotNull(returnedEmployeeId);

        LOGGER.debug("successfulAddEmployeeTest() - returned Employee id: {}", returnedEmployeeId);
        assertTrue(returnedEmployeeId > 0);

        newEmployee.setEmployeeId(returnedEmployeeId);
        newEmployee.setParticipatedInvestigation(Collections.emptyList());


        Arrays.asList(sFirstExistsInvestigation.getInvestigationId(), sSecondExistsInvestigation.getInvestigationId())
                .forEach((itemId) -> {

                    List<Employee> returnedEmployees =
                            employeeDao.getInvolvedEmployeesInInvestigation(itemId, NULL_OFFSET, COUNT_ALL_EMPLOYEE + 1);
                    assertTrue(returnedEmployees.contains(newEmployee));
                });
    }

    @Test
    public void failureAddEmployeeTest_WithNullEmployeeObject() throws Exception {

        LOGGER.debug("failureAddEmployeeTest_WithNullEmployeeObject()");

        thrownException.expect(IllegalArgumentException.class);
        employeeDao.addEmployee(null);
    }

    @Test
    public void successfulAddInvestigations2EmployeeTest() throws Exception {

        LOGGER.debug("successfulAddInvestigations2EmployeeTest()");

        List<Integer> addInvestigations = Arrays.asList(sFirstExistsInvestigation.getInvestigationId(), sSecondExistsInvestigation.getInvestigationId());

        employeeDao.addInvestigations2Employee(sFirstExistsEmployee.getEmployeeId(), addInvestigations);

        LOGGER.debug("successfulAddInvestigations2EmployeeTest() - check added investigations.");

        addInvestigations.forEach((itemId) -> {

            List<Employee> returnedEmployees =
                    employeeDao.getInvolvedEmployeesInInvestigation(itemId, NULL_OFFSET, COUNT_ALL_EMPLOYEE + 1);
            assertTrue(returnedEmployees.contains(sFirstExistsEmployee));
        });
    }

    @Test
    public void failureAddInvestigations2EmployeeTest_WithNotExistsIds() throws Exception {

        LOGGER.debug("failureAddInvestigations2EmployeeTest_WithNotExistsIds()");

        thrownException.expect(NullPointerException.class);
        employeeDao.addInvestigations2Employee(null, null);
    }

    @Test
    public void failureAddInvestigations2EmployeeTest_WithNullInvestigations() throws Exception {

        LOGGER.debug("failureAddInvestigations2EmployeeTest_WithNullInvestigations()");

        thrownException.expect(NullPointerException.class);
        employeeDao.addInvestigations2Employee(sFirstExistsEmployee.getEmployeeId(), null);
    }

    @Test
    public void failureAddInvestigations2EmployeeTest_WithNullEmployeeId() throws Exception {

        LOGGER.debug("failureAddInvestigations2EmployeeTest_WithNullEmployeeId()");

        thrownException.expect(DataIntegrityViolationException.class);

        List<Integer> addInvestigations = Arrays.asList(sFirstExistsInvestigation.getInvestigationId(), sSecondExistsInvestigation.getInvestigationId());
        employeeDao.addInvestigations2Employee(null, addInvestigations);
    }


    @Test
    public void successfulUpdateEmployeeTest_WithoutInvestigations() throws Exception {

        LOGGER.debug("successfulUpdateEmployeeTest_WithoutInvestigations()");

        Employee updateEmployee = new Employee(
                sFirstExistsEmployee.getEmployeeId(),
                "Isaac Asimov",
                LocalDate.parse("1920-01-02", DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse("1931-01-01", DateTimeFormatter.ISO_LOCAL_DATE));

        int updatedRow = employeeDao.updateEmployee(updateEmployee);
        assertTrue(updatedRow >= 0);

        List<Employee> employees = employeeDao.getAllEmployees(NULL_OFFSET, COUNT_ALL_EMPLOYEE);

        assertNotNull(employees);
        assertTrue(employees.contains(updateEmployee));
    }

    @Test
    public void successfulUpdateEmployeeTest_WithInvestigations() throws Exception {

        LOGGER.debug("successfulUpdateEmployeeTest_WithInvestigations()");

//        Employee with id = 1 contains investigations with ids: 1 and 3
        Employee updateEmployee = new Employee(
                sFirstExistsEmployee.getEmployeeId(),
                "Isaac Asimov",
                LocalDate.parse("1920-01-02", DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse("1931-01-01", DateTimeFormatter.ISO_LOCAL_DATE),
                Arrays.asList(sFirstExistsInvestigation, sSecondExistsInvestigation));

        int updatedRow = employeeDao.updateEmployee(updateEmployee);
        assertTrue(updatedRow >= 0);

        List<Employee> employees = employeeDao.getAllEmployees(NULL_OFFSET, COUNT_ALL_EMPLOYEE);

        updateEmployee.setParticipatedInvestigation(Collections.emptyList());
        assertTrue(employees.contains(updateEmployee));

        Arrays.asList(sFirstExistsInvestigation.getInvestigationId(), sSecondExistsInvestigation.getInvestigationId(), 3)
                .forEach((itemId) -> {

                    List<Employee> returnedEmployees =
                            employeeDao.getInvolvedEmployeesInInvestigation(itemId, NULL_OFFSET, COUNT_ALL_EMPLOYEE);

                    if (itemId != 3) {
                        assertTrue(returnedEmployees.contains(updateEmployee));
                    } else {
                        assertFalse(returnedEmployees.contains(updateEmployee));
                    }
                });
    }

    @Test
    public void failureUpdateEmployeeTest_WithNullEmployee() throws Exception {

        LOGGER.debug("failureUpdateEmployeeTest_WithNullEmployee()");

        thrownException.expect(NullPointerException.class);
        employeeDao.updateEmployee(null);
    }

    @Test
    public void successfulUpdateEmployeeInvestigationsTest() throws Exception {

        LOGGER.debug("successfulUpdateEmployeeInvestigationsTest()");

//        Employee with id = 1 contains investigations with ids: 1 and 3
//        sFirstExistsEmployee's id is 1
        int updatedRow = employeeDao.updateEmployeeInvestigations(sFirstExistsEmployee.getEmployeeId(), Arrays.asList(1, 2));
        assertTrue(updatedRow >= 0);

        Arrays.asList(1, 2, 3).forEach((itemId) -> {

            List<Employee> returnedEmployees =
                    employeeDao.getInvolvedEmployeesInInvestigation(itemId, NULL_OFFSET, COUNT_ALL_EMPLOYEE);

            if (itemId != 3) {
                assertTrue(returnedEmployees.contains(sFirstExistsEmployee));
            } else {
                assertFalse(returnedEmployees.contains(sFirstExistsEmployee));
            }
        });
    }

    @Test
    public void successfulUpdateEmployeeInvestigationsTest_WithEmptyInvestigationsId() throws Exception {

        LOGGER.debug("successfulUpdateInvolvedStaffInInvestigationTest_WithEmptyEmployeesId()");

//        Employee with id = 1 contains investigations with ids: 1 and 3
//        sFirstExistsEmployee's id is 1
        int updatedRow = employeeDao.updateEmployeeInvestigations(sFirstExistsEmployee.getEmployeeId(), Collections.emptyList());
        assertTrue(updatedRow >= 0);

        Arrays.asList(1, 3).forEach((itemId) -> {

            List<Employee> returnedEmployees =
                    employeeDao.getInvolvedEmployeesInInvestigation(itemId, NULL_OFFSET, COUNT_ALL_EMPLOYEE);
            assertFalse(returnedEmployees.contains(sFirstExistsEmployee));
        });
    }

    @Test
    public void failureUpdateEmployeeInvestigationsTest_WithNullInvestigationsId() throws Exception {

        LOGGER.debug("failureUpdateEmployeeInvestigationsTest_WithNullInvestigationsId()");

        thrownException.expect(NullPointerException.class);
        employeeDao.updateEmployeeInvestigations(sFirstExistsInvestigation.getInvestigationId(), null);
    }

    @Test
    public void failureUpdateEmployeeInvestigationsTest_WithNullEmployeeId() throws Exception {

        LOGGER.debug("failureUpdateEmployeeInvestigationsTest_WithNullEmployeeId()");

        thrownException.expect(DataIntegrityViolationException.class);
        employeeDao.updateEmployeeInvestigations(null, Arrays.asList(sFirstExistsEmployee.getEmployeeId()));
    }

    @Test
    public void successfulDeleteEmployeeByIdTest() throws Exception {

        LOGGER.debug("successfulDeleteEmployeeByIdTest()");

        int updatedRow = employeeDao.deleteEmployeeById(sFirstExistsEmployee.getEmployeeId());
        assertTrue(updatedRow >= 0);

        List<Employee> returnedEmployees = employeeDao.getAllEmployees(NULL_OFFSET, COUNT_ALL_EMPLOYEE);
        assertFalse(returnedEmployees.contains(sFirstExistsEmployee));
    }

    @Test
    public void successfulDeleteEmployeeByIdTest_WithNullEmployeeId() throws Exception {

        LOGGER.debug("successfulDeleteEmployeeByIdTest_WithNullEmployeeId()");

        int deletedRow = employeeDao.deleteEmployeeById(null);
        assertTrue(deletedRow == 0);
    }

    @Test
    public void successfulDeleteEmployeeByIdTest_WithNotExistsEmployee() throws Exception {

        LOGGER.debug("successfulDeleteEmployeeByIdTest_WithNotExistsEmployee()");

        int deletedRow = employeeDao.deleteEmployeeById(NOT_EXISTS_EMPLOYEE_ID);
        assertTrue(deletedRow == 0);
    }

    @Test
    public void successfulGetEmployeesRatingTest() throws Exception {

        LOGGER.debug("successfulGetEmployeesRatingTest()");

        List<Pair<Integer, Integer>> returnedRating = employeeDao.getEmployeesRating(NULL_OFFSET, COUNT_ALL_EMPLOYEE);
        assertNotNull(returnedRating);
        assertTrue(returnedRating.size() == COUNT_ALL_EMPLOYEE);

        returnedRating.forEach((pair) -> {
            assertNotNull(pair);
            assertNotNull("ERROR: Employee id is null.", pair.first);
            assertNotNull("ERROR: Rating value for employee with id: " + pair.first + " is null.", pair.second);
        });
    }

    @Test
    public void successfulGetEmployeesRatingTest_WithOffsetAndLimit() throws Exception {

        LOGGER.debug("successfulGetEmployeesRatingTest_WithOffsetAndLimit()");

        int allowsReturnedEmployees = 2;
        List<Pair<Integer, Integer>> returnedRating = employeeDao.getEmployeesRating(NULL_OFFSET + 1, allowsReturnedEmployees);

        assertNotNull(returnedRating);
        assertTrue(returnedRating.size() <= allowsReturnedEmployees);

        returnedRating.forEach((pair) -> {
            assertNotNull(pair);
            assertNotNull("ERROR: Employee id is null.", pair.first);
            assertNotNull("ERROR: Rating value for employee with id: " + pair.first + " is null.", pair.second);
        });
    }

    @Test
    public void successfulGetEmployeesRatingTest_WithOutOfBoundsRange() throws Exception {

        LOGGER.debug("successfulGetEmployeesRatingTest_WithOutOfBoundsRange()");

        List<Pair<Integer, Integer>> returnedRating = employeeDao.getEmployeesRating(NULL_OFFSET + COUNT_ALL_EMPLOYEE, COUNT_ALL_EMPLOYEE);
        assertNotNull(returnedRating);
        assertTrue(returnedRating.isEmpty());
    }
}
