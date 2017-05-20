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

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:service-test.xml"})
@Transactional
public class InvestigationServiceImplTest {

    private static final Logger LOGGER = LogManager.getLogger();

    @Rule
    public ExpectedException thrownException = ExpectedException.none();

    @Autowired
    private InvestigationService investigationService;

    private static final int NULL_OFFSET = 0;
    private static final int COUNT_ALL_INVESTIGATIONS = 3;

    private static final int INVALID_ID = -5;
    private static final int INVALID_OFFSET = -5;
    private static final int INVALID_COUNT_ALL_INVESTIGATIONS = -5;

    private static final int EXISTS_EMPLOYEE_ID = 2;
    private static final int NOT_EXISTS_ID = 5;

    private static final int VALID_INVESTIGATION_NUMBER = 5;
    private static final OffsetDateTime START_DATE_IN_PERIOD;

    private static final OffsetDateTime END_DATE_IN_PERIOD;
    private static final OffsetDateTime FIRST_TEST_DATE;

    private static final OffsetDateTime SECOND_TEST_DATE;
    private static final Investigation sFirstExistsInvestigation;
    private static final Investigation sSecondExistsInvestigation;

    private static final Investigation sThirdExistsInvestigation;
    private static final Employee sFirstExistsEmployee;

    static {
        FIRST_TEST_DATE = OffsetDateTime.parse("2010-03-15T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        SECOND_TEST_DATE = OffsetDateTime.parse("2015-01-01T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        START_DATE_IN_PERIOD = OffsetDateTime.parse("1965-03-12T15:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        END_DATE_IN_PERIOD = OffsetDateTime.parse("1969-01-01T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        sFirstExistsInvestigation = new Investigation(1, 1,
                "Murders of Sharon Tate",
                "American actress and sex symbol Sharon Tate was murdered on August 1969 by members of the Charles Manson’s family.",
                OffsetDateTime.parse("1969-09-13T16:09:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                OffsetDateTime.parse("1972-02-04T13:26:16Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        sSecondExistsInvestigation = new Investigation(2, 2,
                "Assassination of Martin Luther King Jr.",
                "Martin Luther King Jr. was assassinated by James Earl Ray in Memphis, Tennessee on April 4, 1968.",
                OffsetDateTime.parse("1968-04-04T08:50:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                OffsetDateTime.parse("1968-04-10T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        sThirdExistsInvestigation = new Investigation(3, 3,
                "Murders of Charles Moore and Henry Dee",
                "Charles Moore and Henry Dee were tortured and drowned by members of Ku Klux Klan in Franklin County, Mississippi.",
                OffsetDateTime.parse("1964-05-25T22:16:30Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                null);

        sFirstExistsEmployee = new Employee(3, "Frank Columbo",
                LocalDate.parse("1936-04-16", DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse("1953-11-16", DateTimeFormatter.ISO_LOCAL_DATE),
                Arrays.asList(sFirstExistsInvestigation, sSecondExistsInvestigation, sThirdExistsInvestigation));

    }

    @Test
    public void successfulGetAllInvestigationsTest() throws Exception {

        LOGGER.debug("successfulGetAllInvestigationsTest()");

        List<Investigation> investigations = investigationService.getAllInvestigations(NULL_OFFSET, COUNT_ALL_INVESTIGATIONS);

        assertNotNull(investigations);
        investigations.forEach(Assert::assertNotNull);
    }

    @Test
    public void failureGetAllInvestigationsTest_WithInvalidOffset() throws Exception {

        LOGGER.debug("successfulGetAllInvestigationsTest_WithInvalidOffset()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.OFFSET_CAN_NOT_BE_LOWER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.getAllInvestigations(INVALID_OFFSET, COUNT_ALL_INVESTIGATIONS);
    }

    @Test
    public void failureGetAllInvestigationsTest_WithInvalidLimit() throws Exception {

        LOGGER.debug("failureGetAllInvestigationsTest_WithInvalidLimit()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.LIMIT_CAN_NOT_BE_LOWER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.getAllInvestigations(NULL_OFFSET, INVALID_COUNT_ALL_INVESTIGATIONS);
    }

    @Test
    public void successfulGetInvestigationBetweenPeriodTest() throws Exception {

        LOGGER.debug("successfulGetInvestigationBetweenPeriodTest()");

        List<Investigation> investigations = investigationService.getInvestigationsBetweenPeriod(
                START_DATE_IN_PERIOD, END_DATE_IN_PERIOD, NULL_OFFSET, COUNT_ALL_INVESTIGATIONS);

        assertNotNull(investigations);
        investigations.forEach(Assert::assertNotNull);

        assertFalse(investigations.contains(sFirstExistsInvestigation));
        assertTrue(investigations.contains(sSecondExistsInvestigation));
        assertFalse(investigations.contains(sThirdExistsInvestigation));
    }

    @Test
    public void failureGetInvestigationsBetweenPeriodTest_WithInvalidOffset() throws Exception {

        LOGGER.debug("failureGetInvestigationsBetweenPeriodTest_WithInvalidOffset()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.OFFSET_CAN_NOT_BE_LOWER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.getInvestigationsBetweenPeriod(
                START_DATE_IN_PERIOD, END_DATE_IN_PERIOD, INVALID_OFFSET, COUNT_ALL_INVESTIGATIONS);
    }

    @Test
    public void failureGetInvestigationsBetweenPeriodTest_WithInvalidLimit() throws Exception {

        LOGGER.debug("failureGetInvestigationsBetweenPeriodTest_WithInvalidLimit()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.LIMIT_CAN_NOT_BE_LOWER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.getInvestigationsBetweenPeriod(
                START_DATE_IN_PERIOD, END_DATE_IN_PERIOD, NULL_OFFSET, INVALID_COUNT_ALL_INVESTIGATIONS);
    }

    @Test
    public void failureGetInvestigationsBetweenPeriodTest_WithInvalidDates() throws Exception {

        LOGGER.debug("failureGetInvestigationsBetweenPeriodTest_WithInvalidDates()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.START_AND_END_DATES_SHOULD_MATCH_PATTERN);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.getInvestigationsBetweenPeriod(SECOND_TEST_DATE, FIRST_TEST_DATE, NULL_OFFSET, COUNT_ALL_INVESTIGATIONS);
    }

    @Test
    public void failureGetInvestigationsBetweenPeriodTest_WithNullStartDate() throws Exception {

        LOGGER.debug("failureGetInvestigationsBetweenPeriodTest_WithNullStartDate()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.START_DATE_IN_PERIOD_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.getInvestigationsBetweenPeriod(null, END_DATE_IN_PERIOD, NULL_OFFSET, COUNT_ALL_INVESTIGATIONS);
    }

    @Test
    public void failureGetInvestigationsBetweenPeriodTest_WithNullEndDate() throws Exception {

        LOGGER.debug("failureGetInvestigationsBetweenPeriodTest_WithNullEndDate()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.END_DATE_IN_PERIOD_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.getInvestigationsBetweenPeriod(START_DATE_IN_PERIOD, null, NULL_OFFSET, COUNT_ALL_INVESTIGATIONS);
    }

    @Test
    public void successfulGetEmployeeInvestigationsTest() throws Exception {

        LOGGER.debug("successfulGetEmployeeInvestigationsTest()");

        List<Investigation> investigations = investigationService.getEmployeeInvestigations(EXISTS_EMPLOYEE_ID, NULL_OFFSET, COUNT_ALL_INVESTIGATIONS);

        assertNotNull(investigations);
        investigations.forEach(Assert::assertNotNull);

        assertFalse(investigations.contains(sFirstExistsInvestigation));
        assertTrue(investigations.contains(sSecondExistsInvestigation));
        assertTrue(investigations.contains(sThirdExistsInvestigation));
    }

    @Test
    public void failureGetEmployeeInvestigationsTest_WithInvalidOffset() throws Exception {

        LOGGER.debug("failureGetEmployeeInvestigationsTest_WithInvalidOffset()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.OFFSET_CAN_NOT_BE_LOWER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.getEmployeeInvestigations(EXISTS_EMPLOYEE_ID, INVALID_OFFSET, COUNT_ALL_INVESTIGATIONS);
    }

    @Test
    public void failureGetEmployeeInvestigationsTest_WithInvalidLimit() throws Exception {

        LOGGER.debug("failureGetEmployeeInvestigationsTest_WithInvalidLimit()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.LIMIT_CAN_NOT_BE_LOWER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.getEmployeeInvestigations(EXISTS_EMPLOYEE_ID, NULL_OFFSET, INVALID_COUNT_ALL_INVESTIGATIONS);
    }

    @Test
    public void failureGetEmployeeInvestigationsTest_WithInvalidEmployeeId() throws Exception {

        LOGGER.debug("failureGetEmployeeInvestigationsTest_WithInvalidEmployeeId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.getEmployeeInvestigations(INVALID_ID, NULL_OFFSET, COUNT_ALL_INVESTIGATIONS);
    }

    @Test
    public void failureGetEmployeeInvestigationsTest_WithNullEmployeeId() throws Exception {

        LOGGER.debug("failureGetEmployeeInvestigationsTest_WithNullEmployeeId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.getEmployeeInvestigations(null, NULL_OFFSET, COUNT_ALL_INVESTIGATIONS);
    }

    @Test
    public void failureGetEmployeeInvestigationsTest_WithNotExistsEmployeeId() throws Exception {

        LOGGER.debug("failureGetEmployeeInvestigationsTest_WithNotExistsEmployeeId()");

        thrownException.expectMessage(MessageError.EMPLOYEE_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.getEmployeeInvestigations(NOT_EXISTS_ID, NULL_OFFSET, COUNT_ALL_INVESTIGATIONS);
    }

    @Test
    public void successfulGetInvestigationByIdTest() throws Exception {

        LOGGER.debug("successfulGetInvestigationByIdTest()");

        Investigation investigation = investigationService.getInvestigationById(sFirstExistsInvestigation.getInvestigationId());

        assertNotNull(investigation);
        assertEquals(sFirstExistsInvestigation, investigation);
    }

    @Test
    public void failureGetInvestigationByIdTest_WithInvalidInvestigationId() throws Exception {

        LOGGER.debug("failureGetInvestigationByIdTest_WithInvalidInvestigationId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.getInvestigationById(INVALID_ID);
    }

    @Test
    public void failureGetInvestigationByIdTest_WithNullInvestigationId() throws Exception {

        LOGGER.debug("failureGetInvestigationByIdTest_WithNullInvestigationId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.getInvestigationById(null);
    }

    @Test
    public void failureGetGetInvestigationByIdTest_WithNotExistsInvestigationId() throws Exception {

        LOGGER.debug("failureGetGetInvestigationByIdTest_WithNotExistsInvestigationId()");

        thrownException.expectMessage(MessageError.INVESTIGATION_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.getInvestigationById(NOT_EXISTS_ID);
    }

    @Test
    public void successfulAddInvestigationTest_WithoutInvolvedEmployees() throws Exception {

        LOGGER.debug("successfulAddInvestigationTest_WithoutInvolvedEmployees()");

        Investigation newInvestigation = new Investigation("Some title", "Some other description",
                FIRST_TEST_DATE, SECOND_TEST_DATE);

        int investigationId = investigationService.addInvestigation(newInvestigation);
        assertTrue(investigationId > 0);
    }

    @Test
    public void successfulAddInvestigationTest_WithInvolvedEmployees() throws Exception {

        LOGGER.debug("successfulAddInvestigationTest()");

        Investigation newInvestigation = new Investigation("Some title", "Some description",
                FIRST_TEST_DATE, null,
                Arrays.asList(sFirstExistsEmployee));

        int investigationId = investigationService.addInvestigation(newInvestigation);
        assertTrue(investigationId > 0);
    }

    @Test
    public void failureAddInvestigationTest_WithInvalidInvestigationId() throws Exception {

        LOGGER.debug("failureAddInvestigationTest_WithInvalidInvestigationId()");

        Investigation newInvestigation = new Investigation(
                sFirstExistsInvestigation.getInvestigationId(),
                VALID_INVESTIGATION_NUMBER,
                "Some title",
                "Some description",
                FIRST_TEST_DATE,
                null);

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_NULL_OR_MINUS_ONE);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.addInvestigation(newInvestigation);
    }

    @Test
    public void failureAddInvestigationTest_WithInvalidInvestigationDates() throws Exception {

        LOGGER.debug("failureAddInvestigationTest_WithInvalidInvestigationDates()");

        Investigation newInvestigation = new Investigation(VALID_INVESTIGATION_NUMBER, "Some title", "Some description",
                SECOND_TEST_DATE, FIRST_TEST_DATE);

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.START_AND_END_DATES_SHOULD_MATCH_PATTERN);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.addInvestigation(newInvestigation);
    }

    @Test
    public void failureAddInvestigationTest_WithNotExistsEmployeesId() throws Exception {

        LOGGER.debug("failureAddInvestigationTest_WithNotExistsEmployeesId()");

        Investigation newInvestigation = new Investigation(VALID_INVESTIGATION_NUMBER, "Some title", "Some description",
                FIRST_TEST_DATE, null,
                Arrays.asList(new Employee(NOT_EXISTS_ID, "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"))));

        thrownException.expectMessage(MessageError.EMPLOYEE_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.addInvestigation(newInvestigation);
    }

    @Test
    public void failureAddInvestigationTest_WithInvalidEmployeesId() throws Exception {

        LOGGER.debug("failureAddInvestigationTest_WithInvalidEmployeesId()");

        Investigation newInvestigation = new Investigation(VALID_INVESTIGATION_NUMBER, "Some title", "Some description",
                FIRST_TEST_DATE, null,
                Arrays.asList(new Employee(INVALID_ID, "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"))));

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.addInvestigation(newInvestigation);
    }

    @Test
    public void failureAddInvestigationTest_WithNullEmployeesId() throws Exception {

        LOGGER.debug("failureAddInvestigationTest_WithNullEmployeesId()");

        Investigation newInvestigation = new Investigation(VALID_INVESTIGATION_NUMBER, "Some title", "Some description",
                FIRST_TEST_DATE, null,
                Arrays.asList(new Employee(null, "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"))));

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.addInvestigation(newInvestigation);
    }

    @Test
    public void failureAddInvestigationTest_WithNullInvestigation() throws Exception {

        LOGGER.debug("failureAddInvestigationTest_WithNullInvestigation()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.addInvestigation(null);
    }

    @Test
    public void successfulAddInvolvedStaff2InvestigationTest() throws Exception {

        LOGGER.debug("successfulAddInvolvedStaff2InvestigationTest()");

        List<Integer> involvedStaff = Arrays.asList(EXISTS_EMPLOYEE_ID);

        List<Investigation> investigations = investigationService.getEmployeeInvestigations(EXISTS_EMPLOYEE_ID, NULL_OFFSET, COUNT_ALL_INVESTIGATIONS);
        assertFalse(investigations.contains(sFirstExistsInvestigation));

        investigationService.addInvolvedStaff2Investigation(sFirstExistsInvestigation.getInvestigationId(), involvedStaff);

        investigations = investigationService.getEmployeeInvestigations(EXISTS_EMPLOYEE_ID, NULL_OFFSET, COUNT_ALL_INVESTIGATIONS);
        assertTrue(investigations.contains(sFirstExistsInvestigation));
    }

    @Test
    public void failureAddInvolvedStaff2InvestigationTest_WithNullInvestigationId() throws Exception {

        LOGGER.debug("failureAddInvolvedStaff2InvestigationTest_WithNullInvestigationId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.addInvolvedStaff2Investigation(null, Arrays.asList(EXISTS_EMPLOYEE_ID));
    }

    @Test
    public void failureAddInvolvedStaff2InvestigationTest_WithInvalidInvestigationId() throws Exception {

        LOGGER.debug("failureAddInvolvedStaff2InvestigationTest_WithInvalidInvestigationId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.addInvolvedStaff2Investigation(INVALID_ID, Arrays.asList(EXISTS_EMPLOYEE_ID));
    }

    @Test
    public void failureAddInvolvedStaff2InvestigationTest_WithNotExistsInvestigationId() throws Exception {

        LOGGER.debug("failureAddInvolvedStaff2InvestigationTest_WithNotExistsInvestigationId()");

        thrownException.expectMessage(MessageError.INVESTIGATION_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.addInvolvedStaff2Investigation(NOT_EXISTS_ID, Arrays.asList(EXISTS_EMPLOYEE_ID));
    }

    @Test
    public void failureAddInvolvedStaff2InvestigationTest_WithNullInvolvedStaff() throws Exception {

        LOGGER.debug("failureAddInvolvedStaff2InvestigationTest_WithNullInvolvedStaff()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_INVOLVED_STAFF_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.addInvolvedStaff2Investigation(sFirstExistsInvestigation.getInvestigationId(), null);
    }

    @Test
    public void failureAddInvolvedStaff2InvestigationTest_WithNullEmployeeId() throws Exception {

        LOGGER.debug("failureAddInvolvedStaff2InvestigationTest_WithNullEmployeeId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.addInvolvedStaff2Investigation(sFirstExistsInvestigation.getInvestigationId(), Arrays.asList(EXISTS_EMPLOYEE_ID, null));
    }

    @Test
    public void failureAddInvolvedStaff2InvestigationTest_WithNotExistsEmployeeId() throws Exception {

        LOGGER.debug("failureAddInvolvedStaff2InvestigationTest_WithNotExistsEmployeeId()");

        thrownException.expectMessage(MessageError.EMPLOYEE_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.addInvolvedStaff2Investigation(sFirstExistsInvestigation.getInvestigationId(), Arrays.asList(NOT_EXISTS_ID));
    }

    @Test
    public void failureAddInvolvedStaff2InvestigationTest_WithInvalidEmployeeId() throws Exception {

        LOGGER.debug("failureAddInvolvedStaff2InvestigationTest_WithInvalidEmployeeId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.addInvolvedStaff2Investigation(sFirstExistsInvestigation.getInvestigationId(), Arrays.asList(INVALID_ID));
    }

    @Test
    public void successfulUpdateInvestigationTest_WithoutInvolvedStaff() throws Exception {

        LOGGER.debug("successfulUpdateInvestigationTest_WithoutInvolvedStaff()");

        Investigation updatedInvestigation = new Investigation(
                sFirstExistsInvestigation.getInvestigationId(),
                VALID_INVESTIGATION_NUMBER,
                "Some changed title",
                "Some changed description",
                FIRST_TEST_DATE,
                null);

        List<Investigation> investigations = investigationService.getEmployeeInvestigations(
                sFirstExistsEmployee.getEmployeeId(), NULL_OFFSET, COUNT_ALL_INVESTIGATIONS);

        assertTrue(investigations.contains(sFirstExistsInvestigation));

        boolean isUpdated = investigationService.updateInvestigation(updatedInvestigation);
        assertTrue(isUpdated);

        investigations = investigationService.getAllInvestigations(NULL_OFFSET, COUNT_ALL_INVESTIGATIONS);
        assertTrue(investigations.contains(updatedInvestigation));

        investigations = investigationService.getEmployeeInvestigations(
                sFirstExistsEmployee.getEmployeeId(), NULL_OFFSET, COUNT_ALL_INVESTIGATIONS);
        assertFalse(investigations.contains(updatedInvestigation));
    }

    @Test
    public void successfulUpdateInvestigationTest_WithInvolvedStaff() throws Exception {

        LOGGER.debug("successfulUpdateInvestigationTest_WithInvolvedStaff()");

        Investigation updatedInvestigation = new Investigation(
                sSecondExistsInvestigation.getInvestigationId(),
                VALID_INVESTIGATION_NUMBER,
                "Some changed title",
                "Some changed description",
                FIRST_TEST_DATE,
                null);

        updatedInvestigation.setInvolvedStaff(Arrays.asList(sFirstExistsEmployee));

        sFirstExistsInvestigation.setInvolvedStaff(Collections.emptyList());

        List<Investigation> investigations = investigationService.getEmployeeInvestigations(
                EXISTS_EMPLOYEE_ID, NULL_OFFSET, COUNT_ALL_INVESTIGATIONS);
        assertTrue(investigations.contains(sSecondExistsInvestigation));

        boolean isUpdated = investigationService.updateInvestigation(updatedInvestigation);
        assertTrue(isUpdated);

        updatedInvestigation.setInvolvedStaff(Collections.emptyList());

        investigations = investigationService.getAllInvestigations(NULL_OFFSET, COUNT_ALL_INVESTIGATIONS);
        assertTrue(investigations.contains(updatedInvestigation));

        investigations = investigationService.getEmployeeInvestigations(
                sFirstExistsEmployee.getEmployeeId(), NULL_OFFSET, COUNT_ALL_INVESTIGATIONS);
        assertTrue(investigations.contains(updatedInvestigation));

        investigations = investigationService.getEmployeeInvestigations(
                EXISTS_EMPLOYEE_ID, NULL_OFFSET, COUNT_ALL_INVESTIGATIONS);
        assertFalse(investigations.contains(updatedInvestigation));
    }

    @Test
    public void failureUpdateInvestigationTest_WithNullInvestigation() throws Exception {

        LOGGER.debug("failureUpdateInvestigationTest_WithNullInvestigation()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.updateInvestigation(null);
    }

    @Test
    public void failureUpdateInvestigationTest_WithNullInvolvedStaff() throws Exception {

        LOGGER.debug("failureUpdateInvestigationTest_WithNullInvolvedStaff()");

        Investigation updatedInvestigation = new Investigation(
                sFirstExistsInvestigation.getInvestigationId(),
                VALID_INVESTIGATION_NUMBER,
                "Some changed title",
                "Some changed description",
                FIRST_TEST_DATE,
                null);

        updatedInvestigation.setInvolvedStaff(null);

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_INVOLVED_STAFF_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.updateInvestigation(updatedInvestigation);
    }

    @Test
    public void failureUpdateInvestigationTest_WithNotExistsInvestigation() throws Exception {

        LOGGER.debug("failureUpdateInvestigationTest_WithNotExistsInvestigation()");

        Investigation updatedInvestigation = new Investigation(NOT_EXISTS_ID, VALID_INVESTIGATION_NUMBER, "Some changed title",
                "Some changed description", OffsetDateTime.parse("1965-06-12T15:06:45Z"), null);

        thrownException.expectMessage(MessageError.INVESTIGATION_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.updateInvestigation(updatedInvestigation);
    }

    @Test
    public void failureUpdateInvestigationTest_WithNotExistsEmployee() throws Exception {

        LOGGER.debug("failureUpdateInvestigationTest_WithNotExistsEmployee()");

        Investigation updatedInvestigation = new Investigation(
                sFirstExistsInvestigation.getInvestigationId(),
                VALID_INVESTIGATION_NUMBER,
                "Some changed title",
                "Some changed description",
                FIRST_TEST_DATE,
                null);

        updatedInvestigation.setInvolvedStaff(
                Arrays.asList(new Employee(NOT_EXISTS_ID, "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"))));

        thrownException.expectMessage(MessageError.EMPLOYEE_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.updateInvestigation(updatedInvestigation);
    }

    @Test
    public void failureUpdateInvestigationTest_WithNullInvolvedStaffEmployeeId() throws Exception {

        LOGGER.debug("failureUpdateInvestigationTest_WithNullInvolvedStaffEmployeeId()");

        Investigation updatedInvestigation = new Investigation(
                sFirstExistsInvestigation.getInvestigationId(),
                VALID_INVESTIGATION_NUMBER,
                "Some changed title",
                "Some changed description",
                FIRST_TEST_DATE,
                null);

        updatedInvestigation.setInvolvedStaff(
                Arrays.asList(new Employee(null, "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"))));

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.updateInvestigation(updatedInvestigation);
    }

    @Test
    public void successfulUpdateInvolvedStaffInInvestigationTest() throws Exception {

        LOGGER.debug("successfulUpdateInvolvedStaffInInvestigationTest()");

        List<Integer> employeesId = Arrays.asList(EXISTS_EMPLOYEE_ID);

        List<Investigation> investigations = investigationService.getEmployeeInvestigations(
                sFirstExistsEmployee.getEmployeeId(), NULL_OFFSET, COUNT_ALL_INVESTIGATIONS);
        assertTrue(investigations.contains(sFirstExistsInvestigation));

        investigations = investigationService.getEmployeeInvestigations(EXISTS_EMPLOYEE_ID, NULL_OFFSET, COUNT_ALL_INVESTIGATIONS);
        assertFalse(investigations.contains(sFirstExistsInvestigation));

        boolean isUpdated = investigationService.updateInvolvedStaffInInvestigation(sFirstExistsInvestigation.getInvestigationId(), employeesId);
        assertTrue(isUpdated);

        investigations = investigationService.getEmployeeInvestigations(
                sFirstExistsEmployee.getEmployeeId(), NULL_OFFSET, COUNT_ALL_INVESTIGATIONS);
        assertFalse(investigations.contains(sFirstExistsInvestigation));

        investigations = investigationService.getEmployeeInvestigations(EXISTS_EMPLOYEE_ID, NULL_OFFSET, COUNT_ALL_INVESTIGATIONS);
        assertTrue(investigations.contains(sFirstExistsInvestigation));
    }

    @Test
    public void failureUpdateInvolvedStaffInInvestigationTest_WithNullInvestigationId() throws Exception {

        LOGGER.debug("failureUpdateInvolvedStaffInInvestigationTest_WithNullInvestigationId(");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.updateInvolvedStaffInInvestigation(null, Arrays.asList(EXISTS_EMPLOYEE_ID));
    }

    @Test
    public void failureUpdateInvolvedStaffInInvestigationTest_WithInvalidInvestigationId() throws Exception {

        LOGGER.debug("failureUpdateInvolvedStaffInInvestigationTest_WithInvalidInvestigationId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.updateInvolvedStaffInInvestigation(INVALID_ID, Arrays.asList(EXISTS_EMPLOYEE_ID));
    }

    @Test
    public void failureUpdateInvolvedStaffInInvestigationTest_WithNotExistsInvestigationId() throws Exception {

        LOGGER.debug("failureUpdateInvolvedStaffInInvestigationTest_WithNotExistsInvestigationId()");

        thrownException.expectMessage(MessageError.INVESTIGATION_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.updateInvolvedStaffInInvestigation(NOT_EXISTS_ID, Arrays.asList(EXISTS_EMPLOYEE_ID));
    }

    @Test
    public void failureUpdateInvolvedStaffInInvestigationTest_WithNullInvolvedStaff() throws Exception {

        LOGGER.debug("failureUpdateInvolvedStaffInInvestigationTest_WithNullInvolvedStaff()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_INVOLVED_STAFF_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.updateInvolvedStaffInInvestigation(sFirstExistsInvestigation.getInvestigationId(), null);
    }

    @Test
    public void failureUpdateInvolvedStaffInInvestigationTest_WithNullEmployeeId() throws Exception {

        LOGGER.debug("failureUpdateInvolvedStaffInInvestigationTest_WithNullEmployeeId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.updateInvolvedStaffInInvestigation(
                sFirstExistsInvestigation.getInvestigationId(), Arrays.asList(EXISTS_EMPLOYEE_ID, null));
    }

    @Test
    public void failureUpdateInvolvedStaffInInvestigationTest_WithNotExistsEmployeeId() throws Exception {

        LOGGER.debug("failureUpdateInvolvedStaffInInvestigationTest_WithNotExistsEmployeeId()");

        thrownException.expectMessage(MessageError.EMPLOYEE_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.updateInvolvedStaffInInvestigation(
                sFirstExistsInvestigation.getInvestigationId(), Arrays.asList(NOT_EXISTS_ID));
    }

    @Test
    public void successfulDeleteInvestigationByIdTest() throws Exception {

        LOGGER.debug("successfulDeleteInvestigationByIdTest");

        boolean isDeleted = investigationService.deleteInvestigationById(sFirstExistsInvestigation.getInvestigationId());
        assertTrue(isDeleted);

        List<Investigation> investigations = investigationService.getAllInvestigations(NULL_OFFSET, COUNT_ALL_INVESTIGATIONS);
        assertFalse(investigations.contains(sFirstExistsInvestigation));
    }

    @Test
    public void failureDeleteInvestigationByIdTest_WithNotExistsInvestigation() throws Exception {

        LOGGER.debug("failureDeleteInvestigationByIdTest_WithNotExistsInvestigation()");

        thrownException.expectMessage(MessageError.INVESTIGATION_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.deleteInvestigationById(NOT_EXISTS_ID);
    }

    @Test
    public void failureDeleteInvestigationByIdTest_WithNullInvestigationId() throws Exception {

        LOGGER.debug("failureDeleteInvestigationByIdTest_WithNullInvestigationId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.deleteInvestigationById(null);
    }

    @Test
    public void failureDeleteInvestigationByIdTest_WithInvalidInvestigationId() throws Exception {

        LOGGER.debug("failureDeleteInvestigationByIdTest_WithInvalidInvestigationId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        investigationService.deleteInvestigationById(INVALID_ID);
    }

}
