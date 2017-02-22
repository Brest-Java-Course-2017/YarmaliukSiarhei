package com.epam.trainning.dao;

import com.epam.trainning.dao.UserDAO;
import com.epam.trainning.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDAOImpl implements UserDAO {

    private JdbcTemplate mJdbcTemplate;
    private NamedParameterJdbcTemplate mNamedParameterJdbcTemplate;

    public UserDAOImpl(DataSource dataSource) {
        mJdbcTemplate = new JdbcTemplate(dataSource);
        mNamedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<User> getAllUsers() {

        String getAllUsersSql = "select user_id, login, password, description from app_user";
        return mJdbcTemplate.query(getAllUsersSql, new UserRowMapper());
    }

    @Override
    public User getUserById(Integer userId) {
        return null;
    }

    @Override
    public Integer addUser(User user) {
        return null;
    }

    @Override
    public void updateUser(User user) {

    }

    @Override
    public void deleteUserById(Integer userId) {

    }

    private class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet resultSet, int i) throws SQLException {

            User user = new User(
                    resultSet.getInt("user_id"),
                    resultSet.getString("login"),
                    resultSet.getString("password"),
                    resultSet.getString("description"));

            return user;
        }
    }
}
