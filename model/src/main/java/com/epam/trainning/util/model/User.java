package com.epam.trainning.util.model;

import java.util.Objects;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class User {

    private static final String ILLEGAL_ARGUMENT_EXCEPTION_PREFIX = "Incoming parameter is invalid. ";

    //    Must will be moved in property file
    private static final String LOGIN_PATTERN = "^[a-zA-Z0-9_.-]{5,20}$";
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";

    private Integer mUserId = -1;

    private String mLogin = null;
    private String mDescription = null;
    private String mPassword = null;

    public User(Integer userId, String login, String password) throws IllegalArgumentException {

        if (!isValidUserId(userId) || !isValidLogin(login) || !isValidPassword(password)) {

            String exceptionMessage = null;
            if (!isValidUserId(userId)) {
                exceptionMessage = "User id value is invalid.";
            } else {
                exceptionMessage = !isValidLogin(login) ? "Login value is invalid." : "Password value is invalid.";
            }

            throw new IllegalArgumentException(exceptionMessage);
        }

        this.mUserId = userId;
        this.mLogin = login;
        this.mPassword = password;
    }

    public User(Integer userId, String login, String password, String description) throws IllegalArgumentException{

        if (!isValidUserId(userId) || !isValidLogin(login) || !isValidPassword(password)) {

            String exceptionMessage = null;
            if (!isValidUserId(userId)) {
                exceptionMessage = "User id value is invalid.";
            } else {
                exceptionMessage = isValidLogin(login) ? "Login value is invalid." : "Password value is invalid.";
            }

            throw new IllegalArgumentException(exceptionMessage);
        }


        this.mUserId = userId;
        this.mLogin = login;
        this.mDescription = description;
        this.mPassword = password;
    }

    private boolean isValidUserId(Integer userId) {

        if (userId != null && userId > 0) {
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

        if (!isValidUserId(userId)) {
            String exceptionMessagePostfix = userId == null ? "User id can't be a null." : "User id can't be lower then 0.";
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_EXCEPTION_PREFIX + exceptionMessagePostfix);
        }

        this.mUserId = userId;
    }

    public String getLogin() {
        return mLogin;
    }

    public void setLogin(String login) throws IllegalArgumentException {

        if (!isValidLogin(login)) {
            String exceptionMessagePostfix = login == null ? "Login can't be a null" : "Login doesn't matches pattern.";
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_EXCEPTION_PREFIX + exceptionMessagePostfix);
        }

        this.mLogin = login;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {

        if (!isValidPassword(password)) {
            String exceptionMessagePostfix = password == null ? "Password can't be a null." : "Password doesn't matches pattern.";
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_EXCEPTION_PREFIX + exceptionMessagePostfix);
        }

        this.mPassword = password;
    }

    @Nullable
    public String getDescription() {
        return mDescription;
    }

    public void setDescription(@Nonnull String description) {
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
