package com.epam.training.service;

import com.epam.training.dao.UserDao;
import com.epam.training.model.User;
import com.epam.training.util.MessageError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:service-test-mock.xml"})
//@ComponentScan(basePackages = "com.epam.training")
public class UserServiceImplMockTest {

    private static final Logger LOGGER = LogManager.getLogger();

    @Rule
    public ExpectedException thrownException = ExpectedException.none();

//    @Resource(name = "userDao")
    @Autowired
    private UserDao mMockUserDao;

    @Autowired
    private NamedParameterJdbcTemplate mMockNamedJDBCTemplate;

//    @Resource
    @Autowired
    private UserService mUserService;

    @Value("${sql.getCountUsersWithSameUserId}")
    private final String GET_COUNT_USER_WITH_SAME_ID = null;

    @Value("${sql.getCountUsersWithSameLogin}")
    private final String GET_COUNT_USER_WITH_SAME_LOGIN = null;

    private static final User EXPECTED_USER;

    private static final Integer NOT_EXISTS_USER_ID;
    private static final String NOT_EXISTS_USER_LOGIN;

    private static final String NOT_MATCHES_USER_LOGIN;
    private static final String NOT_MATCHES_USER_PASSWORD;


    static {
        NOT_EXISTS_USER_ID = 5;
        NOT_EXISTS_USER_LOGIN = "CodeMonkey";
        NOT_MATCHES_USER_LOGIN = "*LittleKitty*";
        NOT_MATCHES_USER_PASSWORD = "someNotMatchesPassword";

        EXPECTED_USER = new User("someLogin", "8some)Password*");
    }

    @After
    public void clean() throws Exception {

        LOGGER.debug("clean()");

        verify(mMockUserDao);
        reset(mMockUserDao);

        verify(mMockNamedJDBCTemplate);
        reset(mMockNamedJDBCTemplate);
    }

    @Test
    public void getAllUsersTest() throws Exception {

        LOGGER.debug("getAllUsersTest()");

        List<User> allUser = new ArrayList<>(2);

        expect(mMockUserDao.getAllUsers()).andReturn(allUser);
        replay(mMockUserDao);

        replay(mMockNamedJDBCTemplate);

        mUserService.getAllUsers();
    }

    @Test
    public void successfulGetUserByIdTest() throws Exception {

        LOGGER.debug("successfulGetUserByIdTest()");

        expect(mMockNamedJDBCTemplate.
                queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(1);
        replay(mMockNamedJDBCTemplate);

        expect(mMockUserDao.getUserById(1)).andReturn(EXPECTED_USER);
        replay(mMockUserDao);

        mUserService.getUserById(1);

    }

    @Test
    public void failureGetUserByIdTest_WithNullId() throws Exception {

        LOGGER.debug("failureGetUserByIdTest_WithNullId()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.ID_CAN_NOT_BE_NULL);

        replay(mMockNamedJDBCTemplate);
        replay(mMockUserDao);

        mUserService.getUserById(null);
    }

    @Test
    public void failureGetUserByIdTest_WithIdLowerThanZero() throws Exception {

        LOGGER.debug("failureGetUserByIdTest_WithIdLowerThanZero()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.ID_SHOULD_BE_GREATER_THAN_ZERO);

        replay(mMockNamedJDBCTemplate);
        replay(mMockUserDao);

        mUserService.getUserById(-1);
    }

    @Test
    public void failureGetUserByIdTest_WithNotExistsId() throws Exception {

        LOGGER.debug("failureGetUserByIdTest_WithNotExistsId()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.USER_IS_NOT_EXIST);

        Class<String> clazzString = String.class;

        expect(mMockNamedJDBCTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(0);

        replay(mMockNamedJDBCTemplate);
        replay(mMockUserDao);

        mUserService.getUserById(NOT_EXISTS_USER_ID);
    }

    @Test
    public void successfulGetUserByLoginTest() throws Exception {

        LOGGER.debug("successfulGetUserByLoginTest()");

        expect(mMockNamedJDBCTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(1);
        replay(mMockNamedJDBCTemplate);

        expect(mMockUserDao.getUserByLogin(EXPECTED_USER.getLogin())).andReturn(EXPECTED_USER);
        replay(mMockUserDao);

        mUserService.getUserByLogin(EXPECTED_USER.getLogin());
    }

    @Test
    public void failureGetUserByLoginTest_WithNullLogin() throws Exception {

        LOGGER.debug("failureGetUserByLoginTest_WithNullLogin()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.LOGIN_CAN_NOT_BE_NULL);

        replay(mMockNamedJDBCTemplate);
        replay(mMockUserDao);

        mUserService.getUserByLogin(null);
    }

    @Test
    public void failureGetUserByLoginTest_WithNotMatchedLogin() throws Exception {

        LOGGER.debug("failureGetUserByLoginTest_WithNotMatchedLogin()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.LOGIN_SHOULD_MATCH_PATTERN);

        replay(mMockNamedJDBCTemplate);
        replay(mMockUserDao);

        mUserService.getUserByLogin(NOT_MATCHES_USER_LOGIN);
    }

    @Test
    public void failureGetUserByLoginTest_WithNotExistsLogin() throws Exception {

        LOGGER.debug("failureGetUserByLoginTest_WithNotExistsLogin()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.USER_IS_NOT_EXIST);

        expect(mMockNamedJDBCTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(0);

        replay(mMockNamedJDBCTemplate);
        replay(mMockUserDao);

        mUserService.getUserByLogin(NOT_EXISTS_USER_LOGIN);
    }

    @Test
    public void successfulAddUserTest() throws Exception {

        LOGGER.debug("successfulAddUserTest()");

        expect(mMockNamedJDBCTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(0);
        replay(mMockNamedJDBCTemplate);

        expect(mMockUserDao.addUser(EXPECTED_USER)).andReturn(NOT_EXISTS_USER_ID);
        replay(mMockUserDao);

        Integer addedUserId = mUserService.addUser(EXPECTED_USER);
        assertTrue(addedUserId == NOT_EXISTS_USER_ID);

//        String formatString = "Test format string with float (%f) and string ('%s') and integer (%d).";
//        LOGGER.debug(String.format(formatString, 45.6, "Hello, Word!", 5));
    }

    @Test
    public void failureAddUserTest_WithNullLogin() throws Exception {

        LOGGER.debug("failureAddUserTest_WithNotLogin()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.LOGIN_CAN_NOT_BE_NULL);

        replay(mMockNamedJDBCTemplate);
        replay(mMockUserDao);

        mUserService.addUser(new User(null, NOT_MATCHES_USER_PASSWORD));
    }

    @Test
    public void failureAddUserTest_WithNullPassword() throws Exception {

        LOGGER.debug("failureAddUserTest_WithNullPassword()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.PASSWORD_CAN_NOT_BE_NULL);

        replay(mMockNamedJDBCTemplate);
        replay(mMockUserDao);

        mUserService.addUser(new User(EXPECTED_USER.getLogin(), null));
    }


    @Test
    public void failureAddUserTest_WithNotMatchedLogin() throws Exception {

        LOGGER.debug("failureAddUserTest_WithNotMatchedLogin()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.LOGIN_SHOULD_MATCH_PATTERN);

        replay(mMockNamedJDBCTemplate);
        replay(mMockUserDao);

        mUserService.addUser(new User(NOT_MATCHES_USER_LOGIN, EXPECTED_USER.getPassword()));
    }

    @Test
    public void failureAddUserTest_WithNotMatchedPassword() throws Exception {

        LOGGER.debug("failureAddUserTest_WithNotMatchedPassword()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.PASSWORD_SHOULD_MATCH_PATTERN);

        replay(mMockNamedJDBCTemplate);
        replay(mMockUserDao);

        mUserService.addUser(new User(EXPECTED_USER.getLogin(), NOT_MATCHES_USER_PASSWORD));
    }

    @Test
    public void failureAddUserTest_WithNullUser() throws Exception {

        LOGGER.debug("failureAddUserTest_WithNullUser()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.USER_CAN_NOT_BE_NULL);

        replay(mMockNamedJDBCTemplate);
        replay(mMockUserDao);

        mUserService.addUser(null);
    }

    @Test
    public void failureAddUserTest_WithAlreadyExistsUser() throws Exception {

        LOGGER.debug("failureAddUserTest_WithAlreadyExistsUser()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.ADDED_USER_ALREADY_EXISTS);

        expect(mMockNamedJDBCTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(1);
        replay(mMockNamedJDBCTemplate);

        replay(mMockUserDao);

        mUserService.addUser(EXPECTED_USER);
    }

    @Test
    public void successfulUpdateUserTest() throws Exception {

        LOGGER.debug("successfulUpdateUserTest()");

        User updateUser = new User(1, EXPECTED_USER.getLogin(), EXPECTED_USER.getPassword());

        expect(mMockNamedJDBCTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(1);
        replay(mMockNamedJDBCTemplate);

        expect(mMockUserDao.updateUser(updateUser)).andReturn(1);
        replay(mMockUserDao);

        boolean isUserUpdate = mUserService.updateUser(updateUser);
        assertTrue(isUserUpdate);
    }

    @Test
    public void failureUpdateUserTest_WithNullUser() throws Exception {

        LOGGER.debug("failureUpdateUserTest()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.USER_CAN_NOT_BE_NULL);

        replay(mMockNamedJDBCTemplate);
        replay(mMockUserDao);

        mUserService.updateUser(null);
    }

    @Test
    public void failureUpdateUserTest_WithNullLogin() throws Exception {

        LOGGER.debug("failureUpdateUserTest_WithNotLogin()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.LOGIN_CAN_NOT_BE_NULL);

        replay(mMockNamedJDBCTemplate);
        replay(mMockUserDao);

        mUserService.updateUser(new User(1, null, NOT_MATCHES_USER_PASSWORD));
    }

    @Test
    public void failureUpdateUserTest_WithNullPassword() throws Exception {

        LOGGER.debug("failureAddUserTest_WithNullPassword()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.PASSWORD_CAN_NOT_BE_NULL);

        replay(mMockNamedJDBCTemplate);
        replay(mMockUserDao);

        mUserService.updateUser(new User(1, EXPECTED_USER.getLogin(), null));
    }


    @Test
    public void failureUpdateUserTest_WithNotMatchedLogin() throws Exception {

        LOGGER.debug("failureUpdateUserTest_WithNotMatchedLogin()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.LOGIN_SHOULD_MATCH_PATTERN);

        replay(mMockNamedJDBCTemplate);
        replay(mMockUserDao);

        mUserService.updateUser(new User(1, NOT_MATCHES_USER_LOGIN, EXPECTED_USER.getPassword()));
    }

    @Test
    public void failureUpdateUserTest_WithNotMatchedPassword() throws Exception {

        LOGGER.debug("failureUpdateUserTest_WithNotMatchedPassword()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.PASSWORD_SHOULD_MATCH_PATTERN);

        replay(mMockNamedJDBCTemplate);
        replay(mMockUserDao);

        mUserService.updateUser(new User(1, EXPECTED_USER.getLogin(), NOT_MATCHES_USER_PASSWORD));
    }

    @Test
    public void successfulDeleteUserByIdTest() throws Exception {

        LOGGER.debug("successfulDeleteUserByIdTest()");

        expect(mMockNamedJDBCTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(1);
        replay(mMockNamedJDBCTemplate);

        expect(mMockUserDao.deleteUserById(1)).andReturn(1);
        replay(mMockUserDao);

        boolean isUserDelete = mUserService.deleteUserById(1);
        Assert.isTrue(isUserDelete);
    }

    @Test
    public void failureDeleteUserByIdTest_WithNullUserId() throws Exception {

        LOGGER.debug("failureDeleteUserByIdTest_WithNullUserId()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.ID_CAN_NOT_BE_NULL);

        replay(mMockNamedJDBCTemplate);
        replay(mMockUserDao);

        mUserService.deleteUserById(null);
    }

    @Test
    public void failureDeleteUserByIdTest_WithNotExistsUserId() throws Exception {

        LOGGER.debug("failureDeleteUserByIdTest_WithNotExistsUserId()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.USER_IS_NOT_EXIST);

        expect(mMockNamedJDBCTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(0);
        replay(mMockNamedJDBCTemplate);

        replay(mMockUserDao);

        mUserService.deleteUserById(NOT_EXISTS_USER_ID);
    }

    @Test
    public void successfulDeleteUserByLoginTest() throws Exception {

        LOGGER.debug("successfulDeleteUserByLoginTest()");

        expect(mMockNamedJDBCTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(1);
        replay(mMockNamedJDBCTemplate);

        expect(mMockUserDao.deleteUserByLogin(EXPECTED_USER.getLogin())).andReturn(1);
        replay(mMockUserDao);

        boolean isUserDelete = mUserService.deleteUserByLogin(EXPECTED_USER.getLogin());
        Assert.isTrue(isUserDelete);
    }

    @Test
    public void failureDeleteUserByLoginTest_WithNullUserLogin() throws Exception {

        LOGGER.debug("failureDeleteUserByLoginTest_WithNullUserLogin()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.LOGIN_CAN_NOT_BE_NULL);

        replay(mMockNamedJDBCTemplate);
        replay(mMockUserDao);

        mUserService.deleteUserByLogin(null);
    }

    @Test
    public void failureDeleteUserByIdTest_WithNotExistsUserLogin() throws Exception {

        LOGGER.debug("failureDeleteUserByIdTest_WithNotExistsUserLogin()");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.USER_IS_NOT_EXIST);

        expect(mMockNamedJDBCTemplate.queryForObject(isA(String.class), isA(SqlParameterSource.class), isA(Class.class))).andReturn(0);
        replay(mMockNamedJDBCTemplate);

        replay(mMockUserDao);

        mUserService.deleteUserByLogin(NOT_EXISTS_USER_LOGIN);
    }

}