package com.epam.training.service;

import com.epam.training.model.User;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:service-test.xml"})
public class UserServiceImplTest {


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