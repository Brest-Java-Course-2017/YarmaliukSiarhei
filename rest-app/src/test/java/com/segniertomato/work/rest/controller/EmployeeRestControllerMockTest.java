package com.segniertomato.work.rest.controller;


import com.segniertomato.work.message.MessageError;
import com.segniertomato.work.model.Employee;
import com.segniertomato.work.model.Investigation;
import com.segniertomato.work.model.Pair;
import com.segniertomato.work.rest.CustomObjectMapper;
import com.segniertomato.work.rest.RestErrorHandler;
import com.segniertomato.work.service.EmployeeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
public class EmployeeRestControllerMockTest {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private static final String DEFAULT_LIMIT = "10";
    private static final String DEFAULT_OFFSET = "0";

    private static final String VERSION = "v1";

    private static final String OFFSET = "offset";
    private static final String LIMIT = "limit";

    private static final String INVALID_EMPLOYEE_NAME = "Artur C.9Clark*";

    private static final Integer INVALID_ID = -2;
    private static final Integer NOT_EXISTS_ID = 5;

    private static final Integer EXISTS_INVESTIGATION_ID = 2;

    private static final String EXPECTED_JSON_RESPONSE;
    private static final Employee sFirstTestEmployee;

    static {
        sFirstTestEmployee = new Employee(1, "Some name", LocalDate.parse("1978-06-23", LOCAL_DATE_FORMATTER),
                LocalDate.parse("2002-11-13", LOCAL_DATE_FORMATTER));

        EXPECTED_JSON_RESPONSE = "{employeeId:" + sFirstTestEmployee.getEmployeeId() +
                ",name:'" + sFirstTestEmployee.getName() +
                "',age:'" + sFirstTestEmployee.getAge().format(LOCAL_DATE_FORMATTER) +
                "',startWorkingDate:'" + sFirstTestEmployee.getStartWorkingDate().format(LOCAL_DATE_FORMATTER) +
                "'}";
    }

    @Resource
    private EmployeeRestController employeeRestController;

    private MockMvc mockMvc;

    @Autowired
    private EmployeeService mockEmployeeService;

    @Before
    public void setUp() {

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(CustomObjectMapper.objectMapper());

//        FormattingConversionService conversionService = new FormattingConversionService();
//        conversionService.addFormatterForFieldType(OffsetDateTime.class, new OffsetDateTimeFormatter(LOCAL_DATE_FORMATTER));

        mockMvc = standaloneSetup(employeeRestController)
                .setMessageConverters(converter)
//                .setConversionService(conversionService)
                .setControllerAdvice(new RestErrorHandler())
                .build();
    }

    @After
    public void clear() {

        verify(mockEmployeeService);
        reset(mockEmployeeService);
    }

    @Test
    public void successfulGetEmployeesTest() throws Exception {

        LOGGER.debug("successfulGetEmployeesTest()");

        expect(mockEmployeeService.getAllEmployees(anyInt(), anyInt())).andReturn(Arrays.asList(sFirstTestEmployee));
        replay(mockEmployeeService);

        mockMvc.perform(
                get("/api/" + VERSION + "/employees")
                        .param(OFFSET, "1")
                        .param(LIMIT, "1")

        ).andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json("[" + EXPECTED_JSON_RESPONSE + "]", false))
                .andExpect(status().isOk());
    }

    @Test
    public void successfulGetEmployeesTest_WithoutLimitAndOffset() throws Exception {

        LOGGER.debug("successfulGetEmployeesTest_WithoutLimitAndOffset()");

        expect(mockEmployeeService.getAllEmployees(Integer.valueOf(DEFAULT_OFFSET), Integer.valueOf(DEFAULT_LIMIT)))
                .andReturn(Arrays.asList(sFirstTestEmployee));
        replay(mockEmployeeService);

        mockMvc.perform(
                get("/api/" + VERSION + "/employees")

        ).andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json("[" + EXPECTED_JSON_RESPONSE + "]", false))
                .andExpect(status().isOk());
    }

    @Test
    public void successfulGetInvolvedEmployeesInInvestigationTest() throws Exception {

        LOGGER.debug("successfulGetInvolvedEmployeesInInvestigationTest()");

        List<Employee> employees = Arrays.asList(sFirstTestEmployee);

        expect(mockEmployeeService.getInvolvedEmployeesInInvestigation(isA(Integer.class), anyInt(), anyInt()))
                .andReturn(employees);
        replay(mockEmployeeService);

        mockMvc.perform(
                get("/api/" + VERSION + "/employees/investigation/" + EXISTS_INVESTIGATION_ID)
                        .param(OFFSET, "1")
                        .param(LIMIT, "1")

        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json("[" + EXPECTED_JSON_RESPONSE + "]", false));
    }

    @Test
    public void successfulGetInvolvedEmployeesInInvestigationTest_WithoutLimitAndOffset() throws Exception {

        LOGGER.debug("successfulGetInvolvedEmployeesInInvestigationTest_WithoutLimitAndOffset()");

        List<Employee> employees = Arrays.asList(sFirstTestEmployee);

        expect(mockEmployeeService.
                getInvolvedEmployeesInInvestigation(EXISTS_INVESTIGATION_ID, Integer.valueOf(DEFAULT_OFFSET), Integer.valueOf(DEFAULT_LIMIT)))
                .andReturn(employees);
        replay(mockEmployeeService);

        mockMvc.perform(
                get("/api/" + VERSION + "/employees/investigation/" + EXISTS_INVESTIGATION_ID)

        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json("[" + EXPECTED_JSON_RESPONSE + "]", false));
    }

    @Test
    public void failureGetInvolvedEmployeesInInvestigationTest_WithInvalidOffset() throws Exception {

        LOGGER.debug("failureGetInvolvedEmployeesInInvestigationTest_WithInvalidOffset()");

        expect(mockEmployeeService.getInvolvedEmployeesInInvestigation(isA(Integer.class), anyInt(), anyInt()))
                .andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.OFFSET_CAN_NOT_BE_LOWER_THAN_ZERO));
        replay(mockEmployeeService);

        mockMvc.perform(
                get("/api/" + VERSION + "/employees/investigation/" + EXISTS_INVESTIGATION_ID)
                        .param(OFFSET, "-5")
                        .param(LIMIT, "1")

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.InvalidIncomingParameters.OFFSET_CAN_NOT_BE_LOWER_THAN_ZERO + "\""));
    }

    @Test
    public void failureGetInvolvedEmployeesInInvestigationTest_WithInvalidLimit() throws Exception {

        LOGGER.debug("failureGetInvolvedEmployeesInInvestigationTest_WithInvalidLimit()");

        expect(mockEmployeeService.getInvolvedEmployeesInInvestigation(isA(Integer.class), anyInt(), anyInt()))
                .andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.LIMIT_CAN_NOT_BE_LOWER_THAN_ZERO));
        replay(mockEmployeeService);

        mockMvc.perform(
                get("/api/" + VERSION + "/employees/investigation/" + EXISTS_INVESTIGATION_ID)
                        .param(OFFSET, "1")
                        .param(LIMIT, "-5")

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.InvalidIncomingParameters.LIMIT_CAN_NOT_BE_LOWER_THAN_ZERO + "\""));
    }

    @Test
    public void failureGetInvolvedEmployeesInInvestigationTest_WithInvalidInvestigationId() throws Exception {

        LOGGER.debug("failureGetInvolvedEmployeesInInvestigationTest_WithInvalidInvestigationId()");

        expect(mockEmployeeService.getInvolvedEmployeesInInvestigation(isA(Integer.class), anyInt(), anyInt()))
                .andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO));
        replay(mockEmployeeService);

        mockMvc.perform(
                get("/api/" + VERSION + "/employees/investigation/" + INVALID_ID)
                        .param(OFFSET, "1")
                        .param(LIMIT, "1")

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO + "\""));
    }

    @Test
    public void failureGetInvolvedEmployeesInInvestigationTest_WithNullInvestigationId() throws Exception {

        LOGGER.debug("failureGetInvolvedEmployeesInInvestigationTest_WithNullInvestigationId()");

        replay(mockEmployeeService);

        /*
        * Spring's conversion service can't convert string "null"
        * into primitive types like 'int', and will throw
        * IllegalArgumentException.
        *
        * */

        mockMvc.perform(
                get("/api/" + VERSION + "/employees/investigation/null")
                        .param(OFFSET, "1")
                        .param(LIMIT, "1")

        ).andDo(print())
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void failureGetInvolvedEmployeesInInvestigationTest_WithNotExistsInvestigationId() throws Exception {

        LOGGER.debug("failureGetInvolvedEmployeesInInvestigationTest_WithNotExistsInvestigationId()");

        expect(mockEmployeeService.getInvolvedEmployeesInInvestigation(isA(Integer.class), anyInt(), anyInt()))
                .andThrow(new IllegalArgumentException(MessageError.INVESTIGATION_NOT_EXISTS));
        replay(mockEmployeeService);

        mockMvc.perform(
                get("/api/" + VERSION + "/employees/investigation/" + INVALID_ID)
                        .param(OFFSET, "1")
                        .param(LIMIT, "1")

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.INVESTIGATION_NOT_EXISTS + "\""));
    }

    @Test
    public void successfulGetEmployeeByIdTest() throws Exception {

        LOGGER.debug("successfulGetEmployeeByIdTest()");

        expect(mockEmployeeService.getEmployeeById(isA(Integer.class)))
                .andReturn(sFirstTestEmployee);
        replay(mockEmployeeService);

        mockMvc.perform(
                get("/api/" + VERSION + "/employees/" + sFirstTestEmployee.getEmployeeId())

        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json(EXPECTED_JSON_RESPONSE, false));
    }

    @Test
    public void failureGetEmployeeByIdTest_WithInvalidEmployeeId() throws Exception {

        LOGGER.debug("failureGetEmployeeByIdTest_WithInvalidEmployeeId()");

        expect(mockEmployeeService.getEmployeeById(isA(Integer.class)))
                .andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO));
        replay(mockEmployeeService);

        mockMvc.perform(
                get("/api/" + VERSION + "/employees/" + INVALID_ID)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO + "\""));
    }

    @Test
    public void failureGetEmployeeByIdTest_WithNullEmployeeId() throws Exception {

        LOGGER.debug("failureGetEmployeeByIdTest_WithNullEmployeeId()");

        replay(mockEmployeeService);

        mockMvc.perform(
                get("/api/" + VERSION + "/employees/null")

        ).andDo(print())
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void failureGetEmployeeByIdTest_WithNotExistsEmployeeId() throws Exception {

        LOGGER.debug("failureGetEmployeeByIdTest_WithNotExistsEmployeeId()");

        expect(mockEmployeeService.getEmployeeById(isA(Integer.class)))
                .andThrow(new IllegalArgumentException(MessageError.EMPLOYEE_NOT_EXISTS));
        replay(mockEmployeeService);

        mockMvc.perform(
                get("/api/" + VERSION + "/employees/" + NOT_EXISTS_ID)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.EMPLOYEE_NOT_EXISTS + "\""));
    }

    @Test
    public void successfulAddEmployeeTest_WithoutParticipatedInvestigations() throws Exception {

        LOGGER.debug("successfulAddEmployeeTest_WithoutParticipatedInvestigations()");

        expect(mockEmployeeService.addEmployee(isA(Employee.class)))
                .andReturn(NOT_EXISTS_ID);
        replay(mockEmployeeService);

        Employee newEmployee = new Employee("Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"));

        String serializedEmployee = CustomObjectMapper.objectMapper().writeValueAsString(newEmployee);

        mockMvc.perform(
                post("/api/" + VERSION + "/employees/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployee)

        ).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(String.valueOf(NOT_EXISTS_ID)));
    }

    @Test
    public void successfulAddEmployeeTest_WithParticipatedInvestigations() throws Exception {

        LOGGER.debug("successfulAddEmployeeTest_WithParticipatedInvestigations()");

        expect(mockEmployeeService.addEmployee(isA(Employee.class)))
                .andReturn(NOT_EXISTS_ID);
        replay(mockEmployeeService);

        Employee newEmployee = new Employee("Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"));

        newEmployee.setParticipatedInvestigations(Arrays.asList(new Investigation(2, 2, "Some title",
                "Some interesting description.", OffsetDateTime.parse("2010-03-15T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                OffsetDateTime.parse("2015-01-01T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME))));

        String serializedEmployee = CustomObjectMapper.objectMapper().writeValueAsString(newEmployee);

        mockMvc.perform(
                post("/api/" + VERSION + "/employees/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployee)

        ).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json(String.valueOf(NOT_EXISTS_ID)));

    }

    @Test
    public void failureAddEmployeeTest_WithInvalidEmployeeId() throws Exception {

        LOGGER.debug("failureAddEmployeeTest_WithInvalidEmployeeId()");

        expect(mockEmployeeService.addEmployee(isA(Employee.class)))
                .andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_NULL_OR_MINUS_ONE));
        replay(mockEmployeeService);

        Employee newEmployee = new Employee(INVALID_ID, "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"));

        String serializedEmployee = CustomObjectMapper.objectMapper().writeValueAsString(newEmployee);

        mockMvc.perform(
                post("/api/" + VERSION + "/employees/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployee)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_NULL_OR_MINUS_ONE + "\""));
    }

    @Test
    public void failureAddEmployeeTest_WithInvalidEmployeeName() throws Exception {

        LOGGER.debug("failureAddEmployeeTest_WithInvalidEmployeeName()");

        expect(mockEmployeeService.addEmployee(isA(Employee.class)))
                .andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.EMPLOYEE_NAME_SHOULD_MATCH_PATTERN));
        replay(mockEmployeeService);

        Employee newEmployee = new Employee(INVALID_EMPLOYEE_NAME, LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"));

        String serializedEmployee = CustomObjectMapper.objectMapper().writeValueAsString(newEmployee);

        mockMvc.perform(
                post("/api/" + VERSION + "/employees/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployee)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.InvalidIncomingParameters.EMPLOYEE_NAME_SHOULD_MATCH_PATTERN + "\""));
    }

    @Test
    public void failureAddEmployeeTest_WithInvalidBody() throws Exception {

        LOGGER.debug("failureAddEmployeeTest_WithInvalidBody()");

        replay(mockEmployeeService);

          /*
        * Spring's conversion service can't convert string "null"
        * into primitive types like 'int', and will throw
        * IllegalArgumentException.
        *
        * */
        mockMvc.perform(
                post("/api/" + VERSION + "/employees/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content("null")

        ).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void failureAddEmployeeTest_WithNullParticipatedInvestigations() throws Exception {

        LOGGER.debug("failureAddEmployeeTest_WithNullParticipatedInvestigations()");

        expect(mockEmployeeService.addEmployee(isA(Employee.class)))
                .andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.EMPLOYEE_PARTICIPATED_INVESTIGATIONS_CAN_NOT_BE_NULL));
        replay(mockEmployeeService);

        Employee newEmployee = new Employee(INVALID_ID, "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"));
        newEmployee.setParticipatedInvestigations(null);

        String serializedEmployee = CustomObjectMapper.objectMapper().writeValueAsString(newEmployee);

        mockMvc.perform(
                post("/api/" + VERSION + "/employees/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployee)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.InvalidIncomingParameters.EMPLOYEE_PARTICIPATED_INVESTIGATIONS_CAN_NOT_BE_NULL + "\""));
    }

    @Test
    public void failureAddEmployeeTest_WithInvalidParticipatedInvestigationsId() throws Exception {

        LOGGER.debug("failureAddEmployeeTest_WithInvalidParticipatedInvestigationsId()");

        expect(mockEmployeeService.addEmployee(isA(Employee.class)))
                .andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO));
        replay(mockEmployeeService);

        Employee newEmployee = new Employee(INVALID_ID, "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"));

        newEmployee.setParticipatedInvestigations(Arrays.asList(new Investigation(INVALID_ID, 2, "Some title",
                "Some interesting description.", OffsetDateTime.parse("2010-03-15T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                OffsetDateTime.parse("2015-01-01T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME))));

        String serializedEmployee = CustomObjectMapper.objectMapper().writeValueAsString(newEmployee);

        mockMvc.perform(
                post("/api/" + VERSION + "/employees/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployee)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO + "\""));
    }

    @Test
    public void failureAddEmployeeTest_WithNullParticipatedInvestigationsId() throws Exception {

        LOGGER.debug("failureAddEmployeeTest_WithNullParticipatedInvestigationsId()");

        expect(mockEmployeeService.addEmployee(isA(Employee.class)))
                .andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL));
        replay(mockEmployeeService);

        Employee newEmployee = new Employee("Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"));

        newEmployee.setParticipatedInvestigations(Arrays.asList(new Investigation(null, 2, "Some title",
                "Some interesting description.", OffsetDateTime.parse("2010-03-15T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                OffsetDateTime.parse("2015-01-01T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME))));

        String serializedEmployee = CustomObjectMapper.objectMapper().writeValueAsString(newEmployee);

        mockMvc.perform(
                post("/api/" + VERSION + "/employees/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployee)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL + "\""));
    }

    @Test
    public void failureAddEmployeeTest_WithNotExistsParticipatedInvestigationsId() throws Exception {

        LOGGER.debug("failureAddEmployeeTest_WithNotExistsParticipatedInvestigationsId()");

        expect(mockEmployeeService.addEmployee(isA(Employee.class)))
                .andThrow(new IllegalArgumentException(MessageError.INVESTIGATION_NOT_EXISTS));
        replay(mockEmployeeService);

        Employee newEmployee = new Employee("Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"));

        newEmployee.setParticipatedInvestigations(Arrays.asList(new Investigation(NOT_EXISTS_ID, 2, "Some title",
                "Some interesting description.", OffsetDateTime.parse("2010-03-15T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                OffsetDateTime.parse("2015-01-01T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME))));

        String serializedEmployee = CustomObjectMapper.objectMapper().writeValueAsString(newEmployee);

        mockMvc.perform(
                post("/api/" + VERSION + "/employees/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployee)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.INVESTIGATION_NOT_EXISTS + "\""));
    }


    @Test
    public void successfulAddInvestigations2EmployeeTest() throws Exception {

        LOGGER.debug("successfulAddInvestigations2EmployeeTest()");

        List<Integer> investigationsId = Arrays.asList(EXISTS_INVESTIGATION_ID);

        mockEmployeeService.addInvestigations2Employee(sFirstTestEmployee.getEmployeeId(), investigationsId);
        expectLastCall();
        replay(mockEmployeeService);

        String serializedInvestigationsId = CustomObjectMapper.objectMapper().writeValueAsString(investigationsId);

        mockMvc.perform(
                post("/api/" + VERSION + "/employees/" + sFirstTestEmployee.getEmployeeId() + "/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedInvestigationsId)

        ).andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void failureAddInvestigations2EmployeeTest_WithNullEmployeeId() throws Exception {

        LOGGER.debug("failureAddInvestigations2EmployeeTest_WithNullEmployeeId()");

        List<Integer> investigationsId = Arrays.asList(EXISTS_INVESTIGATION_ID);

        replay(mockEmployeeService);

        String serializedInvestigationsId = CustomObjectMapper.objectMapper().writeValueAsString(investigationsId);

        /*
        * Spring's conversion service can't convert string "null"
        * into primitive types like 'int', and will throw
        * IllegalArgumentException.
        *
        * */

        mockMvc.perform(
                post("/api/" + VERSION + "/employees/null/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedInvestigationsId)

        ).andDo(print())
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void failureAddInvestigations2EmployeeTest_WithInvalidEmployeeId() throws Exception {

        LOGGER.debug("failureAddInvestigations2EmployeeTest_WithInvalidEmployeeId()");

        List<Integer> investigationsId = Arrays.asList(EXISTS_INVESTIGATION_ID);

        mockEmployeeService.addInvestigations2Employee(INVALID_ID, investigationsId);

        expectLastCall()
                .andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO));
        replay(mockEmployeeService);

        String serializedInvestigationsId = CustomObjectMapper.objectMapper().writeValueAsString(investigationsId);

        mockMvc.perform(
                post("/api/" + VERSION + "/employees/" + INVALID_ID + "/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedInvestigationsId)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO + "\""));
    }

    @Test
    public void failureAddInvestigations2EmployeeTest_WithNotExistsEmployeeId() throws Exception {

        LOGGER.debug("failureAddInvestigations2EmployeeTest_WithNotExistsEmployeeId()");

        List<Integer> investigationsId = Arrays.asList(EXISTS_INVESTIGATION_ID);

        mockEmployeeService.addInvestigations2Employee(NOT_EXISTS_ID, investigationsId);

        expectLastCall()
                .andThrow(new IllegalArgumentException(MessageError.EMPLOYEE_NOT_EXISTS));
        replay(mockEmployeeService);

        String serializedInvestigationsId = CustomObjectMapper.objectMapper().writeValueAsString(investigationsId);

        mockMvc.perform(
                post("/api/" + VERSION + "/employees/" + NOT_EXISTS_ID + "/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedInvestigationsId)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.EMPLOYEE_NOT_EXISTS + "\""));
    }

    @Test
    public void failureAddInvestigations2EmployeesTest_WithNullInvestigations() throws Exception {

        LOGGER.debug("failureAddInvestigations2EmployeesTest_WithNullInvestigations()");

        replay(mockEmployeeService);

        mockMvc.perform(
                post("/api/" + VERSION + "/employees/" + sFirstTestEmployee.getEmployeeId() + "/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content("null")

        ).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void failureAddInvestigations2EmployeeTest_WithNullInvestigationId() throws Exception {

        LOGGER.debug("failureAddInvestigations2EmployeeTest_WithNullInvestigationId()");

        List<Integer> investigationsId = Arrays.asList(EXISTS_INVESTIGATION_ID, null);

        mockEmployeeService.addInvestigations2Employee(sFirstTestEmployee.getEmployeeId(), investigationsId);
        expectLastCall().andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL));
        replay(mockEmployeeService);

        String serializedInvestigationsId = CustomObjectMapper.objectMapper().writeValueAsString(investigationsId);

        mockMvc.perform(
                post("/api/" + VERSION + "/employees/" + sFirstTestEmployee.getEmployeeId() + "/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedInvestigationsId)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL + "\""));
    }

    @Test
    public void failureAddInvestigations2EmployeeTest_WithNotExistsInvestigationsId() throws Exception {

        LOGGER.debug("failureAddInvestigations2EmployeeTest_WithNotExistsInvestigationsId()");

        List<Integer> investigationsId = Arrays.asList(EXISTS_INVESTIGATION_ID, NOT_EXISTS_ID);

        mockEmployeeService.addInvestigations2Employee(sFirstTestEmployee.getEmployeeId(), investigationsId);
        expectLastCall().andThrow(new IllegalArgumentException(MessageError.INVESTIGATION_NOT_EXISTS));
        replay(mockEmployeeService);

        String serializedInvestigationsId = CustomObjectMapper.objectMapper().writeValueAsString(investigationsId);

        mockMvc.perform(
                post("/api/" + VERSION + "/employees/" + sFirstTestEmployee.getEmployeeId() + "/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedInvestigationsId)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.INVESTIGATION_NOT_EXISTS + "\""));
    }

    @Test
    public void successfulUpdateEmployeeTest_WithoutParticipatedInvestigations() throws Exception {

        LOGGER.debug("successfulUpdateEmployeeTest_WithoutParticipatedInvestigations()");

        expect(mockEmployeeService.updateEmployee(isA(Employee.class)))
                .andReturn(true);
        replay(mockEmployeeService);

        Employee updatedEmployee = new Employee(sFirstTestEmployee.getEmployeeId(),
                "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"));

        String serializedEmployee = CustomObjectMapper.objectMapper().writeValueAsString(updatedEmployee);

        mockMvc.perform(
                put("/api/" + VERSION + "/employees/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployee)

        ).andDo(print())
                .andExpect(status().isAccepted());
    }

    @Test
    public void successfulUpdateEmployeeTest_WithParticipatedInvestigations() throws Exception {

        LOGGER.debug("successfulUpdateEmployeeTest_WithParticipatedInvestigations()");

        expect(mockEmployeeService.updateEmployee(isA(Employee.class)))
                .andReturn(true);
        replay(mockEmployeeService);

        Employee updatedEmployee = new Employee(sFirstTestEmployee.getEmployeeId(),
                "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"));

        updatedEmployee.setParticipatedInvestigations(Arrays.asList(new Investigation(2, 2, "Some title",
                "Some interesting description.", OffsetDateTime.parse("2010-03-15T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                OffsetDateTime.parse("2015-01-01T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME))));

        String serializedEmployee = CustomObjectMapper.objectMapper().writeValueAsString(updatedEmployee);

        mockMvc.perform(
                put("/api/" + VERSION + "/employees/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployee)

        ).andDo(print())
                .andExpect(status().isAccepted());
    }

    @Test
    public void failureUpdateEmployeeTest_WithNullEmployee() throws Exception {

        LOGGER.debug("failureUpdateEmployeeTest_WithNullEmployee()");

        replay(mockEmployeeService);

        mockMvc.perform(
                put("/api/" + VERSION + "/employees/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content("null")

        ).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void failureUpdateEmployeeTest_WithNotExistsEmployee() throws Exception {

        LOGGER.debug("failureUpdateEmployeeTest_WithNotExistsEmployee()");

        mockEmployeeService.updateEmployee(isA(Employee.class));
        expectLastCall()
                .andThrow(new IllegalArgumentException(MessageError.EMPLOYEE_NOT_EXISTS));
        replay(mockEmployeeService);

        Employee updatedEmployee = new Employee(NOT_EXISTS_ID,
                "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"));

        String serializedEmployee = CustomObjectMapper.objectMapper().writeValueAsString(updatedEmployee);

        mockMvc.perform(
                put("/api/" + VERSION + "/employees/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployee)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.EMPLOYEE_NOT_EXISTS + "\""));
    }

    @Test
    public void failureUpdateEmployeeTest_WithNotInvalidEmployeeId() throws Exception {

        LOGGER.debug("failureUpdateEmployeeTest_WithNotInvalidEmployeeId()");

        mockEmployeeService.updateEmployee(isA(Employee.class));
        expectLastCall()
                .andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO));
        replay(mockEmployeeService);

        Employee updatedEmployee = new Employee(INVALID_ID,
                "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"));

        String serializedEmployee = CustomObjectMapper.objectMapper().writeValueAsString(updatedEmployee);

        mockMvc.perform(
                put("/api/" + VERSION + "/employees/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployee)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO + "\""));
    }

    @Test
    public void failureUpdateEmployeeTest_WithNotInvalidEmployeeName() throws Exception {

        LOGGER.debug("failureUpdateEmployeeTest_WithNotInvalidEmployeeName()");

        mockEmployeeService.updateEmployee(isA(Employee.class));
        expectLastCall()
                .andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.EMPLOYEE_NAME_SHOULD_MATCH_PATTERN));
        replay(mockEmployeeService);

        Employee updatedEmployee = new Employee(sFirstTestEmployee.getEmployeeId(),
                "Artur C. 9*Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"));

        String serializedEmployee = CustomObjectMapper.objectMapper().writeValueAsString(updatedEmployee);

        mockMvc.perform(
                put("/api/" + VERSION + "/employees/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployee)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.InvalidIncomingParameters.EMPLOYEE_NAME_SHOULD_MATCH_PATTERN + "\""));
    }

    @Test
    public void failureUpdateEmployeeTest_WithNullParticipatedInvestigations() throws Exception {

        LOGGER.debug("failureUpdateEmployeeTest_WithNullParticipatedInvestigations()");

        mockEmployeeService.updateEmployee(isA(Employee.class));
        expectLastCall()
                .andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.EMPLOYEE_PARTICIPATED_INVESTIGATIONS_CAN_NOT_BE_NULL));
        replay(mockEmployeeService);

        Employee updatedEmployee = new Employee(sFirstTestEmployee.getEmployeeId(),
                "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"));

        updatedEmployee.setParticipatedInvestigations(null);

        String serializedEmployee = CustomObjectMapper.objectMapper().writeValueAsString(updatedEmployee);

        mockMvc.perform(
                put("/api/" + VERSION + "/employees/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployee)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.InvalidIncomingParameters.EMPLOYEE_PARTICIPATED_INVESTIGATIONS_CAN_NOT_BE_NULL + "\""));
    }

    @Test
    public void failureUpdateEmployeeTest_WithNotExistsInvestigations() throws Exception {

        LOGGER.debug("failureUpdateInvestigationTest_WithNotExistsEmployee()");

        mockEmployeeService.updateEmployee(isA(Employee.class));
        expectLastCall()
                .andThrow(new IllegalArgumentException(MessageError.INVESTIGATION_NOT_EXISTS));
        replay(mockEmployeeService);

        Employee updatedEmployee = new Employee(sFirstTestEmployee.getEmployeeId(),
                "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"));

        updatedEmployee.setParticipatedInvestigations(Arrays.asList(new Investigation(NOT_EXISTS_ID, 2, "Some title",
                "Some interesting description.", OffsetDateTime.parse("2010-03-15T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                OffsetDateTime.parse("2015-01-01T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME))));

        String serializedEmployee = CustomObjectMapper.objectMapper().writeValueAsString(updatedEmployee);

        mockMvc.perform(
                put("/api/" + VERSION + "/employees/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployee)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.INVESTIGATION_NOT_EXISTS + "\""));
    }

    @Test
    public void failureUpdateEmployeeTest_WithInvalidInvestigationsId() throws Exception {

        LOGGER.debug("failureUpdateEmployeeTest_WithInvalidInvestigationsId()");

        mockEmployeeService.updateEmployee(isA(Employee.class));
        expectLastCall()
                .andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO));
        replay(mockEmployeeService);

        Employee updatedEmployee = new Employee(sFirstTestEmployee.getEmployeeId(),
                "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"));

        updatedEmployee.setParticipatedInvestigations(Arrays.asList(new Investigation(INVALID_ID, 2, "Some title",
                "Some interesting description.", OffsetDateTime.parse("2010-03-15T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                OffsetDateTime.parse("2015-01-01T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME))));

        String serializedEmployee = CustomObjectMapper.objectMapper().writeValueAsString(updatedEmployee);

        mockMvc.perform(
                put("/api/" + VERSION + "/employees/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployee)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO + "\""));
    }

    @Test
    public void failureUpdateEmployeeTest_WithNullInvestigationsId() throws Exception {

        LOGGER.debug("failureUpdateEmployeeTest_WithNullInvestigationsId()");

        mockEmployeeService.updateEmployee(isA(Employee.class));
        expectLastCall()
                .andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL));
        replay(mockEmployeeService);

        Employee updatedEmployee = new Employee(sFirstTestEmployee.getEmployeeId(),
                "Artur C. Clark", LocalDate.parse("1989-05-15"), LocalDate.parse("1993-05-15"));

        updatedEmployee.setParticipatedInvestigations(Arrays.asList(new Investigation(2, "Some title",
                "Some interesting description.", OffsetDateTime.parse("2010-03-15T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                OffsetDateTime.parse("2015-01-01T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME))));

        String serializedEmployee = CustomObjectMapper.objectMapper().writeValueAsString(updatedEmployee);

        mockMvc.perform(
                put("/api/" + VERSION + "/employees/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedEmployee)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL + "\""));
    }

    @Test
    public void successfulUpdateInvestigationsInEmployeeTest() throws Exception {

        LOGGER.debug("successfulUpdateInvestigationsInEmployeeTest()");

        List<Integer> investigationsId = Arrays.asList(EXISTS_INVESTIGATION_ID);

        expect(mockEmployeeService.updateEmployeeInvestigations(sFirstTestEmployee.getEmployeeId(), investigationsId))
                .andReturn(true);
        replay(mockEmployeeService);

        String serializedInvestigationsId = CustomObjectMapper.objectMapper().writeValueAsString(investigationsId);

        mockMvc.perform(
                put("/api/" + VERSION + "/employees/" + sFirstTestEmployee.getEmployeeId() + "/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedInvestigationsId)

        ).andDo(print())
                .andExpect(status().isAccepted());
    }

    @Test
    public void failureUpdateInvestigationsInEmployeeTest_WithNullEmployeeId() throws Exception {

        LOGGER.debug("failureUpdateInvestigationsInEmployeeTest_WithNullEmployeeId(");

        List<Integer> investigationsId = Arrays.asList(EXISTS_INVESTIGATION_ID);

        replay(mockEmployeeService);

        String serializedInvestigationsId = CustomObjectMapper.objectMapper().writeValueAsString(investigationsId);

        /*
        * Spring's conversion service can't convert string "null"
        * into primitive types like 'int', and will throw
        * IllegalArgumentException.
        *
        * */

        mockMvc.perform(
                put("/api/" + VERSION + "/employees/null/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedInvestigationsId)

        ).andDo(print())
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void failureUpdateInvestigationsInEmployeeTest_WithInvalidEmployeeId() throws Exception {

        LOGGER.debug("failureUpdateInvestigationsInEmployeeTest_WithInvalidEmployeeId()");

        List<Integer> investigationsId = Arrays.asList(EXISTS_INVESTIGATION_ID);

        expect(mockEmployeeService.updateEmployeeInvestigations(INVALID_ID, investigationsId))
                .andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO));
        replay(mockEmployeeService);

        String serializedInvestigationsId = CustomObjectMapper.objectMapper().writeValueAsString(investigationsId);

        mockMvc.perform(
                put("/api/" + VERSION + "/employees/" + INVALID_ID + "/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedInvestigationsId)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO + "\""));
    }

    @Test
    public void failureUpdateInvestigationsInEmployeeTest_WithNotExistsEmployeeId() throws Exception {

        LOGGER.debug("failureUpdateInvestigationsInEmployeeTest_WithNotExistsEmployeeId()");

        List<Integer> investigationsId = Arrays.asList(EXISTS_INVESTIGATION_ID);

        expect(mockEmployeeService.updateEmployeeInvestigations(NOT_EXISTS_ID, investigationsId))
                .andThrow(new IllegalArgumentException(MessageError.EMPLOYEE_NOT_EXISTS));
        replay(mockEmployeeService);

        String serializedInvestigationsId = CustomObjectMapper.objectMapper().writeValueAsString(investigationsId);

        mockMvc.perform(
                put("/api/" + VERSION + "/employees/" + NOT_EXISTS_ID + "/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedInvestigationsId)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.EMPLOYEE_NOT_EXISTS + "\""));
    }

    @Test
    public void failureUpdateInvestigationsInEmployeeTest_WithNullInvestigations() throws Exception {

        LOGGER.debug("failureUpdateInvestigationsInEmployeeTest_WithNullInvestigations()");

        replay(mockEmployeeService);

        mockMvc.perform(
                put("/api/" + VERSION + "/employees/" + sFirstTestEmployee.getEmployeeId() + "/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content("null")

        ).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void failureUpdateInvestigationsInEmployeeTest_WithNullInvestigationsId() throws Exception {

        LOGGER.debug("failureUpdateInvestigationsInEmployeeTest_WithNullInvestigationsId()");

        List<Integer> investigationsId = Arrays.asList(EXISTS_INVESTIGATION_ID, null);

        expect(mockEmployeeService.updateEmployeeInvestigations(sFirstTestEmployee.getEmployeeId(), investigationsId))
                .andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL));
        replay(mockEmployeeService);

        String serializedInvestigationsId = CustomObjectMapper.objectMapper().writeValueAsString(investigationsId);

        mockMvc.perform(
                put("/api/" + VERSION + "/employees/" + sFirstTestEmployee.getEmployeeId() + "/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedInvestigationsId)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.InvalidIncomingParameters.INVESTIGATION_ID_CAN_NOT_BE_NULL + "\""));
    }

    @Test
    public void failureUpdateInvestigationsInEmployeeTest_WithNotExistsInvestigationsId() throws Exception {

        LOGGER.debug("failureUpdateInvestigationsInEmployeeTest_WithNotExistsInvestigationsId()");

        List<Integer> investigationsId = Arrays.asList(NOT_EXISTS_ID);

        expect(mockEmployeeService.updateEmployeeInvestigations(sFirstTestEmployee.getEmployeeId(), investigationsId))
                .andThrow(new IllegalArgumentException(MessageError.INVESTIGATION_NOT_EXISTS));
        replay(mockEmployeeService);

        String serializedInvestigationsId = CustomObjectMapper.objectMapper().writeValueAsString(investigationsId);

        mockMvc.perform(
                put("/api/" + VERSION + "/employees/" + sFirstTestEmployee.getEmployeeId() + "/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedInvestigationsId)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.INVESTIGATION_NOT_EXISTS + "\""));
    }

    @Test
    public void failureUpdateInvestigationsInEmployeeTest_WithNotInvalidInvestigationsId() throws Exception {

        LOGGER.debug("failureUpdateInvestigationsInEmployeeTest_WithNotInvalidInvestigationsId()");

        List<Integer> investigationsId = Arrays.asList(INVALID_ID);

        expect(mockEmployeeService.updateEmployeeInvestigations(sFirstTestEmployee.getEmployeeId(), investigationsId))
                .andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO));
        replay(mockEmployeeService);

        String serializedInvestigationsId = CustomObjectMapper.objectMapper().writeValueAsString(investigationsId);

        mockMvc.perform(
                put("/api/" + VERSION + "/employees/" + sFirstTestEmployee.getEmployeeId() + "/investigations")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(serializedInvestigationsId)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.InvalidIncomingParameters.INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO + "\""));
    }

    @Test
    public void successfulDeleteEmployeeByIdTest() throws Exception {

        LOGGER.debug("successfulDeleteEmployeeByIdTest");

        expect(mockEmployeeService.deleteEmployeeById(sFirstTestEmployee.getEmployeeId()))
                .andReturn(true);
        replay(mockEmployeeService);

        mockMvc.perform(
                delete("/api/" + VERSION + "/employees/" + sFirstTestEmployee.getEmployeeId())
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)

        ).andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void failureDeleteEmployeeByIdTest_WithNotExistsEmployee() throws Exception {

        LOGGER.debug("failureDeleteEmployeeByIdTest_WithNotExistsEmployee()");

        expect(mockEmployeeService.deleteEmployeeById(NOT_EXISTS_ID))
                .andThrow(new IllegalArgumentException(MessageError.EMPLOYEE_NOT_EXISTS));
        replay(mockEmployeeService);

        mockMvc.perform(
                delete("/api/" + VERSION + "/employees/" + NOT_EXISTS_ID)
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.EMPLOYEE_NOT_EXISTS + "\""));
    }

    @Test
    public void failureDeleteEmployeeByIdTest_WithNullId() throws Exception {

        LOGGER.debug("failureDeleteEmployeeByIdTest_WithNullId()");

        replay(mockEmployeeService);

        /*
        * Spring's conversion service can't convert string "null"
        * into primitive types like 'int', and will throw
        * IllegalArgumentException.
        *
        * */

        mockMvc.perform(
                delete("/api/" + VERSION + "/employees/null")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)

        ).andDo(print())
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void failureDeleteEmployeeByIdTest_WithInvalidId() throws Exception {

        LOGGER.debug("failureDeleteEmployeeByIdTest_WithInvalidId()");

        expect(mockEmployeeService.deleteEmployeeById(INVALID_ID))
                .andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO));
        replay(mockEmployeeService);

        mockMvc.perform(
                delete("/api/" + VERSION + "/employees/" + INVALID_ID)
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.InvalidIncomingParameters.EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO + "\""));
    }

    @Test
    public void successfulGetEmployeesRatingsTest() throws Exception {

        LOGGER.debug("successfulGetEmployeesRatingsTest");

        List<Pair<Integer, Integer>> employeesRatings = Arrays.asList(new Pair<>(sFirstTestEmployee.getEmployeeId(), 66));

        expect(mockEmployeeService.getEmployeesRating(anyInt(), anyInt()))
                .andReturn(employeesRatings);
        replay(mockEmployeeService);

        String serializedRatings = CustomObjectMapper.objectMapper().writeValueAsString(employeesRatings);

        mockMvc.perform(
                get("/api/" + VERSION + "/employees/rating")
                        .param(OFFSET, "1")
                        .param(LIMIT, "1")

        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json(serializedRatings));
    }

    @Test
    public void successfulGetEmployeesRatingsTest_WithoutLimitAndOffset() throws Exception {

        LOGGER.debug("successfulGetEmployeesRatingsTest_WithoutLimitAndOffset");

        List<Pair<Integer, Integer>> employeesRatings = Arrays.asList(new Pair<>(sFirstTestEmployee.getEmployeeId(), 66));

        expect(mockEmployeeService.getEmployeesRating(Integer.valueOf(DEFAULT_OFFSET), Integer.valueOf(DEFAULT_LIMIT)))
                .andReturn(employeesRatings);
        replay(mockEmployeeService);

        String serializedRatings = CustomObjectMapper.objectMapper().writeValueAsString(employeesRatings);

        mockMvc.perform(
                get("/api/" + VERSION + "/employees/rating")

        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json(serializedRatings));
    }

    @Test
    public void failureGetEmployeesRatingsTest_WithInvalidOffset() throws Exception {

        LOGGER.debug("failureGetEmployeesRatingsTest_WithInvalidOffset()");

        expect(mockEmployeeService.getEmployeesRating(anyInt(), anyInt()))
                .andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.OFFSET_CAN_NOT_BE_LOWER_THAN_ZERO));
        replay(mockEmployeeService);

        mockMvc.perform(
                get("/api/" + VERSION + "/employees/rating")
                        .param(OFFSET, "-5")
                        .param(LIMIT, "1")

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.InvalidIncomingParameters.OFFSET_CAN_NOT_BE_LOWER_THAN_ZERO + "\""));
    }

    @Test
    public void failureGetEmployeesRatingsTest_WithInvalidLimit() throws Exception {

        LOGGER.debug("failureGetEmployeesRatingsTest_WithInvalidLimit()");

        expect(mockEmployeeService.getEmployeesRating(anyInt(), anyInt()))
                .andThrow(new IllegalArgumentException(MessageError.InvalidIncomingParameters.LIMIT_CAN_NOT_BE_LOWER_THAN_ZERO));
        replay(mockEmployeeService);

        mockMvc.perform(
                get("/api/" + VERSION + "/employees/rating")
                        .param(OFFSET, "1")
                        .param(LIMIT, "-5")

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"Error message: IllegalArgumentException - " +
                        MessageError.InvalidIncomingParameters.LIMIT_CAN_NOT_BE_LOWER_THAN_ZERO + "\""));
    }

}
