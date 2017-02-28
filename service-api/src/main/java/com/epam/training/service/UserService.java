package com.epam.training.service;


import com.epam.training.model.User;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface UserService {

    public List<User> getAllUsers() throws DataAccessException;

    public User getUserById(Integer userId) throws DataAccessException;

    public User getUserByLogin(String login) throws DataAccessException;

    public Integer addUser(User user) throws DataAccessException;

    public boolean updateUser(User user) throws DataAccessException;

    public boolean deleteUserById(Integer userId) throws DataAccessException;

    public boolean deleteUserByLogin(String login) throws DataAccessException;
}
