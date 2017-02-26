package com.epam.trainning.model;

import com.epam.trainning.util.Pair;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class UserTest {

    private static final String ILLEGAL_ARGUMENT_EXCEPTION_PREFIX = "Incoming parameter is invalid. ";

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
    public void setUserIdTest() {

        String exceptionMessageWithNull = ILLEGAL_ARGUMENT_EXCEPTION_PREFIX + "User id can't be a null.";
        String exceptionMessageWithZeroValue = ILLEGAL_ARGUMENT_EXCEPTION_PREFIX + "User id can't be lower then 0.";

        List<Pair<Integer, String>> listTestValues = new ArrayList<>(3);

        listTestValues.add(new Pair<Integer, String>(null, exceptionMessageWithNull));
        listTestValues.add(new Pair<Integer, String>(-1, exceptionMessageWithZeroValue));
        listTestValues.add(new Pair<Integer, String>(0, exceptionMessageWithZeroValue));

        User dummy = new User(1, TEST_LOGIN, TEST_PASSWORD);

        thrownException.expect(IllegalArgumentException.class);

        for (Pair<Integer, String> pair : listTestValues) {

            thrownException.expectMessage(pair.second);
            dummy.setUserId(pair.first);
        }

        assertEquals("User id = ", TEST_USER_ID, testUser.getUserId());
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