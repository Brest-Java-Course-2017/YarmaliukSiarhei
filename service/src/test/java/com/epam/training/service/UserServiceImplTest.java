package com.epam.training.service;

import com.epam.training.model.User;
import com.epam.training.util.MessageError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:service-test.xml"})
@Transactional
public class UserServiceImplTest {

    private static final Logger LOGGER = LogManager.getLogger();

    @Rule
    public ExpectedException thrownException = ExpectedException.none();

    @Autowired
    private UserService mUserService;

    private static final int COUNT_ALL_USERS = 2;

    private static final Integer NOT_EXISTS_USER_ID;
    private static final String NOT_EXISTS_USER_LOGIN;
    private static final String NOT_MATCHES_USER_LOGIN;
    private static final String NOT_MATCHES_USER_PASSWORD;

    private static final User FIRST_USER;
    private static final User SECOND_USER;

    static {

        NOT_EXISTS_USER_ID = 5;
        NOT_EXISTS_USER_LOGIN = "CodeMonkey";
        NOT_MATCHES_USER_LOGIN = "*LittleKitty*";
        NOT_MATCHES_USER_PASSWORD = "someNotMatchesPassword";

        FIRST_USER = new User(1, "testUserLogin1", "userPassword1@", "first test user");
        SECOND_USER = new User(2, "testUserLogin2", "userPassword2@");
    }

    @Test
    public void getAllUsersTest() throws Exception {

        LOGGER.debug("getAllUsersTest()");

        List<User> allUsers = mUserService.getAllUsers();

        assertNotNull(allUsers);
        LOGGER.debug("getAllUsersTest() - count of returned users is: {}", allUsers.size());

        for (User user : allUsers) {
            assertNotNull(user);
        }
        assertTrue(allUsers.size() == COUNT_ALL_USERS);
    }

    @Test
    public void successfulGetUserByIdTest() throws Exception {

        LOGGER.debug("successfulGetUserByIdTest()");

        User firstReturnedUser = mUserService.getUserById(FIRST_USER.getUserId());
        User secondReturnedUser = mUserService.getUserById(SECOND_USER.getUserId());

        String notEqualErrorMessage = "The returned User doesn't equal with real User in DB.";

        assertNotNull(firstReturnedUser);
        assertEquals(notEqualErrorMessage, firstReturnedUser, FIRST_USER);

        assertNotNull(secondReturnedUser);
        assertEquals(notEqualErrorMessage, secondReturnedUser, SECOND_USER);

    }

    @Test
    public void failureGetUSerByIdTest_WithNotExistsId() throws Exception {

        LOGGER.debug("failureGetUserByIdTest_WithNotExistsId()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.USER_IS_NOT_EXIST);

        LOGGER.debug("failureGetUserByIdTest_WithNotExistsId() - expected throw IllegalArgumentException");
        mUserService.getUserById(NOT_EXISTS_USER_ID);
    }

    @Test
    public void failureGetUSerByIdTest_WithIdLowerThenZero() throws Exception {

        LOGGER.debug("failureGetUserByIdTest_WithIdLowerThenZero()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.ID_SHOULD_BE_GREATER_THAN_ZERO);

        LOGGER.debug("failureGetUserByIdTest_WithIdLowerThenZero() - expected throw IllegalArgumentException");
        mUserService.getUserById(0);
    }

    @Test
    public void failureGetUSerByIdTest_WithNullId() throws Exception {

        LOGGER.debug("failureGetUserByIdTest_WithNullId()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.ID_CAN_NOT_BE_NULL);

        LOGGER.debug("failureGetUserByIdTest_WithNullId() - expected throw IllegalArgumentException");
        mUserService.getUserById(null);
    }

    @Test
    public void successfulGetUserByLoginTest() throws Exception {

        LOGGER.debug("successfulGetUserByLoginTest()");

        User firstReturnedUser = mUserService.getUserByLogin(FIRST_USER.getLogin());
        User secondReturnedUser = mUserService.getUserByLogin(SECOND_USER.getLogin());

        String notEqualErrorMessage = "The returned User doesn't equal with real User in DB.";

        assertNotNull(firstReturnedUser);
        assertEquals(notEqualErrorMessage, firstReturnedUser, FIRST_USER);

        assertNotNull(secondReturnedUser);
        assertEquals(notEqualErrorMessage, secondReturnedUser, SECOND_USER);
    }

    @Test
    public void failureGetUserByLoginTest_WithNullLogin() throws Exception {

        LOGGER.debug("failureGetUserByLoginTest_WithNullLogin()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.LOGIN_CAN_NOT_BE_NULL);

        mUserService.getUserByLogin(null);
    }

    @Test
    public void failureGetUserByLoginTest_WithNotExistsLogin() throws Exception {

        LOGGER.debug("failureGetUserByLoginTest_WithNotExistsLogin()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.USER_IS_NOT_EXIST);

        mUserService.getUserByLogin(NOT_EXISTS_USER_LOGIN);
    }

    @Test
    public void failureGetUserByLoginTest_WithNotMatchedLogin() throws Exception {

        LOGGER.debug("failureGetUserByLoginTest_WithNotMatchedLogin()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.LOGIN_SHOULD_MATCH_PATTERN);

        mUserService.getUserByLogin(NOT_MATCHES_USER_LOGIN);
    }

    @Test
    public void successfulAddUserTest() throws Exception {

        LOGGER.debug("successfulAddUserTest()");

        mUserService.addUser(new User("newUserLogin", FIRST_USER.getPassword()));
    }

    @Test
    public void failureAddUserTest_WithNullLogin() throws Exception {

        LOGGER.debug("failureAddUserTest_WithNullLogin()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.LOGIN_CAN_NOT_BE_NULL);

        mUserService.addUser(new User(null, FIRST_USER.getPassword()));
    }

    @Test
    public void failureAddUserTest_WithNullPassword() throws Exception {

        LOGGER.debug("failureAddUserTest_WithNullPassword()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.PASSWORD_CAN_NOT_BE_NULL);

        mUserService.addUser(new User(FIRST_USER.getLogin(), null));
    }

    @Test
    public void failureAddUserTest_WithNotMatchedLogin() throws Exception {

        LOGGER.debug("failureAddUserTest_WithNotMatchedLogin()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.LOGIN_SHOULD_MATCH_PATTERN);

        mUserService.addUser(new User(NOT_MATCHES_USER_LOGIN, FIRST_USER.getPassword()));
    }

    @Test
    public void failureAddUserTest_WithNotMatchedPassword() throws Exception {

        LOGGER.debug("failureAddUserTest_WithNotMatchedPassword()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.PASSWORD_SHOULD_MATCH_PATTERN);

        mUserService.addUser(new User(FIRST_USER.getLogin(), NOT_MATCHES_USER_PASSWORD));
    }

    @Test
    public void failureAddUserTest_WithNullUser() throws Exception {

        LOGGER.debug("failureAddUserTest_WithNullUser()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.USER_CAN_NOT_BE_NULL);

        mUserService.addUser(null);
    }

    @Test
    public void failureAddUserTest_WithAlreadyExistsUser() throws Exception {

        LOGGER.debug("failureAddUserTest_WithAlreadyExistsUser()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.ADDED_USER_ALREADY_EXISTS);

        mUserService.addUser(new User(FIRST_USER.getLogin(), FIRST_USER.getPassword()));
    }

    @Test
    public void failureAddUserTest_WithExistsUser() throws Exception {

        LOGGER.debug("failureAddUserTest_WithAlreadyExistsUser()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.ID_SHOULD_BE_NULL_OR_NEGATIVE_ONE);

        mUserService.addUser(new User(NOT_EXISTS_USER_ID, FIRST_USER.getLogin(), FIRST_USER.getPassword()));
    }

    @Test
    public void successfulUpdateUserTest() throws Exception {

        LOGGER.debug("successfulUpdateUserTest()");

        User updateUser = new User(FIRST_USER.getUserId(), "newLogin", "newPassword&5");
        boolean isUpdateUser = mUserService.updateUser(updateUser);
        assertTrue(isUpdateUser);

        User returnedUpdatedUser = mUserService.getUserById(FIRST_USER.getUserId());
        assertEquals("The returned User doesn't equal with real updated User in DB.", returnedUpdatedUser, updateUser);
    }

    @Test
    public void failureUpdateUserTest_WithNullUser() throws Exception {

        LOGGER.debug("failureUpdateUserTest_WithNullUser()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.USER_CAN_NOT_BE_NULL);

        mUserService.updateUser(null);
    }

    @Test
    public void failureUpdateUserTest_WithNullId() throws Exception {

        LOGGER.debug("failureUpdateUserTest_WithNullId()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.ID_CAN_NOT_BE_NULL);

        User updateUser = new User(FIRST_USER.getLogin(), FIRST_USER.getPassword());
        updateUser.setUserId(null);

        mUserService.updateUser(updateUser);
    }

    @Test
    public void failureUpdateUserTest_WithNegativeId() throws Exception {

        LOGGER.debug("failureUpdateUserTest_WithNegativeId()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.ID_SHOULD_BE_GREATER_THAN_ZERO);

        mUserService.updateUser(new User(-1, FIRST_USER.getLogin(), FIRST_USER.getPassword()));
    }

    @Test
    public void failureUpdateUserTest_WithNullLogin() throws Exception {

        LOGGER.debug("failureUpdateUserTest_WithNullLogin()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.LOGIN_CAN_NOT_BE_NULL);

        mUserService.updateUser(new User(FIRST_USER.getUserId(), null, FIRST_USER.getPassword()));
    }

    @Test
    public void failureUpdateUserTest_WithNullPassword() throws Exception {

        LOGGER.debug("failureUpdateUserTest_WithNullPassword()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.PASSWORD_CAN_NOT_BE_NULL);

        mUserService.updateUser(new User(FIRST_USER.getUserId(), FIRST_USER.getLogin(), null));
    }

    @Test
    public void failureUpdateUserTest_WithNotMatchedLogin() throws Exception {

        LOGGER.debug("failureUpdateUserTest_WithNotMatchedLogin()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.LOGIN_SHOULD_MATCH_PATTERN);

        mUserService.updateUser(new User(FIRST_USER.getUserId(), NOT_MATCHES_USER_LOGIN, FIRST_USER.getPassword()));
    }

    @Test
    public void failureUpdateUserTest_WithNotMatchedPassword() throws Exception {

        LOGGER.debug("failureUpdateUserTest_WithNotMatchedPassword()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.PASSWORD_SHOULD_MATCH_PATTERN);

        mUserService.updateUser(new User(FIRST_USER.getUserId(), FIRST_USER.getLogin(), NOT_MATCHES_USER_PASSWORD));
    }

    @Test
    public void successfulDeleteUserByIdTest() throws Exception {

        LOGGER.debug("successfulDeleteUserByIdTest()");

        boolean isDeleteUser = mUserService.deleteUserById(FIRST_USER.getUserId());
        assertTrue(isDeleteUser);

        List<User> allUsers = mUserService.getAllUsers();
        assertTrue(allUsers.size() == COUNT_ALL_USERS - 1);
    }

    @Test
    public void failureDeleteUserByIdTest_WithNullId() throws Exception {

        LOGGER.debug("failureDeleteUserByIdTest_WithNullId()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.ID_CAN_NOT_BE_NULL);

        mUserService.deleteUserById(null);
    }

    @Test
    public void failureDeleteUserByIdTest_WithNotExistsId() throws Exception {

        LOGGER.debug("failureDeleteUserByIdTest_WithNotExistsId()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.USER_IS_NOT_EXIST);

        mUserService.deleteUserById(NOT_EXISTS_USER_ID);
    }

    @Test
    public void failureDeleteUserByIdTest_WithNegativeId() throws Exception {

        LOGGER.debug("failureDeleteUserByIdTest_WithNegativeId()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.ID_SHOULD_BE_GREATER_THAN_ZERO);

        mUserService.deleteUserById(-1);
    }

    @Test
    public void successfulDeleteUserByLoginTest() throws Exception {

        LOGGER.debug("successfulDeleteUserByLoginTest()");

        boolean isDeleteUser = mUserService.deleteUserByLogin(FIRST_USER.getLogin());
        assertTrue(isDeleteUser);

        List<User> allUsers = mUserService.getAllUsers();
        assertTrue(allUsers.size() == COUNT_ALL_USERS - 1);
    }

    @Test
    public void failureDeleteUserByIdTest_WithNullLogin() throws Exception {

        LOGGER.debug("failureDeleteUserByIdTest_WithNullLogin()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.LOGIN_CAN_NOT_BE_NULL);

        mUserService.deleteUserByLogin(null);
    }

    @Test
    public void failureDeleteUserByIdTest_WithNotExistsLogin() throws Exception {

        LOGGER.debug("failureDeleteUserByIdTest_WithNotExistsLogin()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.USER_IS_NOT_EXIST);

        mUserService.deleteUserByLogin(NOT_EXISTS_USER_LOGIN);
    }

    @Test
    public void failureDeleteUserByIdTest_WithNotMatchedLogin() throws Exception {

        LOGGER.debug("failureDeleteUserByIdTest_WithNotMatchedLogin()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.LOGIN_SHOULD_MATCH_PATTERN);

        mUserService.deleteUserByLogin(NOT_MATCHES_USER_LOGIN);
    }

}