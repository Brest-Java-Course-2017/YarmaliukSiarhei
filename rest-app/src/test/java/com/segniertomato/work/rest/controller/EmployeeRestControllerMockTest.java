package com.segniertomato.work.rest.controller;


import com.segniertomato.work.service.EmployeeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.Resource;

import static org.junit.Assert.assertNotNull;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring-test-rest-mock.xml"})
public class EmployeeRestControllerMockTest {

    private static final Logger LOGGER = LogManager.getLogger();

    @Resource
    private EmployeeRestController employeeRestController;

    private MockMvc mockMvc;

    @Autowired
    private EmployeeService mockEmployeeService;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeRestController)
                .build();
    }

    @Test
    public void successfulGetAllEmployeesTest() throws Exception {

        LOGGER.debug("successfulGetAllEmployeesTest()");
        assertNotNull(mockEmployeeService);
    }


}
