package com.segniertomato.work.dao;


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
public class InvestigationDaoImplTest {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private InvestigationDao investigationDao;

    @Rule
    public ExpectedException thrownException = ExpectedException.none();

    private static final Integer COUNT_ALL_INVESTIGATION = 3;
    private static final int NULL_OFFSET = 0;
    private static final int NOT_EXISTS_INVESTIGATION_ID = 5;

    private static final Investigation sFirstExistsInvestigation;
    private static final Employee sFirstExistsEmployee;
    private static final Employee sSecondExistsEmployee;

    private static final OffsetDateTime sTestOffsetDateTime;

    private static final OffsetDateTime sValidExistsStartPeriod;
    private static final OffsetDateTime sValidExistsEndPeriod;

    static {

        sTestOffsetDateTime = OffsetDateTime.parse("1990-01-27T02:05:02Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        sValidExistsStartPeriod = OffsetDateTime.parse("1965-06-16T15:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        sValidExistsEndPeriod = OffsetDateTime.parse("1970-01-25T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        sFirstExistsInvestigation = new Investigation("Murders of Sharon Tate",
                "American actress and sex symbol Sharon Tate was murdered on August 1969 by members of the Charles Manson’s family.",
                OffsetDateTime.parse("1969-09-13T16:09:00Z"), OffsetDateTime.parse("1972-02-04T13:26:16Z"));
        sFirstExistsInvestigation.setInvestigationId(1);

        sFirstExistsEmployee = new Employee(1, "Nick Jeyrom", LocalDate.parse("1936-03-26"), LocalDate.parse("1956-09-25"));

        sSecondExistsEmployee = new Employee(2, "Cartman Fingerbang", LocalDate.parse("1937-11-02"), LocalDate.parse("1963-04-16"));
    }


    @Test
    public void successfulGetAllInvestigationsTest() throws Exception {

        LOGGER.debug("successfulGetAllInvestigationsTest()");

        List<Investigation> returnedInvestigations = investigationDao.getAllInvestigations(NULL_OFFSET, COUNT_ALL_INVESTIGATION);

        assertNotNull(returnedInvestigations);

        LOGGER.debug("successfulGetAllInvestigationsTest() - count of returned Investigations: {}", returnedInvestigations.size());
        assertTrue(returnedInvestigations.size() == COUNT_ALL_INVESTIGATION);

        returnedInvestigations.forEach(Assert::assertNotNull);
    }

    @Test
    public void successfulGetAllInvestigationsTest_WithOffsetAndLimit() throws Exception {

        LOGGER.debug("successfulGetAllInvestigationsTest_WithOffsetAndLimit()");

        int allowsReturnedInvestigationsCount = 2;
        List<Investigation> returnedInvestigations = investigationDao.getAllInvestigations(NULL_OFFSET + 1, allowsReturnedInvestigationsCount);

        assertNotNull(returnedInvestigations);

        LOGGER.debug("successfulGetAllInvestigationsTest_WithOffsetAndLimit() - count of returned Investigations: {}", returnedInvestigations.size());
        assertTrue(returnedInvestigations.size() <= allowsReturnedInvestigationsCount);

        returnedInvestigations.forEach(Assert::assertNotNull);
    }

    @Test
    public void successfulGetAllInvestigationsTest_WithOutOfBoundsRange() throws Exception {

        LOGGER.debug("successfulGetAllInvestigationsTest_WithOutOfBoundsRange()");

        int allowsReturnedInvestigationsCount = 5;
        List<Investigation> returnedInvestigations = investigationDao.getAllInvestigations(NULL_OFFSET + COUNT_ALL_INVESTIGATION, allowsReturnedInvestigationsCount);

        assertNotNull(returnedInvestigations);

        LOGGER.debug("successfulGetAllInvestigationsTest_WithOutOfBoundsRange() - count of returned Investigations: {}", returnedInvestigations.size());
        assertTrue(returnedInvestigations.isEmpty());
    }

    @Test
    public void successfulGetInvestigationsBetweenPeriodTest() throws Exception {

        LOGGER.debug("successfulGetInvestigationsBetweenPeriodTest()");

        int expectedCountInvestigationsBetweenPeriod = 2;

        List<Investigation> returnedInvestigations = investigationDao.getInvestigationsBetweenPeriod(sValidExistsStartPeriod, sValidExistsEndPeriod, NULL_OFFSET, COUNT_ALL_INVESTIGATION);

        assertNotNull(returnedInvestigations);

        LOGGER.debug("successfulGetInvestigationsBetweenPeriodTest() - count of returned Investigations: {}", returnedInvestigations.size());
        assertTrue(returnedInvestigations.size() == expectedCountInvestigationsBetweenPeriod);
    }

    @Test
    public void successfulGetInvestigationsBetweenPeriodTest_WithOutOfRange() throws Exception {

        LOGGER.debug("successfulGetInvestigationsBetweenPeriodTest_WithOutOfRange()");

        OffsetDateTime outOfRangeStartPeriod = OffsetDateTime.parse("1970-06-16T15:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        OffsetDateTime outOfRangeEndPeriod = OffsetDateTime.parse("1990-01-25T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        List<Investigation> returnedInvestigations = investigationDao.getInvestigationsBetweenPeriod(outOfRangeStartPeriod, outOfRangeEndPeriod, NULL_OFFSET, COUNT_ALL_INVESTIGATION);

        assertNotNull(returnedInvestigations);

        LOGGER.debug("successfulGetInvestigationsBetweenPeriodTest_WithOutOfRange() - count of returned Investigations: {}", returnedInvestigations.size());
        assertTrue(returnedInvestigations.isEmpty());
    }

    @Test
    public void successfulGetInvestigationsBetweenPeriodTest_WithWrongPeriod() throws Exception {

        LOGGER.debug("successfulGetInvestigationsBetweenPeriodTest_WithWrongPeriod()");

        OffsetDateTime incorrectStartPeriod = OffsetDateTime.parse("1990-01-25T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        OffsetDateTime incorrectEndPeriod = OffsetDateTime.parse("1970-06-16T15:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        List<Investigation> returnedInvestigations = investigationDao.getInvestigationsBetweenPeriod(incorrectStartPeriod, incorrectEndPeriod, NULL_OFFSET, COUNT_ALL_INVESTIGATION);

        assertNotNull(returnedInvestigations);
        assertTrue(returnedInvestigations.isEmpty());
    }

    @Test
    public void successfulGetEmployeeInvestigationsTest() throws Exception {

        LOGGER.debug("successfulGetEmployeeInvestigationsTest()");

        Integer employeeId = 3;
        int expectedCountEmployeeInvestigations = 3;

        List<Investigation> returnedInvestigations = investigationDao.getEmployeeInvestigations(employeeId, NULL_OFFSET, COUNT_ALL_INVESTIGATION);

        assertNotNull(returnedInvestigations);

        LOGGER.debug("successfulGetEmployeeInvestigationsTest() - count of returned Investigations: {}", returnedInvestigations.size());
        assertTrue(returnedInvestigations.size() == expectedCountEmployeeInvestigations);
    }

    @Test
    public void successfulGetEmployeeInvestigationsTest_WithNotExistsEmployeeId() throws Exception {

        LOGGER.debug("successfulGetEmployeeInvestigationsTest_WithNotExistsEmployeeId()");

        Integer notExistsEmployeeId = 5;

        List<Investigation> returnedInvestigations = investigationDao.getEmployeeInvestigations(notExistsEmployeeId, NULL_OFFSET, COUNT_ALL_INVESTIGATION);

        assertNotNull(returnedInvestigations);

        LOGGER.debug("successfulGetEmployeeInvestigationsTest() - count of returned Investigations: {}", returnedInvestigations.size());
        assertTrue(returnedInvestigations.isEmpty());
    }

    @Test
    public void successfulGetEmployeeInvestigationsTest_WithNullEmployeeId() throws Exception {

        LOGGER.debug("successfulGetEmployeeInvestigationsTest_WithNullEmployeeId()");

        List<Investigation> returnedInvestigations = investigationDao.getEmployeeInvestigations(null, NULL_OFFSET, COUNT_ALL_INVESTIGATION);

        assertNotNull(returnedInvestigations);

        LOGGER.debug("successfulGetEmployeeInvestigationsTest_WithNullEmployeeId() - count of returned Investigations: {}", returnedInvestigations.size());
        assertTrue(returnedInvestigations.isEmpty());
    }

    @Test
    public void successfulGetInvestigationByIdTest() throws Exception {

        LOGGER.debug("successfulGetInvestigationByIdTest()");

        Investigation returnedInvestigation = investigationDao.getInvestigationById(sFirstExistsInvestigation.getInvestigationId());

        assertNotNull(returnedInvestigation);
        assertEquals(sFirstExistsInvestigation, returnedInvestigation);
    }

    @Test
    public void failureGetInvestigationByIdTest_WithNotExistsId() throws Exception {

        LOGGER.debug("failureGetInvestigationByIdTest_WithNotExistsId()");

        thrownException.expect(EmptyResultDataAccessException.class);
        investigationDao.getInvestigationById(NOT_EXISTS_INVESTIGATION_ID);
    }

    @Test
    public void failureGetInvestigationByIdTest_WithNullId() throws Exception {

        LOGGER.debug("failureGetInvestigationByIdTest_WithNullId()");

        thrownException.expect(EmptyResultDataAccessException.class);
        investigationDao.getInvestigationById(null);
    }


    @Test
    public void successfulAddInvestigationTest_WithoutInvolvedStaff() throws Exception {

        LOGGER.debug("successfulAddInvestigationTest_WithoutInvolvedStaff()");

        Investigation newInvestigation = new Investigation(55, "someDescription", sTestOffsetDateTime);
        Integer returnedInvestigationId = investigationDao.addInvestigation(newInvestigation);

        assertNotNull(returnedInvestigationId);

        LOGGER.debug("successfulAddInvestigationTest() - returned Investigation id: {}", returnedInvestigationId);
        assertTrue(returnedInvestigationId > 0);

        newInvestigation.setInvestigationId(returnedInvestigationId);

        List<Investigation> investigations = investigationDao.getAllInvestigations(NULL_OFFSET, 6);
        assertTrue(investigations.contains(newInvestigation));

        Investigation returnedInvestigation = investigationDao.getInvestigationById(returnedInvestigationId);
        assertEquals(newInvestigation, returnedInvestigation);
    }

    @Test
    public void successfulAddInvestigationTest_WithInvolvedStaff() throws Exception {

        LOGGER.debug("successfulAddInvestigationTest_WithInvolvedStaff()");

        List<Employee> involvedStaff = Arrays.asList(sFirstExistsEmployee, sSecondExistsEmployee);

        Investigation newInvestigation = new Investigation(55, "someDescription", sTestOffsetDateTime, involvedStaff);
        Integer returnedInvestigationId = investigationDao.addInvestigation(newInvestigation);

        assertNotNull(returnedInvestigationId);

        LOGGER.debug("successfulAddInvestigationTest() - returned Investigation id: {}", returnedInvestigationId);
        assertTrue(returnedInvestigationId > 0);

        newInvestigation.setInvestigationId(returnedInvestigationId);
        newInvestigation.setInvolvedStaff(Collections.emptyList());

        LOGGER.debug("successfulAddInvestigationTest() - check added involved staff.");

        Arrays.asList(1, 2).forEach((itemId) -> {

            List<Investigation> returnedInvestigations =
                    investigationDao.getEmployeeInvestigations(sSecondExistsEmployee.getEmployeeId(), NULL_OFFSET, COUNT_ALL_INVESTIGATION + 1);
            assertTrue(returnedInvestigations.contains(newInvestigation));
        });
    }

    @Test
    public void failureAddInvestigationTest_WithNullInvestigationObject() throws Exception {

        LOGGER.debug("failureAddInvestigationTest_WithNullInvestigationObject()");

        thrownException.expect(IllegalArgumentException.class);
        investigationDao.addInvestigation(null);
    }

    @Test
    public void successfulAddInvolvedStaff2InvestigationTest() throws Exception {

        LOGGER.debug("successfulAddInvolvedStaff2InvestigationTest()");

        List<Integer> addEmployees = Arrays.asList(sSecondExistsEmployee.getEmployeeId());

        investigationDao.addInvolvedStaff2Investigation(sFirstExistsInvestigation.getInvestigationId(), addEmployees);

        LOGGER.debug("successfulAddInvolvedStaff2InvestigationTest() - check added involved staff.");

        addEmployees.forEach((itemId) -> {

            List<Investigation> returnedInvestigations =
                    investigationDao.getEmployeeInvestigations(itemId, NULL_OFFSET, COUNT_ALL_INVESTIGATION + 1);
            assertTrue(returnedInvestigations.contains(sFirstExistsInvestigation));

        });
    }

    @Test
    public void failureAddInvolvedStaff2InvestigationTest_WithNotExistsIds() throws Exception {

        LOGGER.debug("failureAddInvolvedStaff2InvestigationTest_WithNotExistsIds()");

        thrownException.expect(NullPointerException.class);
        investigationDao.addInvolvedStaff2Investigation(null, null);
    }

    @Test
    public void failureAddInvolvedStaff2InvestigationTest_WithNullStaff() throws Exception {

        LOGGER.debug("failureAddInvolvedStaff2InvestigationTest_WithNullStaff()");

        thrownException.expect(NullPointerException.class);
        investigationDao.addInvolvedStaff2Investigation(sFirstExistsInvestigation.getInvestigationId(), null);
    }

    @Test
    public void failureAddInvolvedStaff2InvestigationTest_WithNullInvestigationId() throws Exception {

        LOGGER.debug("failureAddInvolvedStaff2InvestigationTest_WithNullInvestigationId()");

        thrownException.expect(DataIntegrityViolationException.class);

        List<Integer> addEmployees = Arrays.asList(sFirstExistsEmployee.getEmployeeId(), sSecondExistsEmployee.getEmployeeId());
        investigationDao.addInvolvedStaff2Investigation(null, addEmployees);
    }

    @Test
    public void successfulUpdateInvestigationTest_WithoutInvolvedStaff() throws Exception {

        LOGGER.debug("successfulUpdateInvestigationTest_WithoutInvolvedStaff()");

        Investigation updatedInvestigation = new Investigation(
                "Some New Title",
                "Some new description",
                OffsetDateTime.parse("2017-06-16T15:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                OffsetDateTime.parse("2020-01-25T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        updatedInvestigation.setInvestigationId(sFirstExistsInvestigation.getInvestigationId());

        int updatedRow = investigationDao.updateInvestigation(updatedInvestigation);
        assertTrue(updatedRow >= 0);

        Investigation returnedInvestigation = investigationDao.getInvestigationById(updatedInvestigation.getInvestigationId());

        assertNotNull(returnedInvestigation);
        assertEquals(updatedInvestigation, returnedInvestigation);
    }

    @Test
    public void successfulUpdateInvestigationTest_WithInvolvedStaff() throws Exception {

        LOGGER.debug("successfulUpdateInvestigationTest_WithInvolvedStaff()");

//      Investigation with id 1 has employees with id 1 and 3.
        Investigation updatedInvestigation = new Investigation(
                "Some New Title",
                "Some new description",
                OffsetDateTime.parse("2017-06-16T15:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                OffsetDateTime.parse("2020-01-25T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                Arrays.asList(sFirstExistsEmployee, sSecondExistsEmployee));

        updatedInvestigation.setInvestigationId(sFirstExistsInvestigation.getInvestigationId());

        int updatedRow = investigationDao.updateInvestigation(updatedInvestigation);
        assertTrue(updatedRow >= 0);

        updatedInvestigation.setInvolvedStaff(Collections.emptyList());

        Investigation returnedInvestigation = investigationDao.getInvestigationById(updatedInvestigation.getInvestigationId());

        assertEquals(updatedInvestigation, returnedInvestigation);

        Arrays.asList(1, 2, 3).forEach((itemId) -> {

            List<Investigation> returnedInvestigations =
                    investigationDao.getEmployeeInvestigations(itemId, NULL_OFFSET, COUNT_ALL_INVESTIGATION);

            if (itemId != 3) {
                assertTrue(returnedInvestigations.contains(updatedInvestigation));

            } else {
                assertFalse(returnedInvestigations.contains(updatedInvestigation));
            }
        });
    }

    @Test
    public void failureUpdateInvestigationTest_WithNullInvestigationObject() throws Exception {

        LOGGER.debug("failureUpdateInvestigationTest_WithNullInvestigationObject()");

        thrownException.expect(NullPointerException.class);
        investigationDao.updateInvestigation(null);
    }

    @Test
    public void successfulUpdateInvolvedStaffInInvestigationTest() throws Exception {

        LOGGER.debug("successfulUpdateInvolvedStaffInInvestigationTest()");

//      Investigation with id 1 has employees with id 1 and 3.
        List<Integer> updatedEmployeesId = Arrays.asList(1, 2);
        int updatedRow = investigationDao.updateInvolvedStaffInInvestigation(sFirstExistsInvestigation.getInvestigationId(), updatedEmployeesId);
        assertTrue(updatedRow >= 0);

        updatedEmployeesId.forEach((itemId) -> {

            List<Investigation> returnedInvestigations =
                    investigationDao.getEmployeeInvestigations(itemId, NULL_OFFSET, COUNT_ALL_INVESTIGATION);
            assertTrue(returnedInvestigations.contains(sFirstExistsInvestigation));
        });
    }

    @Test
    public void successfulUpdateInvolvedStaffInInvestigationTest_WithEmptyEmployeesId() throws Exception {

        LOGGER.debug("successfulUpdateInvolvedStaffInInvestigationTest_WithEmptyEmployeesId()");

//      Investigation with id 1 has employees with id 1 and 3.
        investigationDao.updateInvolvedStaffInInvestigation(sFirstExistsInvestigation.getInvestigationId(), Collections.emptyList());

        Arrays.asList(1, 3).forEach((itemId) -> {

            List<Investigation> returnedInvestigations
                    = investigationDao.getEmployeeInvestigations(itemId, NULL_OFFSET, COUNT_ALL_INVESTIGATION);
            assertFalse(returnedInvestigations.contains(sFirstExistsInvestigation));
        });
    }

    @Test
    public void failureUpdateInvolvedStaffInInvestigationTest_WithNullEmployeesId() throws Exception {

        LOGGER.debug("failureUpdateInvolvedStaffInInvestigationTest_WithNullEmployeesId()");

        thrownException.expect(NullPointerException.class);
        investigationDao.updateInvolvedStaffInInvestigation(sFirstExistsInvestigation.getInvestigationId(), null);
    }

    @Test
    public void failureUpdateInvolvedStaffInInvestigationTest_WithNullInvestigationId() throws Exception {

        LOGGER.debug("failureUpdateInvolvedStaffInInvestigationTest_WithNullInvestigationId()");

        thrownException.expect(DataIntegrityViolationException.class);
        investigationDao.updateInvolvedStaffInInvestigation(null, Arrays.asList(sFirstExistsEmployee.getEmployeeId()));
    }

    @Test
    public void successfulDeleteInvestigationByIdTest() throws Exception {

        LOGGER.debug("successfulDeleteInvestigationByIdTest()");

        int deletedRow = investigationDao.deleteInvestigationById(sFirstExistsInvestigation.getInvestigationId());
        assertTrue(deletedRow >= 0);

        List<Investigation> allInvestigations = investigationDao.getAllInvestigations(NULL_OFFSET, COUNT_ALL_INVESTIGATION);
        assertFalse(allInvestigations.contains(sFirstExistsInvestigation));
    }

    @Test
    public void successfulDeleteInvestigationByIdTest_WithNullInvestigationId() throws Exception {

        LOGGER.debug("successfulDeleteInvestigationByIdTest_WithNullInvestigationId()");

        int deletedRow = investigationDao.deleteInvestigationById(null);
        assertTrue(deletedRow == 0);
    }

    @Test
    public void successfulDeleteInvestigationByIdTest_WithNotExistsInvestigation() throws Exception {

        LOGGER.debug("successfulDeleteInvestigationByIdTest_WithNotExistsInvestigation()");

        int deletedRow = investigationDao.deleteInvestigationById(NOT_EXISTS_INVESTIGATION_ID);
        assertTrue(deletedRow == 0);
    }
}
