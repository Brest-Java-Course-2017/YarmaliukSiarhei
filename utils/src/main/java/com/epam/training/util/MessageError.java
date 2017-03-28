package com.epam.training.util;


public final class MessageError {

    private MessageError() {

    }

    public static final String ADDED_USER_ALREADY_EXISTS = "Can't insert a new user. User with the same login already exists.";
    public static final String RETURNED_UNIQUE_KEY_IS_NULL = "Returned unique key is null.";

    public static final class InvalidIncomingParameters {

        public static final String USER_CAN_NOT_BE_NULL = "Invalid incoming parameter. User can't be null.";
        public static final String ID_CAN_NOT_BE_NULL = "Invalid incoming parameter. User id can't be null.";
        public static final String LOGIN_CAN_NOT_BE_NULL = "Invalid incoming parameter. Login can't be null.";
        public static final String PASSWORD_CAN_NOT_BE_NULL = "Invalid incoming parameter. Password can't be null.";

        public static final String ID_SHOULD_BE_GREATER_THAN_ZERO = "Invalid incoming parameter. User id should be greater than zero.";
        public static final String ID_SHOULD_BE_NULL_OR_NEGATIVE_ONE = "Invalid incoming parameter. User id should be null or negative one.";

        public static final String LOGIN_SHOULD_MATCH_PATTERN = "Invalid incoming parameter. Login should match pattern.";
        public static final String PASSWORD_SHOULD_MATCH_PATTERN = "Invalid incoming parameter. Password should match pattern.";

        public static final String USER_IS_NOT_EXIST = "Invalid incoming parameter. User is not exist.";

        private InvalidIncomingParameters() {

        }
    }
}