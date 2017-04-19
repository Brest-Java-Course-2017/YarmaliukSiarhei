package com.epam.training.client.rest.api;


import com.epam.training.client.exception.ServerDataAccessException;
import com.epam.training.model.User;

import java.util.List;

public interface UsersConsumer {

    public List<User> getAllUsers() throws ServerDataAccessException;

    public User getUserById(Integer userId) throws ServerDataAccessException;

    public User getUserByLogin(String login) throws ServerDataAccessException;

    public Integer addUser(User user) throws ServerDataAccessException;

    public void updateUser(User user) throws ServerDataAccessException;

    public void deleteUserById(Integer userId) throws ServerDataAccessException;

    public void deleteUserByLogin(String login) throws ServerDataAccessException;

}
