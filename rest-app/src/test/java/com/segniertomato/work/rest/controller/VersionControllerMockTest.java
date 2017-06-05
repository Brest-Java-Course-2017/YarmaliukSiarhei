package com.segniertomato.work.rest.controller;


import com.segniertomato.work.rest.RestControllerUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.Resource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-test-rest-mock.xml"})
public class VersionControllerMockTest {


    @Resource
    private VersionController versionController;

    MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(versionController).build();
    }

    @Test
    public void GetVersionTest() throws Exception {

        mockMvc.perform(
                get("/api/version")
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("\"" + RestControllerUtils.VERSION + "\""));
    }
}
