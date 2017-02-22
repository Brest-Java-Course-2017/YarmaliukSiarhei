package com.epam.trainning.dao;

import com.epam.trainning.dao.UserDAO;
import com.epam.trainning.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:test-spring-dao.xml"})
public class UserDAOImplTest {

    @Autowired
    UserDAO mUserDAO;

    @Test
    public void getAllUsers() throws Exception {
        List<User> users = mUserDAO.getAllUsers();
        Assert.assertTrue(users.size() == 2);
    }

    @Test
    public void getUserById() throws Exception {

    }

    @Test
    public void addUser() throws Exception {

    }

    @Test
    public void updateUser() throws Exception {

    }

    @Test
    public void deleteUserById() throws Exception {

    }

}