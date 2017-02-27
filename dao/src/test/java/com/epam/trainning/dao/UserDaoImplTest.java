package com.epam.trainning.dao;

import com.epam.trainning.model.User;
import com.epam.trainning.util.MessageError;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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

    @Autowired
    private UserDao mUserDao;

    private static final int COUNT_ALL_USERS = 2;

    private static final User firstTestUser;
    private static final User secondTestUser;

    static {
        firstTestUser = new User(1, "testUserLogin1", "userPassword1@", "first test user");
        secondTestUser = new User(2, "testUserLogin2", "userPassword2@");
    }

    private static Integer NOT_EXISTS_USER_ID = 3;

    @Rule
    public ExpectedException thrownException = ExpectedException.none();

    @Test
    public void getAllUsersTest() {

        List<User> allUsers = mUserDao.getAllUsers();
        for (User user : allUsers) {
            assertNotNull("ERROR: user is null.", user);
        }
        assertTrue(allUsers.size() == COUNT_ALL_USERS);
    }

    @Test
    public void successfulGetUserByIdTest() {

        Integer[] arrayId = new Integer[]{firstTestUser.getUserId(),
                secondTestUser.getUserId()};

        for (Integer id : arrayId) {
            User user = mUserDao.getUserById(id);
            assertNotNull(user);
            assertEquals(id, user.getUserId());
        }
    }

    @Test
    public void failureGetUserByIdTest1() {

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.ID_CAN_NOT_BE_A_NULL);
        mUserDao.getUserById(null);
    }

    @Test
    public void failureGetUserByIdTest2() {

        thrownException.expect(EmptyResultDataAccessException.class);
        mUserDao.getUserById(NOT_EXISTS_USER_ID);
    }

//    @Test(expected = DataAccessException.class)
//    public void getNotExistsUserByIdTest() {
//        mUserDao.getUserById(3);
//    }

    @Test
    public void successfulGetUserByLoginTest() {

        User firstExpectedUser = mUserDao.getUserByLogin(firstTestUser.getLogin());
        assertEquals(firstTestUser, firstExpectedUser);

        User secondExpectedUser = mUserDao.getUserByLogin(secondTestUser.getLogin());
        assertEquals(secondTestUser, secondExpectedUser);
    }

    @Test
    public void failureGetUserByLoginTest1() {

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.LOGIN_CAN_NOT_BE_A_NULL);
        mUserDao.getUserByLogin(null);
    }

    @Test
    public void failureGetUserByLoginTest2() {

        thrownException.expect(EmptyResultDataAccessException.class);
        mUserDao.getUserByLogin("");
    }

    @Test
    public void failureGetUserByLoginTest3() {

        thrownException.expect(EmptyResultDataAccessException.class);
        mUserDao.getUserByLogin(firstTestUser.getLogin() + "some additions like bla-bla-bla");
    }

    //    @Rollback(false)
    @Test
    public void successfulAddUserTest() {

        User newUser = (new User("code.monkey", firstTestUser.getPassword()));
        Integer newId = mUserDao.addUser(newUser);
        assertNotNull(newId);
        newUser.setUserId(newId);

        User addedUser = mUserDao.getUserById(newId);
        assertEquals(newUser, addedUser);
    }

    @Test
    public void failureAddUserTest1() {

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.USER_CAN_NOT_BE_A_NULL);
        mUserDao.addUser(null);
    }

    @Test
    public void failureAddUserTest2() {

        thrownException.expect(DataAccessException.class);
        thrownException.expectMessage(MessageError.ADDED_USER_ALREADY_EXISTS);
        mUserDao.addUser(new User(firstTestUser.getLogin(), firstTestUser.getPassword()));
    }

    @Test
    public void successfulUpdateUserTest() {

        User updatedUser = new User(
                firstTestUser.getUserId(), "updatedLogin",
                "777@CoolTvShow", "Stranger Things");

        assertTrue(mUserDao.updateUser(updatedUser));
        assertEquals(updatedUser, mUserDao.getUserById(updatedUser.getUserId()));
    }

    @Test
    public void failureUpdateUserTest1() {

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(MessageError.InvalidIncomingParameters.USER_CAN_NOT_BE_A_NULL);
        mUserDao.updateUser(null);
    }

    @Test
    public void failureUpdateUserTest2() {

        User updatedUser = new User(
                NOT_EXISTS_USER_ID, "updatedLogin",
                "777@CoolTvShow", "Stranger Things");

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.USER_IS_NOT_EXIST);
        updatedUser.setUserId(NOT_EXISTS_USER_ID);
        mUserDao.updateUser(updatedUser);
    }

    @Test
    public void successfulDeleteUserByIdTest() {

        assertTrue(mUserDao.deleteUserById(firstTestUser.getUserId()));
        assertTrue(mUserDao.getAllUsers().size() == COUNT_ALL_USERS - 1);
    }

    @Test
    public void failureDeleteUserByIdTest1() {

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.ID_CAN_NOT_BE_A_NULL);
        thrownException.expect(IllegalArgumentException.class);
        mUserDao.deleteUserById(null);
    }

    @Test
    public void failureDeleteUserByIdTest2() {

        thrownException.expectMessage(
                MessageError.InvalidIncomingParameters.COMPOSITE_PREFIX_USER_WITH_ID +
                        NOT_EXISTS_USER_ID + MessageError.InvalidIncomingParameters.COMPOSITE_POSTFIX_IS_NOT_EXISTS);
        mUserDao.deleteUserById(NOT_EXISTS_USER_ID);
    }

    @Test
    public void successfulDeleteUserByLoginTest() {

        assertTrue(mUserDao.deleteUserByLogin(firstTestUser.getLogin()));
        assertTrue(mUserDao.getAllUsers().size() == COUNT_ALL_USERS - 1);
    }

//    TODO future: for catching multiple throw exception in one method - need use something else like TestNG

    @Test
    public void failureDeleteUserByLoginTest1() {

        thrownException.expectMessage(MessageError.InvalidIncomingParameters.LOGIN_CAN_NOT_BE_A_NULL);
        thrownException.expect(IllegalArgumentException.class);
        mUserDao.deleteUserByLogin(null);
    }

    @Test
    public void failureDeleteUserByLoginTest2() {

        final String notExistLogin = "firstTestUser.getLogin()" + "bla-bla";

        thrownException.expectMessage(
                MessageError.InvalidIncomingParameters.COMPOSITE_PREFIX_USER_WITH_LOGIN +
                        notExistLogin + MessageError.InvalidIncomingParameters.COMPOSITE_POSTFIX_IS_NOT_EXISTS);
        mUserDao.deleteUserByLogin(notExistLogin);
    }

    @Test
    public void failureDeleteUserByLoginTest3() {

        thrownException.expectMessage(
                MessageError.InvalidIncomingParameters.COMPOSITE_PREFIX_USER_WITH_LOGIN +
                        "" + MessageError.InvalidIncomingParameters.COMPOSITE_POSTFIX_IS_NOT_EXISTS);
        mUserDao.deleteUserByLogin("");
    }

}