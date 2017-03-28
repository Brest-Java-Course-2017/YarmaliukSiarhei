package com.epam.training.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {

    private static final Integer TEST_USER_ID = 1;
    private static final String TEST_LOGIN = "test_user";
    private static final String TEST_PASSWORD = "hEllo@12345";
    private static final String TEST_DESCRIPTION = "This is a test description.";

    private User testUser;

    {
        testUser = new User(TEST_USER_ID, TEST_LOGIN, TEST_PASSWORD, TEST_DESCRIPTION);
    }

    @Test
    public void successfulSetUserIdTest() {

        testUser.setUserId(0);
        assertEquals("User id = ", Integer.valueOf(0), testUser.getUserId());
    }

    @Test
    public void setLoginTest() throws Exception {

        assertEquals("Login = ", TEST_LOGIN, testUser.getLogin());
    }

    @Test
    public void setPasswordTest() throws Exception {

        assertEquals("Password = ", TEST_PASSWORD, testUser.getPassword());
    }

    @Test
    public void setDescriptionTest() throws Exception {

        assertEquals("Description = ", TEST_DESCRIPTION, testUser.getDescription());
    }

}