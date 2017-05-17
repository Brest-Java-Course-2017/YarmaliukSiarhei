package com.segniertomato.work.service;


import com.segniertomato.work.dao.InvestigationDao;
import com.segniertomato.work.message.MessageError;
import com.segniertomato.work.model.Employee;
import com.segniertomato.work.model.Investigation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:service-test-mock.xml"})
public class InvestigationServiceImplMockTest {

    private static final Logger LOGGER = LogManager.getLogger();

    @Rule
    public ExpectedException thrownException = ExpectedException.none();

    @Autowired
    private InvestigationDao mockInvestigationDao;

    @Autowired
    private NamedParameterJdbcTemplate mockNamedParameterJdbcTemplate;

    @Autowired
    private InvestigationService investigationService;

    private static final int NULL_OFFSET = 0;
    private static final int COUNT_ALL_EMPLOYEES = 4;

    private static final int EXISTS_EMPLOYEE_ID = 2;
    private static final int EXISTS_INVESTIGATION_ID = 2;

    private static final int INVALID_ID = -5;
    private static final int INVALID_OFFSET = -5;
    private static final int INVALID_COUNT_ALL_EMPLOYEES = -5;

    private static final int NOT_EXISTS_EMPLOYEE_ID = 5;
    private static final int NOT_EXISTS_INVESTIGATION_ID = 5;

    private static final int VALID_INVESTIGATION_NUMBER = 5;

    private static final OffsetDateTime VALID_START_INVESTIGATION_DATE;
    private static final OffsetDateTime VALID_END_INVESTIGATION_DATE;

    private static final OffsetDateTime INVALID_START_PERIOD;
    private static final OffsetDateTime INVALID_END_PERIOD;

    private static final Investigation sExpectedInvestigation;

    static {

        INVALID_START_PERIOD = OffsetDateTime.parse("2015-01-01T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        INVALID_END_PERIOD = OffsetDateTime.parse("2010-03-15T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        VALID_START_INVESTIGATION_DATE = OffsetDateTime.parse("2005-03-12T15:42:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        VALID_END_INVESTIGATION_DATE = OffsetDateTime.parse("2005-03-15T16:16:03Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        sExpectedInvestigation = new Investigation(EXISTS_INVESTIGATION_ID, VALID_INVESTIGATION_NUMBER, "Some title",
                "Some interesting description.", VALID_START_INVESTIGATION_DATE, VALID_END_INVESTIGATION_DATE);
    }

    @After
    public void clear() {

        verify(mockInvestigationDao);
        reset(mockInvestigationDao);

        verify(mockNamedParameterJdbcTemplate);
        reset(mockNamedParameterJdbcTemplate);
    }


    @Test
    public void successfulGetAllInvestigationsTest() throws Exception {

        LOGGER.debug("successfulGetAllInvestigationsTest()");

        replay(mockNamedParameterJdbcTemplate);

        expect(mockInvestigationDao.getAllInvestigations(anyInt(), anyInt())).andReturn(Arrays.asList(sExpectedInvestigation));
        replay(mockInvestigationDao);

        List<Investigation> investigations = investigationService.getAllInvestigations(NULL_OFFSET, COUNT_ALL_EMPLOYEES);

        assertNotNull(investigations);
        investigations.forEach(Assert::assertNotNull);
    }

    @Test
    public void failureGetAllInvestigationsTest_WithInvalidOffset() throws Exception {

        LOGGER.debug("failureGetAllInvestigationsTest_WithInvalidOffset()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.OFFSET_CAN_NOT_BE_LOWER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockInvestigationDao);

        investigationService.getAllInvestigations(INVALID_OFFSET, COUNT_ALL_EMPLOYEES);
    }

    @Test
    public void failureGetAllInvestigationsTest_WithInvalidLimit() throws Exception {

        LOGGER.debug("failureGetAllInvestigationsTest_WithInvalidLimit()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.LIMIT_CAN_NOT_BE_LOWER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockInvestigationDao);

        investigationService.getAllInvestigations(NULL_OFFSET, INVALID_COUNT_ALL_EMPLOYEES);
    }

    @Test
    public void successfulGetInvestigationBetweenPeriodTest() throws Exception {

        LOGGER.debug("successfulGetInvestigationBetweenPeriodTest()");

        replay(mockNamedParameterJdbcTemplate);

        expect(
                mockInvestigationDao.getInvestigationsBetweenPeriod(
                        isA(OffsetDateTime.class), isA(OffsetDateTime.class), anyInt(), anyInt()))
                .andReturn(Arrays.asList(sExpectedInvestigation));
        replay(mockInvestigationDao);

        List<Investigation> investigations = investigationService.getInvestigationsBetweenPeriod(
                VALID_START_INVESTIGATION_DATE, VALID_END_INVESTIGATION_DATE, NULL_OFFSET, COUNT_ALL_EMPLOYEES);

        assertNotNull(investigations);
        investigations.forEach(Assert::assertNotNull);
    }

    @Test
    public void failureGetInvestigationsBetweenPeriodTest_WithInvalidOffset() throws Exception {

        LOGGER.debug("failureGetInvestigationsBetweenPeriodTest_WithInvalidOffset()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.OFFSET_CAN_NOT_BE_LOWER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockInvestigationDao);

        investigationService.getInvestigationsBetweenPeriod(
                VALID_START_INVESTIGATION_DATE, VALID_END_INVESTIGATION_DATE, INVALID_OFFSET, COUNT_ALL_EMPLOYEES);
    }

    @Test
    public void failureGetInvestigationsBetweenPeriodTest_WithInvalidLimit() throws Exception {

        LOGGER.debug("failureGetInvestigationsBetweenPeriodTest_WithInvalidLimit()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.LIMIT_CAN_NOT_BE_LOWER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockInvestigationDao);

        investigationService.getInvestigationsBetweenPeriod(
                VALID_START_INVESTIGATION_DATE, VALID_END_INVESTIGATION_DATE, NULL_OFFSET, INVALID_COUNT_ALL_EMPLOYEES);
    }

    @Test
    public void failureGetInvestigationsBetweenPeriodTest_WithInvalidDates() throws Exception {

        LOGGER.debug("failureGetInvestigationsBetweenPeriodTest_WithInvalidDates()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.START_AND_END_DATES_SHOULD_MATCH_PATTERN);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockInvestigationDao);

        investigationService.getInvestigationsBetweenPeriod(INVALID_START_PERIOD, INVALID_END_PERIOD, NULL_OFFSET, COUNT_ALL_EMPLOYEES);
    }

    @Test
    public void failureGetInvestigationsBetweenPeriodTest_WithNullStartDate() throws Exception {

        LOGGER.debug("failureGetInvestigationsBetweenPeriodTest_WithNullStartDate()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.START_DATE_IN_PERIOD_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockInvestigationDao);

        investigationService.getInvestigationsBetweenPeriod(null, VALID_END_INVESTIGATION_DATE, NULL_OFFSET, COUNT_ALL_EMPLOYEES);
    }

    @Test
    public void failureGetInvestigationsBetweenPeriodTest_WithNullEndDate() throws Exception {

        LOGGER.debug("failureGetInvestigationsBetweenPeriodTest_WithNullEndDate()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.END_DATE_IN_PERIOD_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockInvestigationDao);

        investigationService.getInvestigationsBetweenPeriod(VALID_START_INVESTIGATION_DATE, null, NULL_OFFSET, COUNT_ALL_EMPLOYEES);
    }

    @Test
    public void successfulGetEmployeeInvestigationsTest() throws Exception {

        LOGGER.debug("successfulGetEmployeeInvestigationsTest()");

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(1);
        replay(mockNamedParameterJdbcTemplate);

        expect(mockInvestigationDao.getEmployeeInvestigations(isA(Integer.class), anyInt(), anyInt()))
                .andReturn(Arrays.asList(sExpectedInvestigation));
        replay(mockInvestigationDao);

        List<Investigation> investigations = investigationService.getEmployeeInvestigations(EXISTS_EMPLOYEE_ID, NULL_OFFSET, COUNT_ALL_EMPLOYEES);
        assertNotNull(investigations);
        investigations.forEach(Assert::assertNotNull);
    }

    @Test
    public void failureGetEmployeeInvestigationsTest_WithInvalidOffset() throws Exception {

        LOGGER.debug("failureGetEmployeeInvestigationsTest_WithInvalidOffset()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.OFFSET_CAN_NOT_BE_LOWER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockInvestigationDao);

        investigationService.getEmployeeInvestigations(EXISTS_EMPLOYEE_ID, INVALID_OFFSET, COUNT_ALL_EMPLOYEES);
    }

    @Test
    public void failureGetEmployeeInvestigationsTest_WithInvalidLimit() throws Exception {

        LOGGER.debug("failureGetEmployeeInvestigationsTest_WithInvalidLimit()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.LIMIT_CAN_NOT_BE_LOWER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockInvestigationDao);

        investigationService.getEmployeeInvestigations(EXISTS_EMPLOYEE_ID, NULL_OFFSET, INVALID_COUNT_ALL_EMPLOYEES);
    }

    @Test
    public void failureGetEmployeeInvestigationsTest_WithWrongEmployeeId() throws Exception {

        LOGGER.debug("failureGetEmployeeInvestigationsTest_WithWrongEmployeeId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockInvestigationDao);

        investigationService.getEmployeeInvestigations(INVALID_ID, NULL_OFFSET, COUNT_ALL_EMPLOYEES);
    }

    @Test
    public void failureGetEmployeeInvestigationsTest_WithNullEmployeeId() throws Exception {

        LOGGER.debug("failureGetEmployeeInvestigationsTest_WithNullEmployeeId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockInvestigationDao);

        investigationService.getEmployeeInvestigations(null, NULL_OFFSET, COUNT_ALL_EMPLOYEES);
    }

    @Test
    public void failureGetEmployeeInvestigationsTest_WithNotExistsEmployeeId() throws Exception {

        LOGGER.debug("failureGetEmployeeInvestigationsTest_WithNotExistsEmployeeId()");

        thrownException.expectMessage(MessageError.Database.EMPLOYEE_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(0);
        replay(mockNamedParameterJdbcTemplate);

        replay(mockInvestigationDao);

        investigationService.getEmployeeInvestigations(NOT_EXISTS_EMPLOYEE_ID, NULL_OFFSET, COUNT_ALL_EMPLOYEES);
    }

    @Test
    public void successfulGetInvestigationByIdTest() throws Exception {

        LOGGER.debug("successfulGetInvestigationByIdTest()");

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(1);
        replay(mockNamedParameterJdbcTemplate);

        expect(mockInvestigationDao.getInvestigationById(isA(Integer.class))).andReturn(sExpectedInvestigation);
        replay(mockInvestigationDao);

        Investigation investigation = investigationService.getInvestigationById(sExpectedInvestigation.getInvestigationId());

        assertNotNull(investigation);
        assertEquals(sExpectedInvestigation, investigation);
    }

    @Test
    public void failureGetInvestigationByIdTest_WithWrongInvestigationId() throws Exception {

        LOGGER.debug("failureGetInvestigationByIdTest_WithWrongInvestigationId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockInvestigationDao);

        investigationService.getInvestigationById(INVALID_ID);
    }

    @Test
    public void failureGetInvestigationByIdTest_WithNullInvestigationId() throws Exception {

        LOGGER.debug("failureGetInvestigationByIdTest_WithNullInvestigationId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockInvestigationDao);

        investigationService.getInvestigationById(null);
    }

    @Test
    public void failureGetGetInvestigationByIdTest_WithNotExistsInvestigationId() throws Exception {

        LOGGER.debug("failureGetGetInvestigationByIdTest_WithNotExistsInvestigationId()");

        thrownException.expectMessage(MessageError.Database.INVESTIGATION_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(0);
        replay(mockNamedParameterJdbcTemplate);

        replay(mockInvestigationDao);

        investigationService.getInvestigationById(NOT_EXISTS_EMPLOYEE_ID);
    }

    @Test
    public void successfulAddInvestigationTest_WithoutInvolvedEmployees() throws Exception {

        LOGGER.debug("successfulAddInvestigationTest_WithoutInvolvedEmployees()");

        Investigation newInvestigation = new Investigation("Some title", "Some other description",
                OffsetDateTime.parse("1965-06-12T15:06:45Z"), OffsetDateTime.parse("1969-06-12T15:06:45Z"));

        replay(mockNamedParameterJdbcTemplate);

        expect(mockInvestigationDao.addInvestigation(isA(Investigation.class))).andReturn(EXISTS_INVESTIGATION_ID);
        replay(mockInvestigationDao);

        int investigationId = investigationService.addInvestigation(newInvestigation);
        assertTrue(investigationId > 0);
        assertTrue(investigationId == EXISTS_INVESTIGATION_ID);
    }

    @Test
    public void successfulAddInvestigationTest_WithInvolvedEmployees() throws Exception {

        LOGGER.debug("successfulAddInvestigationTest()");

        Investigation newInvestigation = new Investigation("Some title", "Some description",
                OffsetDateTime.parse("1965-06-12T15:06:45Z"), null,
                Arrays.asList(new Employee(EXISTS_EMPLOYEE_ID, "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"))));

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class)))
                .andReturn(1);
        replay(mockNamedParameterJdbcTemplate);

        expect(mockInvestigationDao.addInvestigation(isA(Investigation.class))).andReturn(EXISTS_INVESTIGATION_ID);
        replay(mockInvestigationDao);

        int investigationId = investigationService.addInvestigation(newInvestigation);
        assertTrue(investigationId > 0);
        assertTrue(investigationId == EXISTS_INVESTIGATION_ID);
    }

    @Test
    public void failureAddInvestigationTest_WithInvalidInvestigationId() throws Exception {

        LOGGER.debug("failureAddInvestigationTest_WithInvalidInvestigationId()");

        Investigation newInvestigation = new Investigation(EXISTS_INVESTIGATION_ID, VALID_INVESTIGATION_NUMBER, "Some title", "Some description",
                OffsetDateTime.parse("1965-06-12T15:06:45Z"), null);

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_NULL_OR_MINUS_ONE);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockInvestigationDao);

        investigationService.addInvestigation(newInvestigation);
    }

    @Test
    public void failureAddInvestigationTest_WithInvalidInvestigationDates() throws Exception {

        LOGGER.debug("failureAddInvestigationTest_WithInvalidInvestigationDates()");

        Investigation newInvestigation = new Investigation(VALID_INVESTIGATION_NUMBER, "Some title", "Some description",
                OffsetDateTime.parse("1975-06-12T15:06:45Z"), OffsetDateTime.parse("1965-06-12T15:06:45Z"));

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.START_AND_END_DATES_SHOULD_MATCH_PATTERN);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockInvestigationDao);

        investigationService.addInvestigation(newInvestigation);
    }

    @Test
    public void failureAddInvestigationTest_WithNotExistsEmployeesId() throws Exception {

        LOGGER.debug("failureAddInvestigationTest_WithNotExistsEmployeesId()");

        Investigation newInvestigation = new Investigation(VALID_INVESTIGATION_NUMBER, "Some title", "Some description",
                OffsetDateTime.parse("1965-06-12T15:06:45Z"), null,
                Arrays.asList(new Employee(NOT_EXISTS_EMPLOYEE_ID, "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"))));

        thrownException.expectMessage(MessageError.Database.EMPLOYEE_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class)))
                .andReturn(0);
        replay(mockNamedParameterJdbcTemplate);
        replay(mockInvestigationDao);

        investigationService.addInvestigation(newInvestigation);
    }

    @Test
    public void failureAddInvestigationTest_WithInvalidEmployeesId() throws Exception {

        LOGGER.debug("failureAddInvestigationTest_WithInvalidEmployeesId()");

        Investigation newInvestigation = new Investigation(VALID_INVESTIGATION_NUMBER, "Some title", "Some description",
                OffsetDateTime.parse("1965-06-12T15:06:45Z"), null,
                Arrays.asList(new Employee(INVALID_ID, "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"))));

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockInvestigationDao);

        investigationService.addInvestigation(newInvestigation);
    }

    @Test
    public void failureAddInvestigationTest_WithNullEmployeesId() throws Exception {

        LOGGER.debug("failureAddInvestigationTest_WithNullEmployeesId()");

        Investigation newInvestigation = new Investigation(VALID_INVESTIGATION_NUMBER, "Some title", "Some description",
                OffsetDateTime.parse("1965-06-12T15:06:45Z"), null,
                Arrays.asList(new Employee(null, "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"))));

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockInvestigationDao);

        investigationService.addInvestigation(newInvestigation);
    }

    @Test
    public void failureAddInvestigationTest_WithNullInvestigation() throws Exception {

        LOGGER.debug("failureAddInvestigationTest_WithNullInvestigation()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockInvestigationDao);

        investigationService.addInvestigation(null);
    }


    @Test
    public void successfulAddInvolvedStaff2InvestigationTest() throws Exception {

        LOGGER.debug("successfulAddInvolvedStaff2InvestigationTest()");

        List<Integer> involvedStaff = Arrays.asList(EXISTS_EMPLOYEE_ID);
        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class)))
                .andReturn(1)
                .atLeastOnce();
        replay(mockNamedParameterJdbcTemplate);

        mockInvestigationDao.addInvolvedStaff2Investigation(EXISTS_INVESTIGATION_ID, involvedStaff);
        expectLastCall();
        replay(mockInvestigationDao);

        investigationService.addInvolvedStaff2Investigation(EXISTS_INVESTIGATION_ID, Arrays.asList(EXISTS_EMPLOYEE_ID));
    }

    @Test
    public void failureAddInvolvedStaff2InvestigationTest_WithNullInvestigationId() throws Exception {

        LOGGER.debug("failureAddInvolvedStaff2InvestigationTest_WithNullInvestigationId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockInvestigationDao);

        investigationService.addInvolvedStaff2Investigation(null, Arrays.asList(EXISTS_EMPLOYEE_ID));
    }

    @Test
    public void failureAddInvolvedStaff2InvestigationTest_WithInvalidInvestigationId() throws Exception {

        LOGGER.debug("failureAddInvolvedStaff2InvestigationTest_WithInvalidInvestigationId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockInvestigationDao);

        investigationService.addInvolvedStaff2Investigation(INVALID_ID, Arrays.asList(EXISTS_EMPLOYEE_ID));
    }

    @Test
    public void failureAddInvolvedStaff2InvestigationTest_WithNotExistsInvestigationId() throws Exception {

        LOGGER.debug("failureAddInvolvedStaff2InvestigationTest_WithNotExistsInvestigationId()");

        thrownException.expectMessage(MessageError.Database.INVESTIGATION_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(0);
        replay(mockNamedParameterJdbcTemplate);

        replay(mockInvestigationDao);

        investigationService.addInvolvedStaff2Investigation(NOT_EXISTS_INVESTIGATION_ID, Arrays.asList(EXISTS_EMPLOYEE_ID));
    }

    @Test
    public void failureAddInvolvedStaff2InvestigationTest_WithNullInvolvedStaff() throws Exception {

        LOGGER.debug("failureAddInvolvedStaff2InvestigationTest_WithNullInvolvedStaff()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_INVOLVED_STAFF_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(1);
        replay(mockNamedParameterJdbcTemplate);

        replay(mockInvestigationDao);

        investigationService.addInvolvedStaff2Investigation(EXISTS_INVESTIGATION_ID, null);
    }

    @Test
    public void failureAddInvolvedStaff2InvestigationTest_WithNullEmployeeId() throws Exception {

        LOGGER.debug("failureAddInvolvedStaff2InvestigationTest_WithNullEmployeeId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class)))
                .andReturn(1)
                .times(2);
        replay(mockNamedParameterJdbcTemplate);

        replay(mockInvestigationDao);

        investigationService.addInvolvedStaff2Investigation(EXISTS_INVESTIGATION_ID, Arrays.asList(EXISTS_EMPLOYEE_ID, null));
    }

    @Test
    public void failureAddInvolvedStaff2InvestigationTest_WithNotExistsEmployeeId() throws Exception {

        LOGGER.debug("failureAddInvolvedStaff2InvestigationTest_WithNotExistsEmployeeId()");

        thrownException.expectMessage(MessageError.Database.EMPLOYEE_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(1);
        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(0);
        replay(mockNamedParameterJdbcTemplate);

        replay(mockInvestigationDao);

        investigationService.addInvolvedStaff2Investigation(EXISTS_INVESTIGATION_ID, Arrays.asList(NOT_EXISTS_INVESTIGATION_ID));
    }


    @Test
    public void successfulUpdateInvestigationTest_WithoutInvolvedStaff() throws Exception {

        LOGGER.debug("successfulUpdateInvestigationTest_WithoutInvolvedStaff()");

        Investigation updatedInvestigation = new Investigation(EXISTS_INVESTIGATION_ID, VALID_INVESTIGATION_NUMBER, "Some changed title",
                "Some changed description", OffsetDateTime.parse("1965-06-12T15:06:45Z"), null);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(1);
        replay(mockNamedParameterJdbcTemplate);

        expect(mockInvestigationDao.updateInvestigation(isA(Investigation.class))).andReturn(1);
        replay(mockInvestigationDao);

        boolean isUpdated = investigationService.updateInvestigation(updatedInvestigation);
        assertTrue(isUpdated);
    }

    @Test
    public void successfulUpdateInvestigationTest_WithInvolvedStaff() throws Exception {

        LOGGER.debug("successfulUpdateInvestigationTest_WithInvolvedStaff()");

        Investigation updatedInvestigation = new Investigation(EXISTS_INVESTIGATION_ID, VALID_INVESTIGATION_NUMBER, "Some changed title",
                "Some changed description", OffsetDateTime.parse("1965-06-12T15:06:45Z"), null);

        updatedInvestigation.setInvolvedStaff(
                Arrays.asList(new Employee(EXISTS_EMPLOYEE_ID, "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"))));

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class)))
                .andReturn(1)
                .atLeastOnce();
        replay(mockNamedParameterJdbcTemplate);

        expect(mockInvestigationDao.updateInvestigation(isA(Investigation.class))).andReturn(1);
        replay(mockInvestigationDao);

        boolean isUpdated = investigationService.updateInvestigation(updatedInvestigation);
        assertTrue(isUpdated);
    }

    @Test
    public void failureUpdateInvestigationTest_WithNullInvestigation() throws Exception {

        LOGGER.debug("failureUpdateInvestigationTest_WithNullInvestigation()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockInvestigationDao);

        investigationService.updateInvestigation(null);
    }

    @Test
    public void failureUpdateInvestigationTest_WithNullInvolvedStaff() throws Exception {

        LOGGER.debug("failureUpdateInvestigationTest_WithNullInvolvedStaff()");

        Investigation updatedInvestigation = new Investigation(EXISTS_INVESTIGATION_ID, VALID_INVESTIGATION_NUMBER, "Some changed title",
                "Some changed description", OffsetDateTime.parse("1965-06-12T15:06:45Z"), null);

        updatedInvestigation.setInvolvedStaff(null);

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_INVOLVED_STAFF_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(1);
        replay(mockNamedParameterJdbcTemplate);

        replay(mockInvestigationDao);

        investigationService.updateInvestigation(updatedInvestigation);
    }

    @Test
    public void failureUpdateInvestigationTest_WithNotExistsInvestigation() throws Exception {

        LOGGER.debug("failureUpdateInvestigationTest_WithNotExistsInvestigation()");

        Investigation updatedInvestigation = new Investigation(NOT_EXISTS_INVESTIGATION_ID, VALID_INVESTIGATION_NUMBER, "Some changed title",
                "Some changed description", OffsetDateTime.parse("1965-06-12T15:06:45Z"), null);

        thrownException.expectMessage(MessageError.Database.INVESTIGATION_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(0);
        replay(mockNamedParameterJdbcTemplate);

        replay(mockInvestigationDao);

        investigationService.updateInvestigation(updatedInvestigation);
    }

    @Test
    public void failureUpdateInvestigationTest_WithNotExistsEmployee() throws Exception {

        LOGGER.debug("failureUpdateInvestigationTest_WithNotExistsEmployee()");

        Investigation updatedInvestigation = new Investigation(EXISTS_INVESTIGATION_ID, VALID_INVESTIGATION_NUMBER, "Some changed title",
                "Some changed description", OffsetDateTime.parse("1965-06-12T15:06:45Z"), null);

        updatedInvestigation.setInvolvedStaff(
                Arrays.asList(new Employee(NOT_EXISTS_EMPLOYEE_ID, "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"))));

        thrownException.expectMessage(MessageError.Database.EMPLOYEE_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class)))
                .andReturn(1);
        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class)))
                .andReturn(0);
        replay(mockNamedParameterJdbcTemplate);

        replay(mockInvestigationDao);

        investigationService.updateInvestigation(updatedInvestigation);
    }

    @Test
    public void failureUpdateInvestigationTest_WithNullInvolvedStaffEmployeeId() throws Exception {

        LOGGER.debug("failureUpdateInvestigationTest_WithNullInvolvedStaffEmployeeId()");

        Investigation updatedInvestigation = new Investigation(EXISTS_INVESTIGATION_ID, VALID_INVESTIGATION_NUMBER, "Some changed title",
                "Some changed description", OffsetDateTime.parse("1965-06-12T15:06:45Z"), null);

        updatedInvestigation.setInvolvedStaff(
                Arrays.asList(new Employee(null, "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"))));

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(1);
        replay(mockNamedParameterJdbcTemplate);

        replay(mockInvestigationDao);

        investigationService.updateInvestigation(updatedInvestigation);
    }

    @Test
    public void successfulUpdateInvolvedStaffInInvestigationTest() throws Exception {

        LOGGER.debug("successfulUpdateInvolvedStaffInInvestigationTest()");

        List<Integer> employeesId = Arrays.asList(EXISTS_EMPLOYEE_ID);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class)))
                .andReturn(1)
                .atLeastOnce();
        replay(mockNamedParameterJdbcTemplate);

        expect(mockInvestigationDao.updateInvolvedStaffInInvestigation(EXISTS_INVESTIGATION_ID, employeesId)).andReturn(1);
        replay(mockInvestigationDao);

        boolean isUpdated = investigationService.updateInvolvedStaffInInvestigation(EXISTS_INVESTIGATION_ID, employeesId);
        assertTrue(isUpdated);
    }

    @Test
    public void failureUpdateInvolvedStaffInInvestigationTest_WithNullInvestigationId() throws Exception {

        LOGGER.debug("failureUpdateInvolvedStaffInInvestigationTest_WithNullInvestigationId(");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockInvestigationDao);

        investigationService.updateInvolvedStaffInInvestigation(null, Arrays.asList(EXISTS_EMPLOYEE_ID));
    }

    @Test
    public void failureUpdateInvolvedStaffInInvestigationTest_WithInvalidInvestigationId() throws Exception {

        LOGGER.debug("failureUpdateInvolvedStaffInInvestigationTest_WithInvalidInvestigationId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockInvestigationDao);

        investigationService.updateInvolvedStaffInInvestigation(INVALID_ID, Arrays.asList(EXISTS_EMPLOYEE_ID));
    }

    @Test
    public void failureUpdateInvolvedStaffInInvestigationTest_WithNotExistsInvestigationId() throws Exception {

        LOGGER.debug("failureUpdateInvolvedStaffInInvestigationTest_WithNotExistsInvestigationId()");

        thrownException.expectMessage(MessageError.Database.INVESTIGATION_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(0);
        replay(mockNamedParameterJdbcTemplate);

        replay(mockInvestigationDao);

        investigationService.updateInvolvedStaffInInvestigation(NOT_EXISTS_INVESTIGATION_ID, Arrays.asList(EXISTS_EMPLOYEE_ID));
    }

    @Test
    public void failureUpdateInvolvedStaffInInvestigationTest_WithNullInvolvedStaff() throws Exception {

        LOGGER.debug("failureUpdateInvolvedStaffInInvestigationTest_WithNullInvolvedStaff()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_INVOLVED_STAFF_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(1);
        replay(mockNamedParameterJdbcTemplate);

        replay(mockInvestigationDao);

        investigationService.updateInvolvedStaffInInvestigation(EXISTS_EMPLOYEE_ID, null);
    }

    @Test
    public void failureUpdateInvolvedStaffInInvestigationTest_WithNullEmployeeId() throws Exception {

        LOGGER.debug("failureUpdateInvolvedStaffInInvestigationTest_WithNullEmployeeId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class)))
                .andReturn(1)
                .times(2);
        replay(mockNamedParameterJdbcTemplate);

        replay(mockInvestigationDao);

        investigationService.updateInvolvedStaffInInvestigation(EXISTS_INVESTIGATION_ID, Arrays.asList(EXISTS_EMPLOYEE_ID, null));
    }

    @Test
    public void failureUpdateInvolvedStaffInInvestigationTest_WithNotExistsEmployeeId() throws Exception {

        LOGGER.debug("failureUpdateInvolvedStaffInInvestigationTest_WithNotExistsEmployeeId()");

        thrownException.expectMessage(MessageError.Database.EMPLOYEE_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(1);
        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(0);
        replay(mockNamedParameterJdbcTemplate);

        replay(mockInvestigationDao);

        investigationService.updateInvolvedStaffInInvestigation(EXISTS_INVESTIGATION_ID, Arrays.asList(NOT_EXISTS_INVESTIGATION_ID));
    }

    @Test
    public void successfulDeleteInvestigationByIdTest() throws Exception {

        LOGGER.debug("successfulDeleteInvestigationByIdTest");

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(1);
        replay(mockNamedParameterJdbcTemplate);

        expect(mockInvestigationDao.deleteInvestigationById(isA(Integer.class))).andReturn(1);
        replay(mockInvestigationDao);

        boolean isDeleted = investigationService.deleteInvestigationById(EXISTS_INVESTIGATION_ID);
        assertTrue(isDeleted);
    }

    @Test
    public void failureDeleteInvestigationByIdTest_WithNotExistsInvestigation() throws Exception {

        LOGGER.debug("failureDeleteInvestigationByIdTest_WithNotExistsInvestigation()");

        thrownException.expectMessage(MessageError.Database.INVESTIGATION_NOT_EXISTS);
        thrownException.expect(IllegalArgumentException.class);

        expect(mockNamedParameterJdbcTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(0);
        replay(mockNamedParameterJdbcTemplate);

        replay(mockInvestigationDao);

        investigationService.deleteInvestigationById(EXISTS_INVESTIGATION_ID);
    }

    @Test
    public void failureDeleteInvestigationByIdTest_WithNullInvestigationId() throws Exception {

        LOGGER.debug("failureDeleteInvestigationByIdTest_WithNullInvestigationId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockInvestigationDao);

        investigationService.deleteInvestigationById(null);
    }

    @Test
    public void failureDeleteInvestigationByIdTest_WithInvalidInvestigationId() throws Exception {

        LOGGER.debug("failureDeleteInvestigationByIdTest_WithInvalidInvestigationId()");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO);
        thrownException.expect(IllegalArgumentException.class);

        replay(mockNamedParameterJdbcTemplate);
        replay(mockInvestigationDao);

        investigationService.deleteInvestigationById(INVALID_ID);
    }

}
