package com.segniertomato.work.rest.controller;


import com.segniertomato.work.model.Investigation;
import com.segniertomato.work.rest.RestErrorHandler;
import com.segniertomato.work.service.InvestigationService;
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

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

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

    private static final String DEFAULT_LIMIT = "10";
    private static final String DEFAULT_OFFSET = "0";

    private static final String VERSION = "v1";

    private static final String OFFSET = "offset";
    private static final String LIMIT = "limit";

    private static final OffsetDateTime FIRST_TEST_DATE;
    private static final OffsetDateTime SECOND_TEST_DATE;

    private static final Investigation sFirstTestInvestigation;

    static {

        FIRST_TEST_DATE = OffsetDateTime.parse("2010-03-15T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        SECOND_TEST_DATE = OffsetDateTime.parse("2015-01-01T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        sFirstTestInvestigation = new Investigation(2, 2, "Some title",
                "Some interesting description.", FIRST_TEST_DATE, SECOND_TEST_DATE);
    }

    @Resource
    private InvestigationRestController investigationRestController;

    private MockMvc mockMvc;

    @Autowired
    private InvestigationService mockInvestigationService;

    @Before
    public void setUp() {
        mockMvc = standaloneSetup(investigationRestController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
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

        String expectedJsonResponse = "[{investigationId:" + sFirstTestInvestigation.getInvestigationId()
                + ",number:" + sFirstTestInvestigation.getNumber() + ",title:'" + sFirstTestInvestigation.getTitle()
                + "',description:'" + sFirstTestInvestigation.getDescription() + "'}]";

        expect(mockInvestigationService.getAllInvestigations(anyInt(), anyInt())).andReturn(Arrays.asList(sFirstTestInvestigation));
        replay(mockInvestigationService);

        mockMvc.perform(
                get("/api/" + VERSION + "/investigations")
                        .param(OFFSET, "1")
                        .param(LIMIT, "1")

        ).andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json(expectedJsonResponse, false))
                .andExpect(status().isOk());
    }

    @Test
    public void successfulGetInvestigationsTest_WithoutLimitAndOffset() throws Exception {

        LOGGER.debug("successfulGetInvestigationsTest_WithoutLimitAndOffset()");

        String expectedJsonResponse = "[{investigationId:" + sFirstTestInvestigation.getInvestigationId()
                + ",number:" + sFirstTestInvestigation.getNumber() + ",title:'" + sFirstTestInvestigation.getTitle()
                + "',description:'" + sFirstTestInvestigation.getDescription() + "'}]";

        expect(mockInvestigationService.getAllInvestigations(Integer.valueOf(DEFAULT_OFFSET), Integer.valueOf(DEFAULT_LIMIT)))
                .andReturn(Arrays.asList(sFirstTestInvestigation));
        replay(mockInvestigationService);

        mockMvc.perform(
                get("/api/" + VERSION + "/investigations")

        ).andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json(expectedJsonResponse, false))
                .andExpect(status().isOk());
    }



}
