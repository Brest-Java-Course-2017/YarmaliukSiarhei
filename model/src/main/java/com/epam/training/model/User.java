package com.epam.training.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private static final Logger LOGGER = LogManager.getLogger();

    private Integer mUserId = -1;

    private String mLogin = null;

    private String mDescription = null;

    private String mPassword = null;

    public User(String login, String password) {

        LOGGER.debug("constructor User(String, String)");

        this.mLogin = login;
        this.mPassword = password;
    }

    public User(String login, String password, String description) {

        LOGGER.debug("constructor User(String, String, String)");

        this.mLogin = login;
        this.mPassword = password;
        this.mDescription = description;
    }

    public User(Integer userId, String login, String password) {

        LOGGER.debug("constructor User(Integer, String, String)");

        this.mUserId = userId;
        this.mLogin = login;
        this.mPassword = password;
    }

    @JsonCreator
    public User(@JsonProperty("userId") Integer userId, @JsonProperty("login") String login,
                @JsonProperty("password") String password, @JsonProperty("description") String description) {

        LOGGER.debug("constructor User(Integer, String, String, String)");

        this.mUserId = userId;
        this.mLogin = login;
        this.mDescription = description;
        this.mPassword = password;
    }

    public Integer getUserId() {
        return mUserId;
    }

    public void setUserId(Integer userId) {

        LOGGER.debug("setUserId(Integer)");
        this.mUserId = userId;
    }

    public String getLogin() {
        return mLogin;
    }

    public void setLogin(String login) {

        LOGGER.debug("setLogin(String)");
        this.mLogin = login;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {

        LOGGER.debug("setPassword(String)");
        this.mPassword = password;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {

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
        if (mDescription != null ? !mDescription.equals(user.getDescription()) : user.getDescription() != null) {
            return false;
        }

        return mPassword.equals(user.getPassword());
    }

    @Override
    public String toString() {

        return "User {" +
                "userId = " + this.mUserId +
                ", login = " + this.mLogin +
                ", password = " + mPassword +
                ", description = " + this.mDescription +
                "}";
    }

}
