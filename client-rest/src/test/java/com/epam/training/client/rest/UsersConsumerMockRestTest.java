package com.epam.training.client.rest;


import com.epam.training.client.rest.api.UsersConsumer;
import com.epam.training.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:client-test-mock.xml"})
public class UsersConsumerMockRestTest {

    private Logger LOGGER = LogManager.getLogger();

    @Autowired
    private UsersConsumer mUsersConsumer;

    @Autowired
    private RestTemplate mMockRestTemplate;


    @Value("${user.protocol}://${user.host}:${user.port}")
    private final String HOST_URL = null;

    @Value("${point.user}")
    private final String URL_USER = null;

    @Value("${point.users}")
    private final String URL_USERS = null;


    private static final User FIRST_TEST_USER;
    private static final User SECOND_TEST_USER;

    static {
        FIRST_TEST_USER = new User(1, "someLogin", "somePassword*7");
        SECOND_TEST_USER = new User(2, "anotherLogin", "anotherPassword6&");
    }

    @After
    public void tearDown() {

        verify(mMockRestTemplate);
        reset(mMockRestTemplate);
    }

    @Test
    public void successfulGetAllUsersTest() throws Exception {

        LOGGER.debug("successfulGetAllUsersTest()");

        List<User> expectedUsers = Arrays.asList(FIRST_TEST_USER, SECOND_TEST_USER);

        expect(mMockRestTemplate.getForEntity(HOST_URL + "/" + URL_USERS, List.class)).andReturn(new ResponseEntity<List>(expectedUsers, HttpStatus.OK));
        replay(mMockRestTemplate);

        List<User> returnedUsers = mUsersConsumer.getAllUsers();
        assertEquals(expectedUsers, returnedUsers);
    }

    @Test
    public void successfulGetUserByIdTest() throws Exception {

        LOGGER.debug("successfulGetUserByIdTest()");

        Integer expectedId = 1;

        expect(mMockRestTemplate.getForEntity(HOST_URL + "/" + URL_USER + "/id/{userId}", User.class, expectedId))
                .andReturn(new ResponseEntity<User>(FIRST_TEST_USER, HttpStatus.FOUND));
        replay(mMockRestTemplate);

        User returnedUser = mUsersConsumer.getUserById(expectedId);
        assertEquals(FIRST_TEST_USER, returnedUser);

    }

    @Test
    public void successfulGetUserByLoginTest() throws Exception {

        LOGGER.debug("successfulGetUserByLoginTest()");

        String expectedLogin = "someExpectedLogin";

        expect(mMockRestTemplate.getForEntity(HOST_URL + "/" + URL_USER + "/login/{login}", User.class, expectedLogin))
                .andReturn(new ResponseEntity<User>(FIRST_TEST_USER, HttpStatus.FOUND));
        replay(mMockRestTemplate);

        User returnedUser = mUsersConsumer.getUserByLogin(expectedLogin);
        assertEquals(FIRST_TEST_USER, returnedUser);
    }

    @Test
    public void successfulUpdateUserTest() throws Exception {

        LOGGER.debug("successfulUpdateUserTest()");

        mMockRestTemplate.put(HOST_URL + "/" + URL_USER + "/{id}/{login}/{password}/{description}", null,
                FIRST_TEST_USER.getUserId(), FIRST_TEST_USER.getLogin(), FIRST_TEST_USER.getPassword(), FIRST_TEST_USER.getDescription());

        expectLastCall();
        replay(mMockRestTemplate);

        mUsersConsumer.updateUser(FIRST_TEST_USER);
    }

    @Test
    public void successfulAddUserTest() throws Exception {

        LOGGER.debug("successfulAddUserTest()");

        expect(mMockRestTemplate.postForEntity(HOST_URL + "/" + URL_USER, FIRST_TEST_USER, Integer.class))
                .andReturn(new ResponseEntity<Integer>(FIRST_TEST_USER.getUserId(), HttpStatus.CREATED));
        replay(mMockRestTemplate);

        Integer returnedUserId = mUsersConsumer.addUser(FIRST_TEST_USER);
        assertEquals(FIRST_TEST_USER.getUserId(), returnedUserId);
    }

    @Test
    public void successfulDeleteUserByIdTest() throws Exception {

        LOGGER.debug("successfulDeleteUserByIdTest()");

        mMockRestTemplate.delete(isA(URI.class));
        expectLastCall();
        replay(mMockRestTemplate);

        mUsersConsumer.deleteUserById(FIRST_TEST_USER.getUserId());
    }

    @Test
    public void successfulDeleteUserByLoginTest() throws Exception {

        LOGGER.debug("successfulDeleteUserByLoginTest()");

        mMockRestTemplate.delete(isA(URI.class));
        expectLastCall();
        replay(mMockRestTemplate);

        mUsersConsumer.deleteUserByLogin(FIRST_TEST_USER.getLogin());
    }

}
