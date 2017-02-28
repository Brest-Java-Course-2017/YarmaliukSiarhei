package com.epam.training.service;

import com.epam.training.dao.UserDao;
import com.epam.training.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LogManager.getLogger();
//    private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao mUserDao;

    public void setUserDao(UserDao userDao){
        this.mUserDao = userDao;
    }

    @Override
    public List<User> getAllUsers() throws DataAccessException {
        return null;
    }

    @Override
    public User getUserById(Integer userId) throws DataAccessException {
        return null;
    }

    @Override
    public User getUserByLogin(String login) throws DataAccessException {
        return null;
    }

    @Override
    public Integer addUser(User user) throws DataAccessException {

        mUserDao.addUser(user);
        return 5;
    }

    @Override
    public boolean updateUser(User user) throws DataAccessException {
        return false;
    }

    @Override
    public boolean deleteUserById(Integer userId) throws DataAccessException {
        return false;
    }

    @Override
    public boolean deleteUserByLogin(String login) throws DataAccessException {
        return false;
    }
}
