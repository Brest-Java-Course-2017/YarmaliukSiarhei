package com.epam.training.rest;


import com.epam.training.model.User;
import com.epam.training.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.Resource;

import java.util.Arrays;

import static org.easymock.EasyMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:test-spring-rest-mock.xml"})
public class UserRestControllerMockTest {

    private static final Logger LOGGER = LogManager.getLogger();

    @Resource
    private UserRestController mUserRestController;

    private MockMvc mockMvc;

    @Autowired
    private UserService mMockUserService;


    private static final String ERROR_MESSAGE;
    private static final User USER;
    private static final User UPDATED_USER;
    private static final String RETURNED_JSON;

    static {

        ERROR_MESSAGE = "some error message";
        USER = new User("CodeMonkey", "someCo0l_Password");
        UPDATED_USER = new User(2, "newLogin", "newPassword", "someDescription");
        RETURNED_JSON = "{userId:-1,login:CodeMonkey,password:someCo0l_Password,description:null}";
    }

    @Before
    public void setUp() {
        mockMvc = standaloneSetup(mUserRestController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .setControllerAdvice(new RestErrorHandler())
//                .alwaysExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .setSingleView(View)
                .build();
    }

    @After
    public void tearDown() {

        verify(mMockUserService);
        reset(mMockUserService);
    }


    /*
    * Besides status method we can you view() and model() method.
    * view().name(somePage.jsp) - assert somePage view
    *
    * */

    @Test
    public void successfulGetUsersTest() throws Exception {

        LOGGER.debug("successfulGetUsersTest()");

        expect(mMockUserService.getAllUsers()).andReturn(Arrays.asList(USER));
        replay(mMockUserService);

        /*
        * For expression `jsonString` inside content().json("jsonString")
        * separators are being next symbols:
        *  1. ':' - between key and value;
        *  2. ',' - between key-value pairs.
        * Key-value pair is represented like a one JSONObject.
        * Symbol '\"' is omits.
        *
        * */
        String returnedJson = "[{userId:" + USER.getUserId() + ",login:" + USER.getLogin()
                + ",password:" + USER.getPassword() + ",description:" + USER.getDescription() + "}]";

        mockMvc.perform(
                get("/users")
//                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json(returnedJson, true))
                .andExpect(status().isOk());

    }

    @Test
    public void successfulAddUserTest() throws Exception {

        LOGGER.debug("successfulAddUserTest()");

        expect(mMockUserService.addUser(anyObject(User.class))).andReturn(3);
        replay(mMockUserService);

        String userSerialized2String = new ObjectMapper().writeValueAsString(USER);

        mockMvc.perform(
                post("/user")
                        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(userSerialized2String)
        ).andDo(print())
                .andDo(log())
                .andExpect(status().isCreated())
                .andExpect(content().string("3"));
    }

    @Test
    public void failureAddUserTest_WithWrongRequestBody() throws Exception {

        LOGGER.debug("failureAddUserTest_WithWrongRequestBody()");

        replay(mMockUserService);

        mockMvc.perform(
                post("/user")
                        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content("Hello, Word!")
        ).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void failureAddUserTest_WithWrongContentType() throws Exception {

        LOGGER.debug("failureAddUserTest_WithWrongContentType()");

        replay(mMockUserService);

        String userSerialized2String = new ObjectMapper().writeValueAsString(USER);

        mockMvc.perform(
                post("/user")
                        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(userSerialized2String)
        ).andDo(print())
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void successfulUpdateUserTest() throws Exception {

        LOGGER.debug("successfulUpdateUserTest()");

        expect(mMockUserService.updateUser(anyObject(User.class))).andReturn(true);
        replay(mMockUserService);

        mockMvc.perform(
                put("/user/" + UPDATED_USER.getUserId() +
                        "/" + UPDATED_USER.getLogin() +
                        "/" + UPDATED_USER.getPassword() +
                        "/" + UPDATED_USER.getDescription())
        ).andDo(print())
                .andExpect(status().isAccepted());
    }

    @Test
    public void successfulUpdateUserTest_WithNullDescription() throws Exception {

        LOGGER.debug("successfulUpdateUserTest_WithNullDescription()");

        expect(mMockUserService.updateUser(anyObject(User.class))).andReturn(true);
        replay(mMockUserService);

        mockMvc.perform(
                put("/user/" + UPDATED_USER.getUserId() +
                        "/" + UPDATED_USER.getLogin() +
                        "/" + UPDATED_USER.getPassword() +
                        "/null")
        ).andDo(print())
                .andExpect(status().isAccepted());
    }

    @Test
    public void failureUpdateUserTest_WithIncorrectUserFields() throws Exception {

        LOGGER.debug("failureUpdateUserTest_WithIncorrectUserFields()");

        expect(mMockUserService.updateUser(anyObject(User.class))).andThrow(new IllegalArgumentException(ERROR_MESSAGE));
        replay(mMockUserService);

        /*
        * org.skyscreamer.jsonassert.JSONParser is used
        * inside a ContentResultMatchers class.
        * When content().json() are being invoked, inside
        * invokes sonHelper.assertJsonEqual() method which
        * uses a JSONParser class for parsing a incoming json object.
        *
        * JSONParser has a method parseJSON(String) which takes
        * a json string and returns either a JSONObject, JSONString or JSONArray.
        * If json string starts with symbol:
        * "\"" - returns a JSONString object;
        * "{"  - returns a JSONObject object;
        * "["  - returns a JSONArray object.
        *
        * */

        mockMvc.perform(
                put("/user/2/null/null/null")
        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: " + ERROR_MESSAGE + "\""));

    }

    @Test
    public void failureUpdateUserTest_WithSpringInnerError() throws Exception {

        LOGGER.debug("failureUpdateUserTest_WithSpringInnerError()");

        expect(mMockUserService.updateUser(anyObject(User.class))).andThrow(new DataAccessException(ERROR_MESSAGE) {
        });
        replay(mMockUserService);

        mockMvc.perform(
                put("/user/2/updatedLogin/updatedPassword/null")
        ).andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().json("\"DataAccessException: " + ERROR_MESSAGE + "\""));

    }

    @Test
    public void successfulGetUserByLoginTest() throws Exception {

        LOGGER.debug("successfulGetUserByLoginTest()");

        expect(mMockUserService.getUserByLogin(anyObject(String.class))).andReturn(USER);
        replay(mMockUserService);

        mockMvc.perform(
                get("/user/login/CodeMonkey")
                        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)

        ).andDo(print())
                .andExpect(status().isFound())
                .andExpect(content().json(RETURNED_JSON));
    }

    @Test
    public void failureGetUserByLoginTest_WithIncorrectLogin() throws Exception {

        LOGGER.debug("successfulGetUserByLoginTest_WithEmpty()");

        expect(mMockUserService.getUserByLogin(anyObject(String.class))).andThrow(new IllegalArgumentException());
        replay(mMockUserService);

        mockMvc.perform(
                get("/user/login/null")
                        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)

        ).andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("\"IllegalArgumentException: null\""));
    }

    @Test
    public void successfulGetUserByIdTest() throws Exception {

        LOGGER.debug("successfulGetUserByIdTest()");

        expect(mMockUserService.getUserById(anyObject(Integer.class))).andReturn(USER);
        replay(mMockUserService);

        mockMvc.perform(
                get("/user/id/1")
                        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)

        ).andDo(print())
                .andExpect(status().isFound())
                .andExpect(content().json(RETURNED_JSON));
    }

//    @Test
//    public void testMethod() throws Exception {
//
//        replay(mMockUserService);
//
//        mockMvc.perform(get("/test")
//                .param("offset", "5"))
//                .andDo(print());
//    }

}
