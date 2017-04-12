package com.epam.training.service;

import com.epam.training.dao.UserDao;
import com.epam.training.dao.UserDaoImpl;
import com.epam.training.model.User;
import com.epam.training.util.MessageError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.List;
import java.util.regex.Pattern;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LogManager.getLogger();
//    private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class);

    @Value("${sql.getCountUsersWithSameUserId}")
    private final String GET_COUNT_USER_WITH_SAME_ID = null;

    @Value("${sql.getCountUsersWithSameLogin}")
    private final String GET_COUNT_USER_WITH_SAME_LOGIN = null;

    @Value("${pattern.login}")
    private final String LOGIN_PATTERN = null;

    @Value("${pattern.password}")
    private final String PASSWORD_PATTERN = null;

    private static final String LOGIN = "login";
    private static final String USER_ID = "user_id";

    @Autowired(required = false)
    private NamedParameterJdbcTemplate mNamedParameterJdbcTemplate = null;

    @Autowired
    private UserDao mUserDao = null;

    public UserServiceImpl() {

        LOGGER.debug("constructor UserServiceImpl()");
    }

    public UserServiceImpl(DataSource dataSource, UserDao userDao) {

        LOGGER.debug("constructor UserServiceImpl(DataSource, UserDao)");

        this.mUserDao = userDao;
        this.mNamedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<User> getAllUsers() throws DataAccessException {

        LOGGER.debug("getAllUsers()");
        return mUserDao.getAllUsers();
    }

    @Override
    public User getUserById(Integer userId) throws DataAccessException {

        LOGGER.debug("getUserById(Integer) - user Id is: {}", userId);

        Assert.notNull(userId, MessageError.InvalidIncomingParameters.ID_CAN_NOT_BE_NULL);
        Assert.isTrue(userId > 0, MessageError.InvalidIncomingParameters.ID_SHOULD_BE_GREATER_THAN_ZERO);
        Assert.isTrue(isUserExist(userId), MessageError.InvalidIncomingParameters.USER_IS_NOT_EXIST);

        return mUserDao.getUserById(userId);
    }

    @Override
    public User getUserByLogin(String login) throws DataAccessException {

        LOGGER.debug("getUserByLogin(String) - user Login is: {}", login);

        Assert.notNull(login, MessageError.InvalidIncomingParameters.LOGIN_CAN_NOT_BE_NULL);
        Assert.isTrue(isValidLogin(login), MessageError.InvalidIncomingParameters.LOGIN_SHOULD_MATCH_PATTERN);
        Assert.isTrue(isUserExist(login), MessageError.InvalidIncomingParameters.USER_IS_NOT_EXIST);

        return mUserDao.getUserByLogin(login);
    }

    @Override
    public Integer addUser(User user) throws DataAccessException {

        LOGGER.debug("addUser(User) - User is: {}", user);

//        Assert from Spring framework or Commons Lang
//        Objects.requireNonNull(user) if user is null throw NullPointerException

        Assert.notNull(user, MessageError.InvalidIncomingParameters.USER_CAN_NOT_BE_NULL);

        Integer userId = user.getUserId();
        Assert.isTrue(userId == null || userId == -1, MessageError.InvalidIncomingParameters.ID_SHOULD_BE_NULL_OR_NEGATIVE_ONE);

        String userLogin = user.getLogin();
        Assert.notNull(userLogin, MessageError.InvalidIncomingParameters.LOGIN_CAN_NOT_BE_NULL);
        Assert.isTrue(isValidLogin(userLogin), MessageError.InvalidIncomingParameters.LOGIN_SHOULD_MATCH_PATTERN);

        String userPassword = user.getPassword();
        Assert.notNull(userPassword, MessageError.InvalidIncomingParameters.PASSWORD_CAN_NOT_BE_NULL);
        Assert.isTrue(isValidPassword(userPassword), MessageError.InvalidIncomingParameters.PASSWORD_SHOULD_MATCH_PATTERN);

        Assert.isTrue(!isUserExist(userLogin), MessageError.ADDED_USER_ALREADY_EXISTS);

        return mUserDao.addUser(user);
    }

    @Override
    public boolean updateUser(User user) throws DataAccessException {

        LOGGER.debug("deleteUserById(Integer) - User is: {}", user);

        Assert.notNull(user, MessageError.InvalidIncomingParameters.USER_CAN_NOT_BE_NULL);

        Integer userId = user.getUserId();
        Assert.isTrue(userId != null, MessageError.InvalidIncomingParameters.ID_CAN_NOT_BE_NULL);
        Assert.isTrue(userId > 0, MessageError.InvalidIncomingParameters.ID_SHOULD_BE_GREATER_THAN_ZERO);

        String userLogin = user.getLogin();
        Assert.notNull(userLogin, MessageError.InvalidIncomingParameters.LOGIN_CAN_NOT_BE_NULL);
        Assert.isTrue(isValidLogin(userLogin), MessageError.InvalidIncomingParameters.LOGIN_SHOULD_MATCH_PATTERN);

        String userPassword = user.getPassword();
        Assert.notNull(userPassword, MessageError.InvalidIncomingParameters.PASSWORD_CAN_NOT_BE_NULL);
        Assert.isTrue(isValidPassword(userPassword), MessageError.InvalidIncomingParameters.PASSWORD_SHOULD_MATCH_PATTERN);

        Assert.isTrue(isUserExist(userId), MessageError.InvalidIncomingParameters.USER_IS_NOT_EXIST);

        int countUpdatedUser = mUserDao.updateUser(user);
        return countUpdatedUser > 0;
    }

    @Override
    public boolean deleteUserById(Integer userId) throws DataAccessException {

        LOGGER.debug("deleteUserById(Integer) - user Id is: {}", userId);

        Assert.isTrue(userId != null, MessageError.InvalidIncomingParameters.ID_CAN_NOT_BE_NULL);
        Assert.isTrue(userId > 0, MessageError.InvalidIncomingParameters.ID_SHOULD_BE_GREATER_THAN_ZERO);

        Assert.isTrue(isUserExist(userId), MessageError.InvalidIncomingParameters.USER_IS_NOT_EXIST);

        int countDeletedUser = mUserDao.deleteUserById(userId);
        return countDeletedUser > 0;
    }

    @Override
    public boolean deleteUserByLogin(String login) throws DataAccessException {

        LOGGER.debug("deleteUserByLogin(String) - user Login is: {}", login);

        Assert.notNull(login, MessageError.InvalidIncomingParameters.LOGIN_CAN_NOT_BE_NULL);
        Assert.isTrue(isValidLogin(login), MessageError.InvalidIncomingParameters.LOGIN_SHOULD_MATCH_PATTERN);

        Assert.isTrue(isUserExist(login), MessageError.InvalidIncomingParameters.USER_IS_NOT_EXIST);

        int countDeletedUser = mUserDao.deleteUserByLogin(login);
        return countDeletedUser > 0;
    }

    private boolean isValidLogin(String login) {
        LOGGER.debug("isValidLogin(String)");

        if (login != null) {
            Pattern loginPattern = Pattern.compile(LOGIN_PATTERN);
            if (loginPattern.matcher(login).matches()) {
                return true;
            }
        }

        return false;
    }

    private boolean isValidPassword(String password) {
        LOGGER.debug("isValidPassword(String)");

        if (password != null) {
            Pattern passwordPattern = Pattern.compile(PASSWORD_PATTERN);
            if (passwordPattern.matcher(password).matches()) {
                return true;
            }
        }

        return false;
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
}
