package com.epam.training.client.rest;


import com.epam.training.client.exception.ServerDataAccessException;
import com.epam.training.client.rest.api.UsersConsumer;
import com.epam.training.model.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class UsersConsumerRest implements UsersConsumer {

    private String mHostUrl;
    private String mUrlUsers;
    private String mUrlUser;


    RestTemplate mRestTemplate;

    public UsersConsumerRest(String hostUrl, String urlUsers, String uslUser) {
        this.mHostUrl = hostUrl;
        this.mUrlUsers = urlUsers;
        this.mUrlUser = uslUser;

    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.mRestTemplate = restTemplate;
    }

    @Override
    public List<User> getAllUsers() throws ServerDataAccessException {

        ResponseEntity responseEntity = mRestTemplate.getForEntity(mHostUrl + "/" + mUrlUsers, List.class);
        List<User> users = (List<User>) responseEntity.getBody();
        return users;
    }

    @Override
    public User getUserById(Integer userId) throws ServerDataAccessException {

        ResponseEntity responseEntity = mRestTemplate.getForEntity(mHostUrl + "/" + mUrlUser + "/id/{userId}", User.class, userId);
//        ResponseEntity responseEntity = mRestTemplate.getForEntity(mHostUrl + "/" + mUrlUser + "/id/" + userId, User.class);
        User user = (User) responseEntity.getBody();
        return user;
    }

    @Override
    public User getUserByLogin(String login) throws ServerDataAccessException {

//        ResponseEntity responseEntity = mRestTemplate.getForEntity(mHostUrl + "/" + mUrlUser + "/login/" + login, User.class);
        ResponseEntity responseEntity = mRestTemplate.getForEntity(mHostUrl + "/" + mUrlUser + "/login/{login}", User.class, login);
        User user = (User) responseEntity.getBody();
        return user;
    }

    @Override
    public Integer addUser(User user) throws ServerDataAccessException {

        ResponseEntity responseEntity = mRestTemplate.postForEntity(mHostUrl + "/" + mUrlUser, user, Integer.class);


        HttpHeaders headers = responseEntity.getHeaders();
        Iterator iterator = headers.entrySet().iterator();

        while (iterator.hasNext()) {

            Map.Entry<String, List<String>> entry = (Map.Entry<String, List<String>>) iterator.next();
            System.out.print("For header " + entry.getKey() + " : ");

            for (String headerValue : entry.getValue()) {
                System.out.println(headerValue);

            }
        }
//        mRestTemplate.execute(HttpMethod.)
        Integer userId = (Integer) responseEntity.getBody();
        return userId;
    }

    @Override
    public void updateUser(User user) throws ServerDataAccessException {

//        String updateUrl = mHostUrl + "/" + mUrlUser + "/" + user.getUserId() + "/" +
//                user.getLogin() + "/" + user.getPassword() + "/" + user.getDescription();
//        mRestTemplate.put(updateUrl, null);

        mRestTemplate.put(mHostUrl + "/" + mUrlUser + "/{id}/{login}/{password}/{description}", null,
                user.getUserId(), user.getLogin(), user.getPassword(), user.getDescription());
    }

    @Override
    public void deleteUserById(Integer userId) throws ServerDataAccessException {

        String url = mHostUrl + "/" + mUrlUser + "/id";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("userId", userId);

        mRestTemplate.delete(builder.build().encode().toUri());
    }

    @Override
    public void deleteUserByLogin(String login) throws ServerDataAccessException {

        String url = mHostUrl + "/" + mUrlUser + "/login";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("login", login);

        mRestTemplate.delete(builder.build().encode().toUri());
    }

}
