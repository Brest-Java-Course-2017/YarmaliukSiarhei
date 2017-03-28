package com.epam.training.dao;

import com.epam.training.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:test-spring-dao.xml"})
@Transactional
public class UserDaoImplTest {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private UserDao mUserDao;

    private static final int COUNT_ALL_USERS = 2;

    private static final User sFirstTestUser;
    private static final User sSecondTestUser;

    static {
        sFirstTestUser = new User(1, "testUserLogin1", "userPassword1@", "first test user");
        sSecondTestUser = new User(2, "testUserLogin2", "userPassword2@");
    }

    private static Integer NOT_EXISTS_USER_ID = 3;

    @Rule
    public ExpectedException thrownException = ExpectedException.none();

    @Test
    public void getAllUsersTest() {

        LOGGER.debug("getAllUsersTest()");

        List<User> allUsers = mUserDao.getAllUsers();

        LOGGER.debug("getAllUsersTest() - count of returned users: {}", allUsers.size());

        for (User user : allUsers) {
            assertNotNull("ERROR: user is null.", user);
        }
        assertTrue(allUsers.size() == COUNT_ALL_USERS);
    }

    @Test
    public void successfulGetUserByIdTest() {

        LOGGER.debug("successfulGetUserByIdTest()");

        Integer[] arrayId = new Integer[]{sFirstTestUser.getUserId(),
                sSecondTestUser.getUserId()};

        for (Integer id : arrayId) {

            LOGGER.debug("successfulGetUserByIdTest() - invoked getUserById( {} )", id);

            User user = mUserDao.getUserById(id);
            assertNotNull(user);
            assertEquals(id, user.getUserId());
        }
    }

    @Test
    public void failureGetUserByIdTest_WithNullId() {

        LOGGER.debug("failureGetUserByIdTest_WithNullId()");
        thrownException.expect(EmptyResultDataAccessException.class);

        LOGGER.debug("failureGetUserByIdTest_WithNullId() - expected throw EmptyResultDataAccessException");
        mUserDao.getUserById(null);
    }

    @Test
    public void failureGetUserByIdTest_WithNotExistsId() {

        LOGGER.debug("failureGetUserByIdTest_WithNotExistsId()");
        thrownException.expect(EmptyResultDataAccessException.class);

        LOGGER.debug("failureGetUserByIdTest_WithNotExistsId() - expected throw EmptyResultDataAccessException");
        mUserDao.getUserById(NOT_EXISTS_USER_ID);
    }

    @Test
    public void successfulGetUserByLoginTest() {

        LOGGER.debug("successfulGetUserByLoginTest()");

        LOGGER.debug("successfulGetUserByLoginTest() - getUserByLogin( {} )", sFirstTestUser.getLogin());
        User firstExpectedUser = mUserDao.getUserByLogin(sFirstTestUser.getLogin());
        assertEquals(sFirstTestUser, firstExpectedUser);

        LOGGER.debug("successfulGetUserByLoginTest() - getUserByLogin( {} )", sSecondTestUser.getLogin());
        User secondExpectedUser = mUserDao.getUserByLogin(sSecondTestUser.getLogin());
        assertEquals(sSecondTestUser, secondExpectedUser);
    }

    @Test
    public void failureGetUserByLoginTest_WithNullLogin() {

        LOGGER.debug("failureGetUserByLoginTest_WithNullLogin()");

        LOGGER.debug("failureGetUserByLoginTest_WithNullLogin() - expected EmptyResultDataAccessException");
        thrownException.expect(EmptyResultDataAccessException.class);
        mUserDao.getUserByLogin(null);
    }

    @Test
    public void failureGetUserByLoginTest_WithEmptyLogin() {

        LOGGER.debug("failureGetUserByLoginTest_WithEmptyLogin()");

        LOGGER.debug("failureGetUserByLoginTest_WithEmptyLogin() - expected EmptyResultDataAccessException");
        thrownException.expect(EmptyResultDataAccessException.class);
        mUserDao.getUserByLogin("");
    }

    @Test
    public void failureGetUserByLoginTest_WithNotExistsLogin() {

        LOGGER.debug("failureGetUserByLoginTest_WithNotExistsLogin()");

        LOGGER.debug("failureGetUserByLoginTest_WithNotExistsLogin() - expected EmptyResultDataAccessException");
        thrownException.expect(EmptyResultDataAccessException.class);
        mUserDao.getUserByLogin(sFirstTestUser.getLogin() + "some additions like bla-bla-bla");
    }

    //    @Rollback(false)
    @Test
    public void successfulAddUserTest() {

        LOGGER.debug("successfulAddUserTest()");

        User newUser = (new User("code.monkey", sFirstTestUser.getPassword()));
        Integer newId = mUserDao.addUser(newUser);

        LOGGER.debug("successfulAddUserTest() - check returned user's id");
        assertNotNull(newId);
        newUser.setUserId(newId);

        LOGGER.debug("successfulAddUserTest() - get added User's id");
        User addedUser = mUserDao.getUserById(newId);
        assertEquals(newUser, addedUser);
    }

    @Test
    public void successfulAddUserTest_WithExistsUser() {

        LOGGER.debug("successfulAddUserTest_WithExistsUser()");

        LOGGER.debug("successfulAddUserTest_WithExistsUser() - added exists User");

//        In our case DAO module must work without any checks.
//        It just works with db and doesn't know about any limitation of logic.
        mUserDao.addUser(new User(sFirstTestUser.getLogin(), sFirstTestUser.getPassword()));
    }

    @Test
    public void failureAddUserTest_WithNullUser() {

        LOGGER.debug("failureAddUserTest_WithNullUser()");

        LOGGER.debug("failureAddUserTest_WithNullUser() - expected IllegalArgumentException");

//        BeanPropertySqlParameterSource generate IllegalArgumentException
        thrownException.expect(IllegalArgumentException.class);
        mUserDao.addUser(null);
    }

    @Test
    public void successfulUpdateUserTest() {

        LOGGER.debug("successfulUpdateUserTest()");

        User updatedUser = new User(
                sFirstTestUser.getUserId(), "updatedLogin",
                "777@CoolTvShow", "Stranger Things");

        assertTrue(mUserDao.updateUser(updatedUser) > 0);
        assertEquals(updatedUser, mUserDao.getUserById(updatedUser.getUserId()));
    }

    @Test
    public void successfulUpdateUserTest_WithNotExistsUser() {

        LOGGER.debug("successfulUpdateUserTest_WithNotExistsUser()");

        User updatedUser = new User(
                NOT_EXISTS_USER_ID, "updatedLogin",
                "777@CoolTvShow", "Stranger Things");

        int updatedRowCount = mUserDao.updateUser(updatedUser);

        LOGGER.debug("successfulUpdateUserTest_WithNotExistsUser() - updated row count {}", updatedRowCount);
        assertTrue(updatedRowCount == 0);
    }

    @Test
    public void failureUpdateUserTest_WithNullUser() {

        LOGGER.debug("failureUpdateUserTest_WithNullUser()");

        LOGGER.debug("failureUpdateUserTest_WithNullUser() - expected IllegalArgumentException");

//        BeanPropertySqlParameterSource generate IllegalArgumentException
        thrownException.expect(IllegalArgumentException.class);
        mUserDao.updateUser(null);
    }

    @Test
    public void successfulDeleteUserByIdTest() {

        LOGGER.debug("successfulDeleteUserByIdTest()");

        int deletedRowCount = mUserDao.deleteUserById(sFirstTestUser.getUserId());

        LOGGER.debug("successfulDeleteUserByIdTest() - deleted row count {}", deletedRowCount);
        assertTrue(deletedRowCount == 1);

        assertTrue(mUserDao.getAllUsers().size() == COUNT_ALL_USERS - 1);
    }

    @Test
    public void successfulDeleteUserByIdTest_WithNullId() {

        LOGGER.debug("successfulDeleteUserByIdTest_WithNullId()");

        int deletedRowCount = mUserDao.deleteUserById(null);

        LOGGER.debug("successfulDeleteUserByIdTest_WithNullId() - deleted row count {}", deletedRowCount);
        assertTrue(deletedRowCount == 0);
    }

    @Test
    public void successfulDeleteUserByIdTest_WithNotExistsId() {

        LOGGER.debug("successfulDeleteUserByIdTest_WithNotExistsId()");

        int deletedRowCount = mUserDao.deleteUserById(NOT_EXISTS_USER_ID);

        LOGGER.debug("successfulDeleteUserByIdTest_WithNotExistId() - deleted row count {}", deletedRowCount);
        assertTrue(deletedRowCount == 0);
    }

    @Test
    public void successfulDeleteUserByLoginTest() {

        LOGGER.debug("successfulDeleteUserByLoginTest()");

        assertTrue(mUserDao.deleteUserByLogin(sFirstTestUser.getLogin()) == 1);
        assertTrue(mUserDao.getAllUsers().size() == COUNT_ALL_USERS - 1);
    }

//    TODO future: for catching multiple throw exception in one method - need use something else like TestNG

    @Test
    public void successfulDeleteUserByLoginTest_WithNullLogin() {

        LOGGER.debug("successfulDeleteUserByLoginTest_WithNullLogin()");

        int deletedRowCount = mUserDao.deleteUserByLogin(null);

        LOGGER.debug("successfulDeleteUserByLoginTest_WithNullLogin() - deleted row count {}", deletedRowCount);
        assertTrue(deletedRowCount == 0);
    }

    @Test
    public void successfulDeleteUserByLoginTest_WithNotExistsLogin() {

        LOGGER.debug("successfulDeleteUserByLoginTest_WithNotExistsLogin()");

        final String notExistLogin = sFirstTestUser.getLogin() + "bla-bla";
        int deletedRowCount = mUserDao.deleteUserByLogin(notExistLogin);

        LOGGER.debug("successfulDeleteUserByLoginTest_WithNotExistsLogin() - deleted row count {}", deletedRowCount);
        assertTrue(deletedRowCount == 0);
    }

    @Test
    public void successfulDeleteUserByLoginTest_WithEmptyLogin() {

        LOGGER.debug("successfulDeleteUserByLoginTest_WithEmptyLogin()");

        int deletedRowCount = mUserDao.deleteUserByLogin("");
        LOGGER.debug("successfulDeleteUserByLoginTest_WithEmptyLogin() - deleted row count {}", deletedRowCount);
        assertTrue(deletedRowCount == 0);
    }

}