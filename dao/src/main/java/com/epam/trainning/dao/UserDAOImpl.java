package com.epam.trainning.dao;

import com.epam.trainning.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

//@Repository
public class UserDAOImpl implements UserDAO {

    @Value("${sql.getAllUsers}")
    private final String GET_ALL_USERS_SQL = null;

    @Value("${sql.getUserById}")
    private final String GET_USER_BY_ID = null;

    @Value("${sql.getUserByLogin}")
    private final String GET_USER_BY_LOGIN = null;

    @Value("${sql.addUser}")
    private final String ADD_USER = null;

    @Value("${sql.updateUser}")
    private final String UPDATE_USER = null;

    @Value("${sql.deleteUserById}")
    private final String DELETE_USER_BY_ID = null;

    @Value("${sql.deleteUserByLogin}")
    private final String DELETE_USER_BY_LOGIN = null;

    @Value("${sql.getCountUserWithSameLogin}")
    private final String GET_COUNT_USER_WITH_SAME_LOGIN = null;

    @Value("${sql.getCountUserWithSameId}")
    private final String GET_COUNT_USER_WITH_SAME_ID = null;


    private static final String LOGIN = "login";
    private static final String USER_ID = "user_id";
    private static final String PASSWORD = "password";
    private static final String DESCRIPTION = "description";

    private static final String ERROR_MSG_USER_ALREADY_EXISTS = "Can't insert a new user. User with the same login already exists.";
    private static final String ILLEGAL_ARGUMENT_EXCEPTION_PREFIX_MSG = "Invalid incoming parameter.";

    private NamedParameterJdbcTemplate mNamedParameterJdbcTemplate;

    public UserDAOImpl(DataSource dataSource) {
        mNamedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<User> getAllUsers() {
        return mNamedParameterJdbcTemplate.getJdbcOperations().query(GET_ALL_USERS_SQL, new UserRowMapper());
    }

    @Override
    public User getUserById(Integer userId) throws IllegalArgumentException {

        if (userId == null) {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_EXCEPTION_PREFIX_MSG + " Id can't be a null.");
        }
        SqlParameterSource namedParameters = new MapSqlParameterSource(USER_ID, userId);
        User user = mNamedParameterJdbcTemplate.queryForObject(GET_USER_BY_ID, namedParameters, new UserRowMapper());
        return user;
    }

    @Override
    public User getUserByLogin(String login) throws IllegalArgumentException {

        if (login == null) {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_EXCEPTION_PREFIX_MSG + " Login can't be a null.");
        }
        SqlParameterSource namedParameters = new MapSqlParameterSource(LOGIN, login);
        User user = mNamedParameterJdbcTemplate.queryForObject(GET_USER_BY_LOGIN, namedParameters, new UserRowMapper());
        return user;
    }

    @Override
    public Integer addUser(User user) throws IllegalArgumentException {

        if (user == null) {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_EXCEPTION_PREFIX_MSG + " User can't be a null.");
        }

        if (isUserExist(user.getLogin())) {
            throw new DataAccessException(ERROR_MSG_USER_ALREADY_EXISTS) {
            };
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(user);
        int updatedRowsCount = mNamedParameterJdbcTemplate.update(ADD_USER, namedParameters, keyHolder);

        Number newId = keyHolder.getKey();
        if (newId == null) {
            throw new DataAccessException("Returned unique key is a null.") {
            };
        }
        return keyHolder.getKey().intValue();
    }

    private boolean isUserExist(String login) {
        int countUserWithSameLogin = mNamedParameterJdbcTemplate.queryForObject(
                GET_COUNT_USER_WITH_SAME_LOGIN,
                new MapSqlParameterSource(LOGIN, login),
                Integer.class);

        return countUserWithSameLogin != 0;
    }

    private boolean isUserExist(Integer userId) {
        int countUserWithSameLogin = mNamedParameterJdbcTemplate.queryForObject(
                GET_COUNT_USER_WITH_SAME_ID,
                new MapSqlParameterSource(USER_ID, userId),
                Integer.class);

        return countUserWithSameLogin != 0;
    }

    @Override
    public boolean updateUser(User user) throws IllegalArgumentException {

        if (user == null) {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_EXCEPTION_PREFIX_MSG + " User can't be a null.");
        }

        if (isUserExist(user.getUserId())) {

            SqlParameterSource namedParameter = new BeanPropertySqlParameterSource(user);
            int countUpdatedRow = mNamedParameterJdbcTemplate.update(UPDATE_USER, namedParameter);

            if (countUpdatedRow > 0) {
                return true;
            }

        } else {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_EXCEPTION_PREFIX_MSG + " User is not exist.");
        }

        return false;
    }

    @Override
    public boolean deleteUserById(Integer userId) throws IllegalArgumentException {

        if (userId == null) {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_EXCEPTION_PREFIX_MSG + " User id can't be a null.");
        }

        if (isUserExist(userId)) {

            int countDeletedRow = mNamedParameterJdbcTemplate.update(DELETE_USER_BY_ID, new MapSqlParameterSource(USER_ID, userId));
            if (countDeletedRow > 0) {
                return true;
            }

        } else {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_EXCEPTION_PREFIX_MSG + " User with userId = " + userId + " is not exist.");
        }

        return false;
    }

    @Override
    public boolean deleteUserByLogin(String login) throws IllegalArgumentException {
        if (login == null) {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_EXCEPTION_PREFIX_MSG + " Login can't be a null.");
        }

        if (!isUserExist(login)) {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_EXCEPTION_PREFIX_MSG + " User with Login = " + login + " is not exist.");

        } else {

            int countDeletedRow = mNamedParameterJdbcTemplate.update(DELETE_USER_BY_LOGIN, new MapSqlParameterSource(LOGIN, login));
            if (countDeletedRow > 0) {
                return true;
            }
        }

        return false;
    }

    private static final class UserRowMapper implements RowMapper<User> {

        @Override
        public User mapRow(ResultSet resultSet, int i) throws SQLException {

            return new User(
                    resultSet.getInt(USER_ID),
                    resultSet.getString(LOGIN),
                    resultSet.getString(PASSWORD),
                    resultSet.getString(DESCRIPTION));
        }
    }
}
