package com.epam.training.client.rest;


import com.epam.training.client.exception.ServerDataAccessException;
import com.epam.training.client.rest.api.UsersConsumer;
import com.epam.training.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;


public class UsersConsumerRest implements UsersConsumer {

    private Logger LOGGER = LogManager.getLogger();

    private String mHostUrl;
    private String mUrlUsers;
    private String mUrlUser;


    RestTemplate mRestTemplate;

    public UsersConsumerRest(String hostUrl, String urlUsers, String uslUser) {

        LOGGER.debug("constructor UsersConsumerRest(String, String, String)");

        this.mHostUrl = hostUrl;
        this.mUrlUsers = urlUsers;
        this.mUrlUser = uslUser;

    }

    public void setRestTemplate(RestTemplate restTemplate) {

        LOGGER.debug("setRestTemplate(RestTemplate)");
        this.mRestTemplate = restTemplate;
    }

    @Override
    public List<User> getAllUsers() throws ServerDataAccessException {

        LOGGER.debug("getAllUsers()");

        ResponseEntity<List> responseEntity = mRestTemplate.getForEntity(mHostUrl + "/" + mUrlUsers, List.class);
        return (List<User>) responseEntity.getBody();
    }

    @Override
    public User getUserById(Integer userId) throws ServerDataAccessException {

        LOGGER.debug("getUserById(Integer)");

        ResponseEntity<User> responseEntity = mRestTemplate.getForEntity(mHostUrl + "/" + mUrlUser + "/id/{userId}", User.class, userId);
        return responseEntity.getBody();
    }

    @Override
    public User getUserByLogin(String login) throws ServerDataAccessException {

        LOGGER.debug("getUserByLogin(String)");

        ResponseEntity<User> responseEntity = mRestTemplate.getForEntity(mHostUrl + "/" + mUrlUser + "/login/{login}", User.class, login);
        return responseEntity.getBody();
    }

    @Override
    public Integer addUser(User user) throws ServerDataAccessException {

        LOGGER.debug("addUser(User)");

        ResponseEntity<Integer> responseEntity = mRestTemplate.postForEntity(mHostUrl + "/" + mUrlUser, user, Integer.class);

//        HttpHeaders headers = responseEntity.getHeaders();
//        Iterator iterator = headers.entrySet().iterator();
//
//        while (iterator.hasNext()) {
//
//            Map.Entry<String, List<String>> entry = (Map.Entry<String, List<String>>) iterator.next();
//            System.out.print("For header " + entry.getKey() + " : ");
//
//            LOGGER.debug("addUser(User) - Header: {}", entry.getKey());
//
//            for (String headerValue : entry.getValue()) {
//                LOGGER.debug("addUser(User) - Header value is: {}",headerValue);
//                System.out.println(headerValue);
//            }
//        }

        return responseEntity.getBody();
    }

    @Override
    public void updateUser(User user) throws ServerDataAccessException {

        LOGGER.debug("updateUser(User)");

//        String updateUrl = mHostUrl + "/" + mUrlUser + "/" + user.getUserId() + "/" +
//                user.getLogin() + "/" + user.getPassword() + "/" + user.getDescription();
//        mRestTemplate.put(updateUrl, null);

        mRestTemplate.put(mHostUrl + "/" + mUrlUser + "/{id}/{login}/{password}/{description}", null,
                user.getUserId(), user.getLogin(), user.getPassword(), user.getDescription());
    }

    @Override
    public void deleteUserById(Integer userId) throws ServerDataAccessException {

        LOGGER.debug("deleteUserById(Integer)");

        String url = mHostUrl + "/" + mUrlUser + "/id";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("userId", userId);

        mRestTemplate.delete(builder.build().encode().toUri());
    }

    @Override
    public void deleteUserByLogin(String login) throws ServerDataAccessException {

        LOGGER.debug("deleteUserByLogin(String)");

        String url = mHostUrl + "/" + mUrlUser + "/login";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("login", login);

        mRestTemplate.delete(builder.build().encode().toUri());

//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
//        HttpEntity requestEntity = new HttpEntity(headers);
//
//        ResponseEntity<String> responseEntity = mRestTemplate.exchange(builder.build().encode().toUri(), HttpMethod.DELETE, requestEntity, String.class);
//        System.out.println("REST's delete method returned status: " + responseEntity.getStatusCode());
    }

}
