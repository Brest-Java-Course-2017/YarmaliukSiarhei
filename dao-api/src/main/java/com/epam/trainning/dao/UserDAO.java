package com.epam.trainning.dao;

import com.epam.trainning.model.User;

import java.util.List;

public interface UserDAO {

    public List<User> getAllUsers();

    public User getUserById(Integer userId);

    public Integer addUser(User user);

    public void updateUser(User user);

    public void deleteUserById(Integer userId);

}
