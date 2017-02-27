package com.epam.trainning.dao;

import com.epam.trainning.model.User;

import java.util.List;

public interface UserDao {

    public List<User> getAllUsers();

    public User getUserById(Integer userId);

    public User getUserByLogin(String login);

    public Integer addUser(User user);

    public boolean updateUser(User user);

    public boolean deleteUserById(Integer userId);

    public boolean deleteUserByLogin(String login);

}
