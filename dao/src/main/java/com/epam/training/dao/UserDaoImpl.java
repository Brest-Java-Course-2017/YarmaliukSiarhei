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


    private static final String LOGIN = "login";
    private static final String USER_ID = "user_id";
    private static final String PASSWORD = "password";
    private static final String DESCRIPTION = "description";

    private NamedParameterJdbcTemplate mNamedParameterJdbcTemplate = null;

    public UserDaoImpl(DataSource dataSource) {
        LOGGER.debug("constructor UserDaoImpl(DataSource)");
        mNamedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<User> getAllUsers() throws DataAccessException {
        LOGGER.debug("getAllUsers()");
        return mNamedParameterJdbcTemplate.getJdbcOperations().query(GET_ALL_USERS_SQL, new UserRowMapper());
    }

    @Override
    public User getUserById(Integer userId) throws DataAccessException {

        LOGGER.debug("getUserById(Integer)");

        LOGGER.debug("getUserById(Integer) - create SqlParameterSource");
        SqlParameterSource namedParameters = new MapSqlParameterSource(USER_ID, userId);
        User user = mNamedParameterJdbcTemplate.queryForObject(GET_USER_BY_ID, namedParameters, new UserRowMapper());
        return user;
    }

    @Override
    public User getUserByLogin(String login) throws DataAccessException {

        LOGGER.debug("getUserByLogin(String)");

        LOGGER.debug("getUserByLogin(String) - create SqlParameterSource");
        SqlParameterSource namedParameters = new MapSqlParameterSource(LOGIN, login);
        User user = mNamedParameterJdbcTemplate.queryForObject(GET_USER_BY_LOGIN, namedParameters, new UserRowMapper());
        return user;
    }

    @Override
    public Integer addUser(User user) throws DataAccessException {

        LOGGER.debug("addUser(User)");

        KeyHolder keyHolder = new GeneratedKeyHolder();

        LOGGER.debug("addUser(User) - create SqlParameterSource");
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(user);
        mNamedParameterJdbcTemplate.update(ADD_USER, namedParameters, keyHolder);

        LOGGER.debug("addUser(User) - get returned user id");
        Number newId = keyHolder.getKey();
        if (newId == null) {

            LOGGER.debug("addUser(User) - returned user's id is a null.\nThrow DataAccessException");
            throw new DataAccessException(MessageError.RETURNED_UNIQUE_KEY_IS_NULL) {
            };
        }
        return keyHolder.getKey().intValue();
    }

    @Override
    public int updateUser(User user) throws DataAccessException {

        LOGGER.debug("updateUser(User)");

        LOGGER.debug("updateUser(User) - create SqlParameterSource");
        SqlParameterSource namedParameter = new BeanPropertySqlParameterSource(user);
        return mNamedParameterJdbcTemplate.update(UPDATE_USER, namedParameter);
    }

    @Override
    public int deleteUserById(Integer userId) throws DataAccessException {

        LOGGER.debug("deleteUserById(Integer)");

        LOGGER.debug("deleteUserById(Integer) - NamedParameterJdbcTemplate update query");
        return mNamedParameterJdbcTemplate.update(DELETE_USER_BY_ID, new MapSqlParameterSource(USER_ID, userId));
    }

    @Override
    public int deleteUserByLogin(String login) throws DataAccessException {

        LOGGER.debug("deleteUserByLogin(String)");

        LOGGER.debug("deleteUserByLogin(String) - NamedParameterJdbcTemplate update query");
        return mNamedParameterJdbcTemplate.update(DELETE_USER_BY_LOGIN, new MapSqlParameterSource(LOGIN, login));
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
