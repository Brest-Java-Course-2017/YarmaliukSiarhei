package com.epam.training.dao;

import com.epam.training.model.User;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface UserDao {

    public List<User> getAllUsers() throws DataAccessException;

    public User getUserById(Integer userId) throws DataAccessException;

    public User getUserByLogin(String login) throws DataAccessException;

    public Integer addUser(User user) throws DataAccessException;

    public int updateUser(User user) throws DataAccessException;

    public int deleteUserById(Integer userId) throws DataAccessException;

    public int deleteUserByLogin(String login) throws DataAccessException;
}
