package com.epam.training.service;

import com.epam.training.dao.UserDao;
import com.epam.training.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.springframework.util.Assert;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:service-test-mock.xml"})
public class UserServiceImplMockTest {


    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private UserDao mMockUserDao;

    @Autowired
    private UserService mUserService;

    public void setUserDao(UserDao mockUserDao) {
        this.mMockUserDao = mockUserDao;
    }

    @After
    public void clean() throws Exception {

        LOGGER.debug("clean()");

        verify(mMockUserDao);
        reset(mMockUserDao);
    }

    @Test
    public void getAllUsersTest() throws Exception {

    }

    @Test
    public void getUserByIdTest() throws Exception {

    }

    @Test
    public void getUserByLoginTest() throws Exception {

    }

    @Test
    public void addUserTest() throws Exception {

        LOGGER.debug("addUserTest()");

        User user = new User("new_Test_user", "tset");

        expect(mMockUserDao.addUser(user)).andReturn(5);
        replay(mMockUserDao);

        Integer id = mUserService.addUser(user);
        assertTrue(id == 5);
    }

    @Test
    public void updateUserTest() throws Exception {

    }

    @Test
    public void deleteUserByIdTest() throws Exception {

    }

    @Test
    public void deleteUserByLoginTest() throws Exception {

    }

}