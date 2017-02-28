package com.epam.training.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class User {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String ILLEGAL_ARGUMENT_EXCEPTION_PREFIX = "Incoming parameter is invalid. ";

    //    Must will be moved in property file
//    TODO: 22.02 - checking valid login and password need be moved on Service Level.
    private static final String LOGIN_PATTERN = "^[a-zA-Z0-9_.-]{5,20}$";
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";

    private Integer mUserId = -1;

    private String mLogin = null;
    private String mDescription = null;
    private String mPassword = null;

    public User(String login, String password) throws IllegalArgumentException {

        LOGGER.debug("constructor User(String, String)");

        if (login == null || password == null) {
            LOGGER.debug("constructor User(String, String) - throw IllegalArgumentException");
            throw new IllegalArgumentException(login == null ? "Login value can't be a null." : "Password value can't be a null.");
        }

        this.mLogin = login;
        this.mPassword = password;
    }

    public User(String login, String password, @Nullable String description) throws IllegalArgumentException {

        LOGGER.debug("constructor User(String, String, String)");

        if (login == null || password == null) {
            LOGGER.debug("constructor User(String, String, String) - throw IllegalArgumentException");
            throw new IllegalArgumentException(login == null ? "Login value can't be a null." : "Password value can't be a null.");
        }

        this.mLogin = login;
        this.mPassword = password;
        this.mDescription = description;
    }

    public User(Integer userId, String login, String password) throws IllegalArgumentException {

        LOGGER.debug("constructor User(Integer, String, String)");

        if (!isValidUserId(userId) || !isValidLogin(login) || !isValidPassword(password)) {

            String exceptionMessage = null;
            if (!isValidUserId(userId)) {
                exceptionMessage = "User id value is invalid.";
            } else {
                exceptionMessage = !isValidLogin(login) ? "Login value is invalid." : "Password value is invalid.";
            }

            LOGGER.debug("constructor User(Integer, String, String) - throw IllegalArgumentException");
            throw new IllegalArgumentException(exceptionMessage);
        }

        this.mUserId = userId;
        this.mLogin = login;
        this.mPassword = password;
    }

    public User(Integer userId, String login, String password, @Nullable String description) throws IllegalArgumentException {

        LOGGER.debug("constructor User(Integer, String, String, String)");

        if (!isValidUserId(userId) || !isValidLogin(login) || !isValidPassword(password)) {

            String exceptionMessage = null;
            if (!isValidUserId(userId)) {
                exceptionMessage = "User id value is invalid.";
            } else {
                exceptionMessage = isValidLogin(login) ? "Login value is invalid." : "Password value is invalid.";
            }

            LOGGER.debug("constructor User(Integer, String, String, String) - throw IllegalArgumentException");
            throw new IllegalArgumentException(exceptionMessage);
        }

        this.mUserId = userId;
        this.mLogin = login;
        this.mDescription = description;
        this.mPassword = password;
    }

    private boolean isValidUserId(Integer userId) {

        if (userId != null && userId >= 0) {
            return true;
        }
        return false;
    }

    private boolean isValidLogin(String login) {

        if (login != null) {
            Pattern loginPattern = Pattern.compile(LOGIN_PATTERN);
            if (loginPattern.matcher(login).matches()) {
                return true;
            }
        }

        return false;
    }

    private boolean isValidPassword(String password) {

        if (password != null) {
            Pattern passwordPattern = Pattern.compile(PASSWORD_PATTERN);
            if (passwordPattern.matcher(password).matches()) {
                return true;
            }
        }

        return false;
    }

    public Integer getUserId() {
        return mUserId;
    }

    public void setUserId(Integer userId) throws IllegalArgumentException {

        LOGGER.debug("setUserId(Integer)");

        if (!isValidUserId(userId)) {
            String exceptionMessagePostfix = userId == null ? "User Id can't be a null." : "User Id can't be lower then 0.";

            LOGGER.debug("setUserId(Integer) - throw IllegalArgumentException");
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_EXCEPTION_PREFIX + exceptionMessagePostfix);
        }

        this.mUserId = userId;
    }

    public String getLogin() {
        return mLogin;
    }

    public void setLogin(String login) throws IllegalArgumentException {

        LOGGER.debug("setLogin(String)");

        if (!isValidLogin(login)) {
            String exceptionMessagePostfix = login == null ? "Login can't be a null" : "Login doesn't matches pattern.";

            LOGGER.debug("setLogin(String) - throw IllegalArgumentException");
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_EXCEPTION_PREFIX + exceptionMessagePostfix);
        }

        this.mLogin = login;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {

        LOGGER.debug("setPassword(String)");

        if (!isValidPassword(password)) {
            String exceptionMessagePostfix = password == null ? "Password can't be a null." : "Password doesn't matches pattern.";

            LOGGER.debug("setPassword(String) - throw IllegalArgumentException");
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_EXCEPTION_PREFIX + exceptionMessagePostfix);
        }

        this.mPassword = password;
    }

    @Nullable
    public String getDescription() {
        return mDescription;
    }

    public void setDescription(@Nonnull String description) {

        LOGGER.debug("setDescription(String)");
        this.mDescription = description;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mUserId, mLogin, mPassword, mDescription);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (!mUserId.equals(user.getUserId())) return false;
        if (!mLogin.equals(user.getLogin())) return false;
        if (mDescription != null ? !mDescription.equals(user.getDescription()) : user.getDescription() != null)
            return false;
        return mPassword.equals(user.getPassword());
    }

    @Override
    public String toString() {

        return "User {" +
                "userId = " + this.mUserId +
                ", login = " + this.mLogin +
                ", password = " + this.mPassword +
                ", description = " + this.mDescription +
                "}";
    }

}
