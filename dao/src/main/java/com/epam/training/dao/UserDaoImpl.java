package com.epam.training.dao;

import com.epam.training.model.User;
import com.epam.training.util.MessageError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class UserDaoImpl implements UserDao {

    private static final Logger LOGGER = LogManager.getLogger();

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

    private NamedParameterJdbcTemplate mNamedParameterJdbcTemplate;

    public UserDaoImpl(DataSource dataSource) {
        LOGGER.debug("constructor UserDaoImpl(DataSource)");
        mNamedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<User> getAllUsers() {
        LOGGER.debug("getAllUsers()");
        return mNamedParameterJdbcTemplate.getJdbcOperations().query(GET_ALL_USERS_SQL, new UserRowMapper());
    }

    @Override
    public User getUserById(Integer userId) throws IllegalArgumentException {

        LOGGER.debug("getUserById(Integer)");

        if (userId == null) {
            LOGGER.debug("getUserById(Integer) - throw IllegalArgumentException");
            throw new IllegalArgumentException(MessageError.InvalidIncomingParameters.ID_CAN_NOT_BE_A_NULL);
        }

        LOGGER.debug("getUserById(Integer) - create SqlParameterSource");

        SqlParameterSource namedParameters = new MapSqlParameterSource(USER_ID, userId);
        User user = mNamedParameterJdbcTemplate.queryForObject(GET_USER_BY_ID, namedParameters, new UserRowMapper());
        return user;
    }

    @Override
    public User getUserByLogin(String login) throws IllegalArgumentException {

        LOGGER.debug("getUserByLogin(String)");

        if (login == null) {
            LOGGER.debug("getUserByLogin(String) - throw IllegalArgumentException");
            throw new IllegalArgumentException(MessageError.InvalidIncomingParameters.LOGIN_CAN_NOT_BE_A_NULL);
        }

        LOGGER.debug("getUserByLogin(String) - create SqlParameterSource");

        SqlParameterSource namedParameters = new MapSqlParameterSource(LOGIN, login);
        User user = mNamedParameterJdbcTemplate.queryForObject(GET_USER_BY_LOGIN, namedParameters, new UserRowMapper());
        return user;
    }

    @Override
    public Integer addUser(User user) throws IllegalArgumentException {

        LOGGER.debug("addUser(User)");
        if (user == null) {
            LOGGER.debug("addUser(User) - throw IllegalArgumentException");
            throw new IllegalArgumentException(MessageError.InvalidIncomingParameters.USER_CAN_NOT_BE_A_NULL);
        }

        if (isUserExist(user.getLogin())) {
            LOGGER.debug("addUser(User) - added User doesn't exist.\nThrow DataAccessException");
            throw new DataAccessException(MessageError.ADDED_USER_ALREADY_EXISTS) {
            };
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        LOGGER.debug("addUser(User) - create SqlParameterSource");
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(user);
        mNamedParameterJdbcTemplate.update(ADD_USER, namedParameters, keyHolder);

        LOGGER.debug("addUser(User) - get returned user id");
        Number newId = keyHolder.getKey();
        if (newId == null) {

            LOGGER.debug("addUser(User) - returned user's id is a null.\nThrow DataAccessException");
            throw new DataAccessException(MessageError.RETURNED_UNIQUE_KEY_IS_A_NULL) {
            };
        }
        return keyHolder.getKey().intValue();
    }

    private boolean isUserExist(String login) {
        LOGGER.debug("isUserExist(String)");
        int countUserWithSameLogin = mNamedParameterJdbcTemplate.queryForObject(
                GET_COUNT_USER_WITH_SAME_LOGIN,
                new MapSqlParameterSource(LOGIN, login),
                Integer.class);

        return countUserWithSameLogin != 0;
    }

    private boolean isUserExist(Integer userId) {
        LOGGER.debug("isUserExists(Integer)");
        int countUserWithSameLogin = mNamedParameterJdbcTemplate.queryForObject(
                GET_COUNT_USER_WITH_SAME_ID,
                new MapSqlParameterSource(USER_ID, userId),
                Integer.class);

        return countUserWithSameLogin != 0;
    }

    @Override
    public boolean updateUser(User user) throws IllegalArgumentException {

        LOGGER.debug("updateUser(User)");

        if (user == null) {
            LOGGER.debug("updateUser(User) - throw IllegalArgumentException");
            throw new IllegalArgumentException(MessageError.InvalidIncomingParameters.USER_CAN_NOT_BE_A_NULL);
        }

        if (!isUserExist(user.getUserId())) {
            LOGGER.debug("updateUser(User) - updated User doesn't exist.\nThrow IllegalArgumentException");
            throw new IllegalArgumentException(MessageError.InvalidIncomingParameters.USER_IS_NOT_EXIST);

        } else {
            LOGGER.debug("updateUser(User) - create SqlParameterSource");

            SqlParameterSource namedParameter = new BeanPropertySqlParameterSource(user);

            int countUpdatedRow = mNamedParameterJdbcTemplate.update(UPDATE_USER, namedParameter);
            if (countUpdatedRow > 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean deleteUserById(Integer userId) throws IllegalArgumentException {

        LOGGER.debug("deleteUserById(Integer)");

        if (userId == null) {
            LOGGER.debug("deleteUserById(Integer) - throw IllegalArgumentException");
            throw new IllegalArgumentException(MessageError.InvalidIncomingParameters.ID_CAN_NOT_BE_A_NULL);
        }

        if (!isUserExist(userId)) {
            LOGGER.debug("deleteUserById(Integer) - deleted User doesn't exist.\nThrow IllegalArgumentException");

            throw new IllegalArgumentException(
                    MessageError.InvalidIncomingParameters.COMPOSITE_PREFIX_USER_WITH_ID + userId +
                            MessageError.InvalidIncomingParameters.COMPOSITE_POSTFIX_IS_NOT_EXISTS);

        } else {
            LOGGER.debug("deleteUserById(Integer) - NamedParameterJdbcTemplate update query");

            int countDeletedRow = mNamedParameterJdbcTemplate.update(DELETE_USER_BY_ID, new MapSqlParameterSource(USER_ID, userId));
            if (countDeletedRow > 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean deleteUserByLogin(String login) throws IllegalArgumentException {

        LOGGER.debug("deleteUserByLogin(String)");

        if (login == null) {
            LOGGER.debug("deleteUserByLogin(String) - throw IllegalArgumentException");
            throw new IllegalArgumentException(MessageError.InvalidIncomingParameters.LOGIN_CAN_NOT_BE_A_NULL);
        }

        if (!isUserExist(login)) {
            LOGGER.debug("deleteUserByLogin(String) - deleted User doesn't exist.\nThrow IllegalArgumentException");

            throw new IllegalArgumentException(
                    MessageError.InvalidIncomingParameters.COMPOSITE_PREFIX_USER_WITH_LOGIN + login +
                            MessageError.InvalidIncomingParameters.COMPOSITE_POSTFIX_IS_NOT_EXISTS);

        } else {
            LOGGER.debug("deleteUserByLogin(String) - NamedParameterJdbcTemplate update query");

            int countDeletedRow = mNamedParameterJdbcTemplate.update(DELETE_USER_BY_LOGIN, new MapSqlParameterSource(LOGIN, login));
            if (countDeletedRow > 0) {
                return true;
            }
        }

        return false;
    }

    private static final class UserRowMapper implements RowMapper<User> {

        private static final Logger LOGGER = LogManager.getLogger();
        @Override
        public User mapRow(ResultSet resultSet, int i) throws SQLException {
            LOGGER.debug("mapRow(ResultSet, int) - mapping returned ResultSet into User object");

            return new User(
                    resultSet.getInt(USER_ID),
                    resultSet.getString(LOGIN),
                    resultSet.getString(PASSWORD),
                    resultSet.getString(DESCRIPTION));
        }
    }
}
