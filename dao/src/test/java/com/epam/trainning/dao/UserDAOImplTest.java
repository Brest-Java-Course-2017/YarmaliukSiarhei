package com.epam.trainning.dao;

import com.epam.trainning.model.User;
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
public class UserDAOImplTest {

    @Autowired
    private UserDAO mUserDAO;

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

        List<User> allUsers = mUserDAO.getAllUsers();
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
            User user = mUserDAO.getUserById(id);
            assertNotNull(user);
            assertEquals("Input and output User's object ids should be same.", id, user.getUserId());
        }

    }

    @Test
    public void failureGetUserByIdTest() {

        thrownException.expect(EmptyResultDataAccessException.class);
        mUserDAO.getUserById(NOT_EXISTS_USER_ID);

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage("Invalid incoming parameter. Id can't be a null.");
        mUserDAO.getUserById(null);

    }

//    @Test(expected = DataAccessException.class)
//    public void getNotExistsUserByIdTest() {
//        mUserDAO.getUserById(3);
//    }

    @Test
    public void successfulGetUserByLoginTest() {

        User firstExpectedUser = mUserDAO.getUserByLogin(firstTestUser.getLogin());
        assertEquals(firstTestUser, firstExpectedUser);

        User secondExpectedUser = mUserDAO.getUserByLogin(secondTestUser.getLogin());
        assertEquals(secondTestUser, secondExpectedUser);
    }

    @Test
    public void failureGetUserByLoginTest() {

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage("Invalid incoming parameter. Login can't be a null.");
        mUserDAO.getUserByLogin(null);

        thrownException.expect(EmptyResultDataAccessException.class);
        mUserDAO.getUserByLogin("");
        mUserDAO.getUserByLogin(firstTestUser.getLogin() + "some additions like bla-bla-bla");

    }

    //    @Rollback(false)
    @Test
    public void successfulAddUserTest() {

        User newUser = (new User("code.monkey", firstTestUser.getPassword()));
        Integer newId = mUserDAO.addUser(newUser);
        assertNotNull(newId);
        newUser.setUserId(newId);

        User addedUser = mUserDAO.getUserById(newId);
        assertEquals(newUser, addedUser);
    }

    @Test
    public void failureAddUserTest() {

        thrownException.expect(DataAccessException.class);
        thrownException.expectMessage("Can't insert a new user. User with the same login already exists.");
        mUserDAO.addUser(new User(firstTestUser.getLogin(), firstTestUser.getPassword()));
        mUserDAO.addUser(new User(secondTestUser.getLogin(), firstTestUser.getPassword()));

        thrownException.expectMessage("Invalid incoming parameter. User can't be a null.");
        mUserDAO.addUser(null);
    }

    @Test
    public void successfulUpdateUserTest() {

        User updatedUser = new User(
                firstTestUser.getUserId(), "updatedLogin",
                "777@CoolTvShow", "Stranger Things");

        assertTrue(mUserDAO.updateUser(updatedUser));
        assertEquals(updatedUser, mUserDAO.getUserById(updatedUser.getUserId()));
    }

    @Test
    public void failureUpdateUserTest() {

        User updatedUser = new User(
                NOT_EXISTS_USER_ID, "updatedLogin",
                "777@CoolTvShow", "Stranger Things");

        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage("Invalid incoming parameter. User can't be a null.");
        mUserDAO.updateUser(null);

        thrownException.expectMessage("Invalid incoming parameter. User is not exist.");
        updatedUser.setUserId(NOT_EXISTS_USER_ID);
        mUserDAO.updateUser(updatedUser);
    }

    @Test
    public void successfulDeleteUserByIdTest() {

        assertTrue(mUserDAO.deleteUserById(firstTestUser.getUserId()));
        assertTrue(mUserDAO.getAllUsers().size() == COUNT_ALL_USERS - 1);
    }

    @Test
    public void failureDeleteUserByIdTest() {

        thrownException.expectMessage("Invalid incoming parameter. User id can't be a null.");
        thrownException.expect(IllegalArgumentException.class);
        mUserDAO.deleteUserById(null);

        thrownException.expectMessage("Invalid incoming parameter. User with userId = " + NOT_EXISTS_USER_ID + " is not exist.");
        mUserDAO.deleteUserById(NOT_EXISTS_USER_ID);
    }

    @Test
    public void successfulDeleteUserByLoginTest() {

        assertTrue(mUserDAO.deleteUserByLogin(firstTestUser.getLogin()));
        assertTrue(mUserDAO.getAllUsers().size() == COUNT_ALL_USERS - 1);
    }

    @Test
    public void failureDeleteUserByLoginTest() {

        thrownException.expectMessage("Invalid incoming parameter. Login can't be a null.");
        thrownException.expect(IllegalArgumentException.class);
        mUserDAO.deleteUserByLogin(null);

        final String notExistLogin = "firstTestUser.getLogin()" + "bla-bla";

        thrownException.expectMessage("Invalid incoming parameter. User with userId = " + notExistLogin + " is not exist.");
        mUserDAO.deleteUserByLogin(notExistLogin);

        thrownException.expectMessage("Invalid incoming parameter. User with userId = " + "" + " is not exist.");
        mUserDAO.deleteUserByLogin("");
    }

}