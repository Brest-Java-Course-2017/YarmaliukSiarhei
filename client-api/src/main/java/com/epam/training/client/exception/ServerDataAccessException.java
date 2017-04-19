package com.epam.training.client.exception;


public class ServerDataAccessException extends RuntimeException{

    public ServerDataAccessException(String message){
        super(message);
    }

    public ServerDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
