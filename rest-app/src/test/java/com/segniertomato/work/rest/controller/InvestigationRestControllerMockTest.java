package com.segniertomato.work.rest.controller;


import com.segniertomato.work.message.MessageError;
import com.segniertomato.work.model.Employee;
import com.segniertomato.work.model.Investigation;
import com.segniertomato.work.rest.CustomObjectMapper;
import com.segniertomato.work.rest.OffsetDateTimeFormatter;
import com.segniertomato.work.rest.RestErrorHandler;
import com.segniertomato.work.service.InvestigationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.Resource;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring-test-rest-mock.xml"})
public class InvestigationRestControllerMockTest {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final DateTimeFormatter OFFSET_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private static final String DEFAULT_LIMIT = "10";
    private static final String DEFAULT_OFFSET = "0";

    private static final String VERSION = "v1";

    private static final String OFFSET = "offset";
    private static final String LIMIT = "limit";
    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";

    private static final int EXISTS_EMPLOYEE_ID = 3;
    private static final int NOT_EXISTS_ID = 5;
    private static final int INVALID_ID = -5;

    private static final OffsetDateTime FIRST_TEST_DATE;
    private static final OffsetDateTime SECOND_TEST_DATE;

    private static final String EXPECTED_JSON_RESPONSE;
    private static final Investigation sFirstTestInvestigation;

    static {

        FIRST_TEST_DATE = OffsetDateTime.parse("2010-03-15T00:00:00Z", OFFSET_DATE_TIME_FORMATTER);
        SECOND_TEST_DATE = OffsetDateTime.parse("2015-01-01T00:00:00Z", OFFSET_DATE_TIME_FORMATTER);

        sFirstTestInvestigation = new Investigation(2, 2, "Some title",
                "Some interesting description.", FIRST_TEST_DATE, SECOND_TEST_DATE);

        EXPECTED_JSON_RESPONSE = "{investigationId:" + sFirstTestInvestigation.getInvestigationId() +
                ",number:" + sFirstTestInvestigation.getNumber() + ",title:'" + sFirstTestInvestigation.getTitle() +
                "',description:'" + sFirstTestInvestigation.getDescription() +
                "',startInvestigationDate:'" + sFirstTestInvestigation.getStartInvestigationDate().format(OFFSET_DATE_TIME_FORMATTER) +
                "',endInvestigationDate:'" + sFirstTestInvestigation.getEndInvestigationDate().format(OFFSET_DATE_TIME_FORMATTER) +
                "'}";
    }

    @Resource
    private InvestigationRestController investigationRestController;

    private MockMvc mockMvc;

    @Autowired
    private InvestigationService mockInvestigationService;

    @Before
    public void setUp() {

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(CustomObjectMapper.objectMapper());

        FormattingConversionService conversionService = new FormattingConversionService();
        conversionService.addFormatterForFieldType(OffsetDateTime.class, new OffsetDateTimeFormatter(OFFSET_DATE_TIME_FORMATTER));

        mockMvc = standaloneSetup(investigationRestController)
                .setMessageConverters(converter)
                .setConversionService(conversionService)
                .setControllerAdvice(new RestErrorHandler())
                .build();
    }

    @After
    public void clear() {

        verify(mockInvestigationService);
        reset(mockInvestigationService);
    }

    @Test
    public void successfulGetInvestigationsTest() throws Exception {

        LOGGER.debug("successfulGetInvestigationsTest()");

        expect(mockInvestigationService.getAllInvestigations(anyInt(), anyInt())).andReturn(Arrays.asList(sFirstTestInvestigation));
        replay(mockInvestigationService);

        mockMvc.perform(
                get("/api/" + VERSION + "/investigations")
                        .param(OFFSET, "1")
                        .param(LIMIT, "1")

        ).andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json("[" + EXPECTED_JSON_RESPONSE + "]", false))
                .andExpect(status().isOk());
    }

    @Test
    public void successfulGetInvestigationsTest_WithoutLimitAndOffset() throws Exception {

        LOGGER.debug("successfulGetInvestigationsTest_WithoutLimitAndOffset()");

        expect(mockInvestigationService.getAllInvestigations(Integer.valueOf(DEFAULT_OFFSET), Integer.valueOf(DEFAULT_LIMIT)))
                .andReturn(Arrays.asList(sFirstTestInvestigation));
        replay(mockInvestigationService);

        mockMvc.perform(
                get("/api/" + VERSION + "/investigations")

        ).andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json("[" + EXPECTED_JSON_RESPONSE + "]", false))
                .andExpect(status().isOk());
    }

    @Test
    public void successfulGetInvestigationsBetweenPeriod() throws Exception {

        LOGGER.debug("successfulGetInvestigationsBetweenPeriod()");

        String testStartInvestigationDate = "2009-03-15T00:00:00+01:00";
        String testEndInvestigationDate = "2016-03-15T00:00:00Z";

        expect(mockInvestigationService.getInvestigationsBetweenPeriod(
                isA(OffsetDateTime.class), isA(OffsetDateTime.class), anyInt(), anyInt()))
                .andReturn(Arrays.asList(sFirstTestInvestigation));

        replay(mockInvestigationService);

        mockMvc.perform(
                get("/api/" + VERSION + "/investigations/filter")
                        .param(OFFSET, "1")
                        .param(LIMIT, "1")
                        .param(START_DATE, testStartInvestigationDate)
                        .param(END_DATE, testEndInvestigationDate)
        ).andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json("[" + EXPECTED_JSON_RESPONSE + "]", false))
                .andExpect(status().isOk());
    }


    @Test
    public void successfulGetInvestigationsBetweenPeriod_WithoutLimitAndOffset() throws Exception {

        LOGGER.debug("successfulGetInvestigationsBetweenPeriod_WithoutLimitAndOffset()");

        String testStartInvestigationDate = "2009-03-15T00:00:00+01:00";
        String testEndInvestigationDate = "2016-03-15T00:00:00Z";

        expect(mockInvestigationService.getInvestigationsBetweenPeriod(
                isA(OffsetDateTime.class), isA(OffsetDateTime.class), anyInt(), anyInt()
        )).andReturn(Arrays.asList(sFirstTestInvestigation));

        replay(mockInvestigationService);

        mockMvc.perform(
                get("/api/" + VERSION + "/investigations/filter")
                        .param(START_DATE, testStartInvestigationDate)
                        .param(END_DATE, testEndInvestigationDate)
        ).andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json("[" + EXPECTED_JSON_RESPONSE + "]", false))
                .andExpect(status().isOk());
    }

    @Test
    public void failureGetInvestigationsBetweenPeriodTest_WithInvalidPeriodDate() throws Exception {

        LOGGER.debug("failureGetInvestigationsBetweenPeriodTest_WithInvalidPeriodDate()");

        String testStartInvestigationDate = "2016-03-15T00:00:00Z";
        String testEndInvestigationDate = "2009-03-15T00:00:00+01:00";

        expect(mockInvestigationService.getInvestigationsBetweenPeriod(
                isA(OffsetDateTime.class), isA(OffsetDateTime.class), anyInt(), anyInt()
        )).andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.START_AND_END_DATES_SHOULD_MATCH_PATTERN));

        replay(mockInvestigationService);

        mockMvc.perform(
                get("/api/" + VERSION + "/investigations/filter")
                        .param(START_DATE, testStartInvestigationDate)
                        .param(END_DATE, testEndInvestigationDate)
                        .param(OFFSET, "1")
                        .param(LIMIT, "1")
        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: " +
                        MessageError.InvalidIncomingParameters.START_AND_END_DATES_SHOULD_MATCH_PATTERN + "\""));
    }

    @Test
    public void failureGetInvestigationsBetweenPeriodTest_WithEmptyAndNullPeriodDate() throws Exception {

        LOGGER.debug("failureGetInvestigationsBetweenPeriodTest_WithEmptyAndNullPeriodDate()");

        expect(mockInvestigationService.getInvestigationsBetweenPeriod(
                anyObject(OffsetDateTime.class), anyObject(OffsetDateTime.class), anyInt(), anyInt()
        )).andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.START_DATE_IN_PERIOD_CAN_NOT_BE_NULL));
        replay(mockInvestigationService);

        /*
        * If you will try set parameter's value to null,
        * then method called 'param' will throw IllegalArgumentException
        * with a message "'values' must not be empty". And we can't see
        * behavior of a server when it received null values.
        * But in real life we can receive null value in request parameters.
        * How are we can test this situation?
        * Our custom conversion service can convert incoming request's parameters
        * from String to OffsetDateTime type. When incoming parameter equals
        * null or empty, service will convert these values
        * into null values of OffsetDateTime type.
        * So, behavior for empty value will be same like for null value.
        *
        * And when we will test empty value we can see what will happen when
        * our service will receive null.
        *
        * */

        mockMvc.perform(
                get("/api/" + VERSION + "/investigations/filter")
                        .param(START_DATE, "")
                        .param(END_DATE, "")
                        .param(OFFSET, "1")
                        .param(LIMIT, "1")
        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: " +
                        MessageError.InvalidIncomingParameters.START_DATE_IN_PERIOD_CAN_NOT_BE_NULL + "\""));
    }

    @Test
    public void successfulGetInvestigationByIdTest() throws Exception {

        LOGGER.debug("successfulGetInvestigationByIdTest()");

        expect(mockInvestigationService.getInvestigationById(isA(Integer.class))).andReturn(sFirstTestInvestigation);
        replay(mockInvestigationService);

        mockMvc.perform(
                get("/api/" + VERSION + "/investigations/" + sFirstTestInvestigation.getInvestigationId())
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        ).andDo(print())
                .andExpect(content().json(EXPECTED_JSON_RESPONSE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isFound());
    }

    @Test
    public void failureGetInvestigationByIdTest_WithInvalidId() throws Exception {

        LOGGER.debug("failureGetInvestigationByIdTest_WithInvalidId()");

        expect(mockInvestigationService.getInvestigationById(isA(Integer.class)))
                .andThrow(new IllegalArgumentException(MessageError.INVESTIGATION_NOT_EXISTS));
        replay(mockInvestigationService);

        mockMvc.perform(
                get("/api/" + VERSION + "/investigations/" + INVALID_ID)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: " + MessageError.INVESTIGATION_NOT_EXISTS + "\""));

    }


    @Test
    public void failureGetInvestigationByIdTest_WithNotExistsId() throws Exception {

        LOGGER.debug("failureGetInvestigationByIdTest_WithNotExistsId()");

        expect(mockInvestigationService.getInvestigationById(isA(Integer.class)))
                .andThrow(new IllegalArgumentException(MessageError.INVESTIGATION_NOT_EXISTS));
        replay(mockInvestigationService);

        mockMvc.perform(
                get("/api/" + VERSION + "/investigations/" + NOT_EXISTS_ID)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: " +
                        MessageError.INVESTIGATION_NOT_EXISTS + "\""));

    }

    @Test
    public void successfulGetEmployeeInvestigationsTest() throws Exception {

        LOGGER.debug("successfulGetEmployeeInvestigationsTest()");

        expect(mockInvestigationService.getEmployeeInvestigations(isA(Integer.class), anyInt(), anyInt()))
                .andReturn(Arrays.asList(sFirstTestInvestigation));

        replay(mockInvestigationService);

        mockMvc.perform(
                get("/api/" + VERSION + "/investigations/employee/" + EXISTS_EMPLOYEE_ID)
                        .param(OFFSET, "1")
                        .param(LIMIT, "1")

        ).andDo(print())
                .andExpect(status().isFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json("[" + EXPECTED_JSON_RESPONSE + "]"));
    }

    @Test
    public void successfulGetEmployeeInvestigationsTest_WithoutLimitAndOffset() throws Exception {

        LOGGER.debug("successfulGetEmployeeInvestigationsTest_WithoutLimitAndOffset()");

        expect(mockInvestigationService.getEmployeeInvestigations(isA(Integer.class), anyInt(), anyInt()))
                .andReturn(Arrays.asList(sFirstTestInvestigation));

        replay(mockInvestigationService);

        mockMvc.perform(
                get("/api/" + VERSION + "/investigations/employee/" + EXISTS_EMPLOYEE_ID)
                        .param(OFFSET, "1")
                        .param(LIMIT, "1")

        ).andDo(print())
                .andExpect(status().isFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json("[" + EXPECTED_JSON_RESPONSE + "]"));
    }

    @Test
    public void failureGetEmployeeInvestigationsTest_WithNotExistsId() throws Exception {

        LOGGER.debug("failureGetEmployeeInvestigationsTest_WithNotExistsId()");

        expect(mockInvestigationService.getEmployeeInvestigations(isA(Integer.class), anyInt(), anyInt()))
                .andThrow(new IllegalArgumentException(MessageError.EMPLOYEE_NOT_EXISTS));

        replay(mockInvestigationService);

        mockMvc.perform(
                get("/api/" + VERSION + "/investigations/employee/" + NOT_EXISTS_ID)
                        .param(OFFSET, "1")
                        .param(LIMIT, "1")

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: " +
                        MessageError.EMPLOYEE_NOT_EXISTS + "\""));

    }

    @Test
    public void failureGetEmployeeInvestigationsTest_WithInvalidId() throws Exception {

        LOGGER.debug("failureGetEmployeeInvestigationsTest_WithNotExistsId()");

        expect(mockInvestigationService.getEmployeeInvestigations(isA(Integer.class), anyInt(), anyInt()))
                .andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO));

        replay(mockInvestigationService);

        mockMvc.perform(
                get("/api/" + VERSION + "/investigations/employee/" + INVALID_ID)
                        .param(OFFSET, "1")
                        .param(LIMIT, "1")

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: " +
                        MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO + "\""));

    }

    @Test
    public void failureGetEmployeeInvestigationsTest_WithInvalidLimit() throws Exception {

        LOGGER.debug("failureGetEmployeeInvestigationsTest_WithInvalidLimit()");

        int invalidLimit = -5;

        expect(mockInvestigationService.getEmployeeInvestigations(isA(Integer.class), anyInt(), anyInt()))
                .andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.LIMIT_CAN_NOT_BE_LOWER_THAN_ZERO));

        replay(mockInvestigationService);

        mockMvc.perform(
                get("/api/" + VERSION + "/investigations/employee/" + EXISTS_EMPLOYEE_ID)
                        .param(OFFSET, "1")
                        .param(LIMIT, String.valueOf(invalidLimit))

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: " +
                        MessageError.InvalidIncomingParameters.LIMIT_CAN_NOT_BE_LOWER_THAN_ZERO + "\""));

    }

    @Test
    public void failureGetEmployeeInvestigationsTest_WithInvalidOffset() throws Exception {

        LOGGER.debug("failureGetEmployeeInvestigationsTest_WithInvalidOffset()");

        int invalidLimit = -5;

        expect(mockInvestigationService.getEmployeeInvestigations(isA(Integer.class), anyInt(), anyInt()))
                .andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.OFFSET_CAN_NOT_BE_LOWER_THAN_ZERO));

        replay(mockInvestigationService);

        mockMvc.perform(
                get("/api/" + VERSION + "/investigations/employee/" + EXISTS_EMPLOYEE_ID)
                        .param(LIMIT, "1")
                        .param(OFFSET, String.valueOf(invalidLimit))

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: " +
                        MessageError.InvalidIncomingParameters.OFFSET_CAN_NOT_BE_LOWER_THAN_ZERO + "\""));

    }

    @Test
    public void successfulAddInvestigationTest_WithInvolvedInvestigations() throws Exception {

        LOGGER.debug("successfulAddInvestigationTest_WithInvolvedInvestigations()");

        expect(mockInvestigationService.addInvestigation(isA(Investigation.class)))
                .andReturn(NOT_EXISTS_ID);
        replay(mockInvestigationService);

        Investigation newInvestigation = new Investigation(2, "Some title",
                "Some interesting description.", FIRST_TEST_DATE, SECOND_TEST_DATE);

        newInvestigation.setInvolvedStaff(
                Arrays.asList(new Employee(EXISTS_EMPLOYEE_ID, "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"))));

        String serializedInvestigation = CustomObjectMapper.objectMapper().writeValueAsString(newInvestigation);

        mockMvc.perform(
                post("/api/" + VERSION + "/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedInvestigation)

        ).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(String.valueOf(NOT_EXISTS_ID)));
    }

    @Test
    public void successfulAddInvestigationTest_WithoutInvolvedInvestigations() throws Exception {

        LOGGER.debug("successfulAddInvestigationTest_WithoutInvolvedInvestigations()");

        expect(mockInvestigationService.addInvestigation(isA(Investigation.class)))
                .andReturn(NOT_EXISTS_ID);
        replay(mockInvestigationService);

        Investigation newInvestigation = new Investigation(2, "Some title",
                "Some interesting description.", FIRST_TEST_DATE, SECOND_TEST_DATE);

        String serializedInvestigation = CustomObjectMapper.objectMapper().writeValueAsString(newInvestigation);

        mockMvc.perform(
                post("/api/" + VERSION + "/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedInvestigation)

        ).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(String.valueOf(NOT_EXISTS_ID)));
    }

    @Test
    public void failureAddInvestigationTest_WithInvalidInvestigationId() throws Exception {

        LOGGER.debug("failureAddInvestigationTest_WithInvalidInvestigationId()");

        expect(mockInvestigationService.addInvestigation(isA(Investigation.class))).andThrow(
                new IllegalArgumentException(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_NULL_OR_MINUS_ONE));
        replay(mockInvestigationService);

        Investigation newInvestigation = new Investigation(INVALID_ID, 2, "Some title",
                "Some interesting description.", FIRST_TEST_DATE, SECOND_TEST_DATE);

        String serializedInvestigation = CustomObjectMapper.objectMapper().writeValueAsString(newInvestigation);

        mockMvc.perform(
                post("/api/" + VERSION + "/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedInvestigation)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: " +
                        MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_NULL_OR_MINUS_ONE + "\""));
    }

    @Test
    public void failureAddInvestigationTest_WithInvalidInvestigationDates() throws Exception {

        LOGGER.debug("failureAddInvestigationTest_WithInvalidInvestigationDates()");

        expect(mockInvestigationService.addInvestigation(isA(Investigation.class))).andThrow(
                new IllegalArgumentException(MessageError.InvalidIncomingParameters.START_AND_END_DATES_SHOULD_MATCH_PATTERN));
        replay(mockInvestigationService);

        Investigation newInvestigation = new Investigation(2, "Some title",
                "Some interesting description.", SECOND_TEST_DATE, FIRST_TEST_DATE);

        String serializedInvestigation = CustomObjectMapper.objectMapper().writeValueAsString(newInvestigation);

        mockMvc.perform(
                post("/api/" + VERSION + "/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedInvestigation)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: " +
                        MessageError.InvalidIncomingParameters.START_AND_END_DATES_SHOULD_MATCH_PATTERN + "\""));
    }

    @Test
    public void failureAddInvestigationTest_WithNotExistsEmployeesId() throws Exception {

        LOGGER.debug("failureAddInvestigationTest_WithNotExistsEmployeesId()");

        expect(mockInvestigationService.addInvestigation(isA(Investigation.class))).andThrow(
                new IllegalArgumentException(MessageError.EMPLOYEE_NOT_EXISTS));
        replay(mockInvestigationService);

        Investigation newInvestigation = new Investigation(2, "Some title",
                "Some interesting description.", FIRST_TEST_DATE, SECOND_TEST_DATE);

        newInvestigation.setInvolvedStaff(
                Arrays.asList(new Employee(NOT_EXISTS_ID, "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"))));

        String serializedInvestigation = CustomObjectMapper.objectMapper().writeValueAsString(newInvestigation);

        mockMvc.perform(
                post("/api/" + VERSION + "/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedInvestigation)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: " +
                        MessageError.EMPLOYEE_NOT_EXISTS + "\""));
    }

    @Test
    public void failureAddInvestigationTest_WithInvalidEmployeesId() throws Exception {

        LOGGER.debug("failureAddInvestigationTest_WithInvalidEmployeesId()");

        expect(mockInvestigationService.addInvestigation(isA(Investigation.class))).andThrow(
                new IllegalArgumentException(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO));
        replay(mockInvestigationService);

        Investigation newInvestigation = new Investigation(2, "Some title",
                "Some interesting description.", FIRST_TEST_DATE, SECOND_TEST_DATE);

        newInvestigation.setInvolvedStaff(
                Arrays.asList(new Employee(INVALID_ID, "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"))));

        String serializedInvestigation = CustomObjectMapper.objectMapper().writeValueAsString(newInvestigation);

        mockMvc.perform(
                post("/api/" + VERSION + "/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedInvestigation)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: " +
                        MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO + "\""));
    }

    @Test
    public void failureAddInvestigationTest_WithNullEmployeesId() throws Exception {

        LOGGER.debug("failureAddInvestigationTest_WithNullEmployeesId()");

        expect(mockInvestigationService.addInvestigation(isA(Investigation.class))).andThrow(
                new IllegalArgumentException(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL));
        replay(mockInvestigationService);

        Investigation newInvestigation = new Investigation(2, "Some title",
                "Some interesting description.", FIRST_TEST_DATE, SECOND_TEST_DATE);

        newInvestigation.setInvolvedStaff(
                Arrays.asList(new Employee(null, "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"))));

        String serializedInvestigation = CustomObjectMapper.objectMapper().writeValueAsString(newInvestigation);

        mockMvc.perform(
                post("/api/" + VERSION + "/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedInvestigation)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: " +
                        MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL + "\""));
    }

    @Test
    public void failureAddInvestigationTest_WithInvalidBody() throws Exception {

        LOGGER.debug("failureAddInvestigationTest_WithInvalidBody()");

        replay(mockInvestigationService);

        mockMvc.perform(
                post("/api/" + VERSION + "/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content("null")

        ).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void successfulAddInvolvedStaff2InvestigationTest() throws Exception {

        LOGGER.debug("successfulAddInvolvedStaff2InvestigationTest()");

        List<Integer> employeesId = Arrays.asList(EXISTS_EMPLOYEE_ID);

        mockInvestigationService.addInvolvedStaff2Investigation(sFirstTestInvestigation.getInvestigationId(), employeesId);
        expectLastCall();
        replay(mockInvestigationService);

        String serializedEmployeesId = CustomObjectMapper.objectMapper().writeValueAsString(employeesId);

        mockMvc.perform(
                post("/api/" + VERSION + "/investigations/" + sFirstTestInvestigation.getInvestigationId() + "/staff")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployeesId)

        ).andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void failureAddInvolvedStaff2InvestigationTest_WithNullInvestigationId() throws Exception {

        LOGGER.debug("failureAddInvolvedStaff2InvestigationTest_WithNullInvestigationId()");

        List<Integer> employeesId = Arrays.asList(EXISTS_EMPLOYEE_ID);

        replay(mockInvestigationService);

        String serializedEmployeesId = CustomObjectMapper.objectMapper().writeValueAsString(employeesId);

        /*
        * Spring's conversion service can't convert string "null"
        * into primitive types like 'int', and will throw
        * IllegalArgumentException.
        *
        * */
        mockMvc.perform(
                post("/api/" + VERSION + "/investigations/null/staff")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployeesId)

        ).andDo(print())
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void failureAddInvolvedStaff2InvestigationTest_WithInvalidInvestigationId() throws Exception {

        LOGGER.debug("failureAddInvolvedStaff2InvestigationTest_WithInvalidInvestigationId()");

        List<Integer> employeesId = Arrays.asList(EXISTS_EMPLOYEE_ID);

        mockInvestigationService.addInvolvedStaff2Investigation(INVALID_ID, employeesId);
        expectLastCall().andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO));
        replay(mockInvestigationService);

        String serializedEmployeesId = CustomObjectMapper.objectMapper().writeValueAsString(employeesId);

        mockMvc.perform(
                post("/api/" + VERSION + "/investigations/" + INVALID_ID + "/staff")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployeesId)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: " +
                        MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO + "\""));
    }

    @Test
    public void failureAddInvolvedStaff2InvestigationTest_WithNotExistsInvestigationId() throws Exception {

        LOGGER.debug("failureAddInvolvedStaff2InvestigationTest_WithNotExistsInvestigationId()");

        List<Integer> employeesId = Arrays.asList(EXISTS_EMPLOYEE_ID);

        mockInvestigationService.addInvolvedStaff2Investigation(NOT_EXISTS_ID, employeesId);
        expectLastCall().andThrow(new IllegalArgumentException(MessageError.INVESTIGATION_NOT_EXISTS));
        replay(mockInvestigationService);

        String serializedEmployeesId = CustomObjectMapper.objectMapper().writeValueAsString(employeesId);

        mockMvc.perform(
                post("/api/" + VERSION + "/investigations/" + NOT_EXISTS_ID + "/staff")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployeesId)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: " +
                        MessageError.INVESTIGATION_NOT_EXISTS + "\""));
    }

    @Test
    public void failureAddInvolvedStaff2InvestigationTest_WithNullInvolvedStaff() throws Exception {

        LOGGER.debug("failureAddInvolvedStaff2InvestigationTest_WithNullInvolvedStaff()");

        replay(mockInvestigationService);

        mockMvc.perform(
                post("/api/" + VERSION + "/investigations/" + sFirstTestInvestigation.getInvestigationId() + "/staff")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content("null")

        ).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void failureAddInvolvedStaff2InvestigationTest_WithNullEmployeeId() throws Exception {

        LOGGER.debug("failureAddInvolvedStaff2InvestigationTest_WithNullEmployeeId()");

        List<Integer> employeesId = Arrays.asList(EXISTS_EMPLOYEE_ID, null);

        mockInvestigationService.addInvolvedStaff2Investigation(sFirstTestInvestigation.getInvestigationId(), employeesId);
        expectLastCall().andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL));
        replay(mockInvestigationService);

        String serializedEmployeesId = CustomObjectMapper.objectMapper().writeValueAsString(employeesId);

        mockMvc.perform(
                post("/api/" + VERSION + "/investigations/" + sFirstTestInvestigation.getInvestigationId() + "/staff")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployeesId)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: " +
                        MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL + "\""));
    }

    @Test
    public void failureAddInvolvedStaff2InvestigationTest_WithNotExistsEmployeeId() throws Exception {

        LOGGER.debug("failureAddInvolvedStaff2InvestigationTest_WithNotExistsEmployeeId()");

        List<Integer> employeesId = Arrays.asList(EXISTS_EMPLOYEE_ID, NOT_EXISTS_ID);

        mockInvestigationService.addInvolvedStaff2Investigation(sFirstTestInvestigation.getInvestigationId(), employeesId);
        expectLastCall().andThrow(new IllegalArgumentException(MessageError.EMPLOYEE_NOT_EXISTS));
        replay(mockInvestigationService);

        String serializedEmployeesId = CustomObjectMapper.objectMapper().writeValueAsString(employeesId);

        mockMvc.perform(
                post("/api/" + VERSION + "/investigations/" + sFirstTestInvestigation.getInvestigationId() + "/staff")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployeesId)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: " +
                        MessageError.EMPLOYEE_NOT_EXISTS + "\""));
    }

    @Test
    public void successfulUpdateInvestigationTest_WithoutInvolvedStaff() throws Exception {

        LOGGER.debug("successfulUpdateInvestigationTest_WithoutInvolvedStaff()");

        expect(mockInvestigationService.updateInvestigation(isA(Investigation.class))).andReturn(true);
        replay(mockInvestigationService);

        Investigation updateInvestigation = new Investigation(sFirstTestInvestigation.getInvestigationId(), 2, "Some title",
                "Some interesting description.", FIRST_TEST_DATE, SECOND_TEST_DATE);

        String serializedInvestigation = CustomObjectMapper.objectMapper().writeValueAsString(updateInvestigation);

        mockMvc.perform(
                put("/api/" + VERSION + "/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedInvestigation)

        ).andDo(print())
                .andExpect(status().isAccepted());
    }

    @Test
    public void successfulUpdateInvestigationTest_WithInvolvedStaff() throws Exception {

        LOGGER.debug("successfulUpdateInvestigationTest_WithInvolvedStaff()");

        expect(mockInvestigationService.updateInvestigation(isA(Investigation.class))).andReturn(true);
        replay(mockInvestigationService);

        Investigation updateInvestigation = new Investigation(sFirstTestInvestigation.getInvestigationId(), 2, "Some title",
                "Some interesting description.", FIRST_TEST_DATE, SECOND_TEST_DATE);

        updateInvestigation.setInvolvedStaff(
                Arrays.asList(new Employee(EXISTS_EMPLOYEE_ID, "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"))));

        String serializedInvestigation = CustomObjectMapper.objectMapper().writeValueAsString(updateInvestigation);

        mockMvc.perform(
                put("/api/" + VERSION + "/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedInvestigation)

        ).andDo(print())
                .andExpect(status().isAccepted());
    }

    @Test
    public void failureUpdateInvestigationTest_WithNullInvestigation() throws Exception {

        LOGGER.debug("failureUpdateInvestigationTest_WithNullInvestigation()");

        replay(mockInvestigationService);

        mockMvc.perform(
                put("/api/" + VERSION + "/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content("null")

        ).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void failureUpdateInvestigationTest_WithNullInvolvedStaff() throws Exception {

        LOGGER.debug("failureUpdateInvestigationTest_WithNullInvolvedStaff()");

        expect(mockInvestigationService.updateInvestigation(isA(Investigation.class))).andThrow(
                new IllegalArgumentException(MessageError.InvalidIncomingParameters.INVESTIGATION_INVOLVED_STAFF_CAN_NOT_BE_NULL));
        replay(mockInvestigationService);

        Investigation updateInvestigation = new Investigation(sFirstTestInvestigation.getInvestigationId(), 2, "Some title",
                "Some interesting description.", FIRST_TEST_DATE, SECOND_TEST_DATE);

        updateInvestigation.setInvolvedStaff(null);

        String serializedInvestigation = CustomObjectMapper.objectMapper().writeValueAsString(updateInvestigation);

        mockMvc.perform(
                put("/api/" + VERSION + "/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedInvestigation)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: " +
                        MessageError.InvalidIncomingParameters.INVESTIGATION_INVOLVED_STAFF_CAN_NOT_BE_NULL + "\""));
    }

    @Test
    public void failureUpdateInvestigationTest_WithNotExistsInvestigation() throws Exception {

        LOGGER.debug("failureUpdateInvestigationTest_WithNotExistsInvestigation()");

        expect(mockInvestigationService.updateInvestigation(isA(Investigation.class))).andThrow(
                new IllegalArgumentException(MessageError.INVESTIGATION_NOT_EXISTS));
        replay(mockInvestigationService);

        Investigation updateInvestigation = new Investigation(NOT_EXISTS_ID, 2, "Some title",
                "Some interesting description.", FIRST_TEST_DATE, SECOND_TEST_DATE);

        String serializedInvestigation = CustomObjectMapper.objectMapper().writeValueAsString(updateInvestigation);

        mockMvc.perform(
                put("/api/" + VERSION + "/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedInvestigation)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: " +
                        MessageError.INVESTIGATION_NOT_EXISTS + "\""));
    }

    @Test
    public void failureUpdateInvestigationTest_WithNotExistsEmployee() throws Exception {

        LOGGER.debug("failureUpdateInvestigationTest_WithNotExistsEmployee()");

        expect(mockInvestigationService.updateInvestigation(isA(Investigation.class))).andThrow(
                new IllegalArgumentException(MessageError.EMPLOYEE_NOT_EXISTS));
        replay(mockInvestigationService);

        Investigation updateInvestigation = new Investigation(sFirstTestInvestigation.getInvestigationId(), 2, "Some title",
                "Some interesting description.", FIRST_TEST_DATE, SECOND_TEST_DATE);

        updateInvestigation.setInvolvedStaff(
                Arrays.asList(new Employee(NOT_EXISTS_ID, "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"))));
        String serializedInvestigation = CustomObjectMapper.objectMapper().writeValueAsString(updateInvestigation);

        mockMvc.perform(
                put("/api/" + VERSION + "/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedInvestigation)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: " +
                        MessageError.EMPLOYEE_NOT_EXISTS + "\""));
    }

    @Test
    public void failureUpdateInvestigationTest_WithNullInvolvedStaffEmployeeId() throws Exception {

        LOGGER.debug("failureUpdateInvestigationTest_WithNullInvolvedStaffEmployeeId()");

        expect(mockInvestigationService.updateInvestigation(isA(Investigation.class))).andThrow(
                new IllegalArgumentException(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL));
        replay(mockInvestigationService);

        Investigation updateInvestigation = new Investigation(sFirstTestInvestigation.getInvestigationId(), 2, "Some title",
                "Some interesting description.", FIRST_TEST_DATE, SECOND_TEST_DATE);

        updateInvestigation.setInvolvedStaff(
                Arrays.asList(new Employee(null, "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"))));
        String serializedInvestigation = CustomObjectMapper.objectMapper().writeValueAsString(updateInvestigation);

        mockMvc.perform(
                put("/api/" + VERSION + "/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedInvestigation)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: " +
                        MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL + "\""));
    }

    @Test
    public void successfulUpdateInvolvedStaffInInvestigationTest() throws Exception {

        LOGGER.debug("successfulUpdateInvolvedStaffInInvestigationTest()");

        List<Integer> employeesId = Arrays.asList(EXISTS_EMPLOYEE_ID);

        expect(mockInvestigationService.updateInvolvedStaffInInvestigation(sFirstTestInvestigation.getInvestigationId(), employeesId))
                .andReturn(true);
        replay(mockInvestigationService);

        String serializedEmployeesId = CustomObjectMapper.objectMapper().writeValueAsString(employeesId);

        mockMvc.perform(
                put("/api/" + VERSION + "/investigations/" + sFirstTestInvestigation.getInvestigationId() + "/staff")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployeesId)

        ).andDo(print())
                .andExpect(status().isAccepted());
    }

    @Test
    public void failureUpdateInvolvedStaffInInvestigationTest_WithNullInvestigationId() throws Exception {

        LOGGER.debug("failureUpdateInvolvedStaffInInvestigationTest_WithNullInvestigationId(");

        List<Integer> employeesId = Arrays.asList(EXISTS_EMPLOYEE_ID);

        replay(mockInvestigationService);

        String serializedEmployeesId = CustomObjectMapper.objectMapper().writeValueAsString(employeesId);

        /*
        * Spring's conversion service can't convert string "null"
        * into primitive types like 'int', and will throw
        * IllegalArgumentException.
        *
        * */

        mockMvc.perform(
                put("/api/" + VERSION + "/investigations/null/staff")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployeesId)

        ).andDo(print())
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void failureUpdateInvolvedStaffInInvestigationTest_WithInvalidInvestigationId() throws Exception {

        LOGGER.debug("failureUpdateInvolvedStaffInInvestigationTest_WithInvalidInvestigationId()");

        List<Integer> employeesId = Arrays.asList(EXISTS_EMPLOYEE_ID);

        expect(mockInvestigationService.updateInvolvedStaffInInvestigation(INVALID_ID, employeesId))
                .andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO));
        replay(mockInvestigationService);

        String serializedEmployeesId = CustomObjectMapper.objectMapper().writeValueAsString(employeesId);

        mockMvc.perform(
                put("/api/" + VERSION + "/investigations/" + INVALID_ID + "/staff")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployeesId)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: " +
                        MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO + "\""));
    }

    @Test
    public void failureUpdateInvolvedStaffInInvestigationTest_WithNotExistsInvestigationId() throws Exception {

        LOGGER.debug("failureUpdateInvolvedStaffInInvestigationTest_WithNotExistsInvestigationId()");

        List<Integer> employeesId = Arrays.asList(EXISTS_EMPLOYEE_ID);

        expect(mockInvestigationService.updateInvolvedStaffInInvestigation(NOT_EXISTS_ID, employeesId))
                .andThrow(new IllegalArgumentException(MessageError.INVESTIGATION_NOT_EXISTS));
        replay(mockInvestigationService);

        String serializedEmployeesId = CustomObjectMapper.objectMapper().writeValueAsString(employeesId);

        mockMvc.perform(
                put("/api/" + VERSION + "/investigations/" + NOT_EXISTS_ID + "/staff")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployeesId)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: " +
                        MessageError.INVESTIGATION_NOT_EXISTS + "\""));
    }

    @Test
    public void failureUpdateInvolvedStaffInInvestigationTest_WithNullInvolvedStaff() throws Exception {

        LOGGER.debug("failureUpdateInvolvedStaffInInvestigationTest_WithNullInvolvedStaff()");

        List<Integer> employeesId = Arrays.asList(EXISTS_EMPLOYEE_ID);

        replay(mockInvestigationService);

        mockMvc.perform(
                put("/api/" + VERSION + "/investigations/" + sFirstTestInvestigation.getInvestigationId() + "/staff")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content("null")

        ).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void failureUpdateInvolvedStaffInInvestigationTest_WithNullEmployeeId() throws Exception {

        LOGGER.debug("failureUpdateInvolvedStaffInInvestigationTest_WithNullEmployeeId()");

        List<Integer> employeesId = Arrays.asList(EXISTS_EMPLOYEE_ID, null);

        expect(mockInvestigationService.updateInvolvedStaffInInvestigation(sFirstTestInvestigation.getInvestigationId(), employeesId))
                .andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL));
        replay(mockInvestigationService);

        String serializedEmployeesId = CustomObjectMapper.objectMapper().writeValueAsString(employeesId);

        mockMvc.perform(
                put("/api/" + VERSION + "/investigations/" + sFirstTestInvestigation.getInvestigationId() + "/staff")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployeesId)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: " +
                        MessageError.InvalidIncomingParameters.EMPLOYEE_ID_CAN_NOT_BE_NULL + "\""));
    }

    @Test
    public void failureUpdateInvolvedStaffInInvestigationTest_WithNotExistsEmployeeId() throws Exception {

        LOGGER.debug("failureUpdateInvolvedStaffInInvestigationTest_WithNotExistsEmployeeId()");

        List<Integer> employeesId = Arrays.asList(NOT_EXISTS_ID);

        expect(mockInvestigationService.updateInvolvedStaffInInvestigation(sFirstTestInvestigation.getInvestigationId(), employeesId))
                .andThrow(new IllegalArgumentException(MessageError.EMPLOYEE_NOT_EXISTS));
        replay(mockInvestigationService);

        String serializedEmployeesId = CustomObjectMapper.objectMapper().writeValueAsString(employeesId);

        mockMvc.perform(
                put("/api/" + VERSION + "/investigations/" + sFirstTestInvestigation.getInvestigationId() + "/staff")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployeesId)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: " +
                        MessageError.EMPLOYEE_NOT_EXISTS + "\""));
    }

    @Test
    public void successfulDeleteInvestigationByIdTest() throws Exception {

        LOGGER.debug("successfulDeleteInvestigationByIdTest");

        expect(mockInvestigationService.deleteInvestigationById(sFirstTestInvestigation.getInvestigationId()))
                .andReturn(true);
        replay(mockInvestigationService);

        mockMvc.perform(
                delete("/api/" + VERSION + "/investigations/" + sFirstTestInvestigation.getInvestigationId())
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)

        ).andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void failureDeleteInvestigationByIdTest_WithNotExistsInvestigation() throws Exception {

        LOGGER.debug("failureDeleteInvestigationByIdTest_WithNotExistsInvestigation()");

        expect(mockInvestigationService.deleteInvestigationById(NOT_EXISTS_ID))
                .andThrow(new IllegalArgumentException(MessageError.INVESTIGATION_NOT_EXISTS));
        replay(mockInvestigationService);

        mockMvc.perform(
                delete("/api/" + VERSION + "/investigations/" + NOT_EXISTS_ID)
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: " +
                        MessageError.INVESTIGATION_NOT_EXISTS + "\""));
    }

    @Test
    public void failureDeleteInvestigationByIdTest_WithNullInvestigationId() throws Exception {

        LOGGER.debug("failureDeleteInvestigationByIdTest_WithNullInvestigationId()");

        replay(mockInvestigationService);

        /*
        * Spring's conversion service can't convert string "null"
        * into primitive types like 'int', and will throw
        * IllegalArgumentException.
        *
        * */

        mockMvc.perform(
                delete("/api/" + VERSION + "/investigations/null")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)

        ).andDo(print())
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void failureDeleteInvestigationByIdTest_WithInvalidInvestigationId() throws Exception {

        LOGGER.debug("failureDeleteInvestigationByIdTest_WithInvalidInvestigationId()");

        expect(mockInvestigationService.deleteInvestigationById(INVALID_ID))
                .andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO));
        replay(mockInvestigationService);

        mockMvc.perform(
                delete("/api/" + VERSION + "/investigations/" + INVALID_ID)
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: " +
                        MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO + "\""));
    }

}
