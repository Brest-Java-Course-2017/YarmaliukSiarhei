package com.epam.training.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class UserTest {

    private static final Integer TEST_USER_ID = new Integer(1);
    private static final String TEST_LOGIN = "test_user";
    private static final String TEST_PASSWORD = "hEllo@12345";
    private static final String TEST_DESCRIPTION = "This is a test description.";

    @Rule
    public ExpectedException thrownException = ExpectedException.none();

    private User testUser = null;

    {
        testUser = new User(TEST_USER_ID, TEST_LOGIN, TEST_PASSWORD, TEST_DESCRIPTION);
    }

    @Test
    public void successfulSetUserIdTest() {

        testUser.setUserId(0);
        assertEquals("User id = ", Integer.valueOf(0), testUser.getUserId());
    }


    @Test
    public void failureSetUserIdTest1() {

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage("Incoming parameter is invalid. User Id can't be a null.");
        testUser.setUserId(null);
    }

    @Test
    public void failureSetUserIdTest2() {

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage("Incoming parameter is invalid. User Id can't be lower then 0.");
        testUser.setUserId(-1);
    }

//    setlogin, setPassword, setDescriptor methods make like setUserId method

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