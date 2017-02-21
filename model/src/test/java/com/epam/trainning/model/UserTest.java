package com.epam.trainning.model;

import com.epam.trainning.util.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class UserTest {

    private static final String ILLEGAL_ARGUMENT_EXCEPTION_PREFIX = "Incoming parameter is invalid. ";

    private static final Integer TEST_USER_ID = new Integer(1);
    private static final String TEST_LOGIN = "test_user";
    private static final String TEST_PASSWORD = "hEllo@12345";
    private static final String TEST_DESCRIPTION = "This is a test description.";

    private User testUser1 = null;

    {
        testUser1 = new User(TEST_USER_ID, TEST_LOGIN, TEST_PASSWORD, TEST_DESCRIPTION);
    }

//    @Test
//    public void userConstructor() {
//
//        String exceptionMessageIllegalUserId = "User id value is invalid.";
//        String exceptionMessageIllegalLogin = "Login value is invalid.";
//        String exceptionMessageIllegalPassword = "Password value is invalid.";
//
//        User[] userObjectWithInvalidUserId = {
//                new User(null, TEST_LOGIN, TEST_PASSWORD),
//                new User(-1, TEST_LOGIN, TEST_PASSWORD),
//                new User(0, TEST_LOGIN, TEST_PASSWORD),
//
//                new User(null, TEST_LOGIN, TEST_PASSWORD, TEST_DESCRIPTION),
//                new User(-1, TEST_LOGIN, TEST_PASSWORD, TEST_DESCRIPTION),
//                new User(0, TEST_LOGIN, TEST_PASSWORD, TEST_DESCRIPTION),
//        };
//
//        User[] userObjectWithInvalideLogin = {
//                new User(TEST_USER_ID, "123456@t", TEST_PASSWORD),
//                new User(TEST_USER_ID, "test", TEST_PASSWORD),
//                new User(TEST_USER_ID, "hello,test", TEST_PASSWORD),
//                new User(TEST_USER_ID, "I am program monkey", TEST_PASSWORD),
//                new User(TEST_USER_ID, "[program_monkey}", TEST_PASSWORD),
//                new User(TEST_USER_ID, "hello_I_am_program_monkey", TEST_PASSWORD),
//
//                new User(TEST_USER_ID, "123456@t", TEST_PASSWORD, TEST_DESCRIPTION),
//                new User(TEST_USER_ID, "test", TEST_PASSWORD, TEST_DESCRIPTION),
//                new User(TEST_USER_ID, "hello,test", TEST_PASSWORD, TEST_DESCRIPTION),
//                new User(TEST_USER_ID, "I am program monkey", TEST_PASSWORD, TEST_DESCRIPTION),
//                new User(TEST_USER_ID, "[program_monkey}", TEST_PASSWORD, TEST_DESCRIPTION),
//                new User(TEST_USER_ID, "hello_I_am_program_monkey", TEST_PASSWORD, TEST_DESCRIPTION),
//        };
//
//        User[] userObjectWithInvalidePassword = {
//                new User(TEST_USER_ID, TEST_LOGIN, "#12f5Ek"),
//                new User(TEST_USER_ID, TEST_LOGIN, "123456789"),
//                new User(TEST_USER_ID, TEST_LOGIN, "abcdefghft"),
//                new User(TEST_USER_ID, TEST_LOGIN, "ASDSDASDAS"),
//                new User(TEST_USER_ID, TEST_LOGIN, "tsedsfASDC"),
//                new User(TEST_USER_ID, TEST_LOGIN, "asda123fFA"),
//                new User(TEST_USER_ID, TEST_LOGIN, "@#  scsd "),
//
//                new User(TEST_USER_ID, TEST_LOGIN, "#12f5Ek", TEST_DESCRIPTION),
//                new User(TEST_USER_ID, TEST_LOGIN, "123456789", TEST_DESCRIPTION),
//                new User(TEST_USER_ID, TEST_LOGIN, "abcdefghft", TEST_DESCRIPTION),
//                new User(TEST_USER_ID, TEST_LOGIN, "ASDSDASDAS", TEST_DESCRIPTION),
//                new User(TEST_USER_ID, TEST_LOGIN, "tsedsfASDC", TEST_DESCRIPTION),
//                new User(TEST_USER_ID, TEST_LOGIN, "asda123fFA", TEST_DESCRIPTION),
//                new User(TEST_USER_ID, TEST_LOGIN, "@#  scsd ", TEST_DESCRIPTION),
//        };
//
//    }

    @Test
    public void setUserId() {

//        assertAll();

        String exceptionMessageWithNull = ILLEGAL_ARGUMENT_EXCEPTION_PREFIX + "User id can't be a null.";
        String exceptionMessageWithZeroValue = ILLEGAL_ARGUMENT_EXCEPTION_PREFIX + "User id can't be lower then 0.";

        List<Pair<Integer, String>> listTestValues = new ArrayList<>(3);

        listTestValues.add(new Pair<>(null, exceptionMessageWithNull));
        listTestValues.add(new Pair<>(-1, exceptionMessageWithZeroValue));
        listTestValues.add(new Pair<>(0, exceptionMessageWithZeroValue));

        User dummy = new User(1, TEST_LOGIN, TEST_PASSWORD);

        for (Pair<Integer, String> pair : listTestValues) {

            testIllegalArgumentException(pair.second, () -> {
                dummy.setUserId(pair.first);
            });
        }

        Assert.assertEquals("User id = ", TEST_USER_ID, testUser1.getUserId());
    }

    private void testIllegalArgumentException(final String exceptionMessage, Executable executable) {

        Throwable exception = assertThrows(IllegalArgumentException.class, executable);
        Assert.assertEquals(exceptionMessage, exceptionMessage);
    }

//    setlogin, setPassword, setDescriptor methods make like setUserId method

    @Test
    public void setLogin() throws Exception {

        Assert.assertEquals("Login = ", TEST_LOGIN, testUser1.getLogin());
    }

    @Test
    public void setPassword() throws Exception {

        Assert.assertEquals("Password = ", TEST_PASSWORD, testUser1.getPassword());
    }

    @Test
    public void setDescription() throws Exception {

        Assert.assertEquals("Description = ", TEST_DESCRIPTION, testUser1.getDescription());
    }

}