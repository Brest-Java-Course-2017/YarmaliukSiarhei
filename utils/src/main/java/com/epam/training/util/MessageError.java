package com.epam.training.util;


public final class MessageError {

    private MessageError() {

    }

    public static final String ADDED_USER_ALREADY_EXISTS = "Can't insert a new user. User with the same login already exists.";
    public static final String RETURNED_UNIQUE_KEY_IS_A_NULL = "Returned unique key is a null.";

    public static final class InvalidIncomingParameters {

        public static final String ID_CAN_NOT_BE_A_NULL = "Invalid incoming parameter. Id can't be a null.";
        public static final String LOGIN_CAN_NOT_BE_A_NULL = "Invalid incoming parameter. Login can't be a null.";
        public static final String USER_CAN_NOT_BE_A_NULL = "Invalid incoming parameter. User can't be a null.";
        public static final String USER_IS_NOT_EXIST = "Invalid incoming parameter. User is not exist.";

        public static final String COMPOSITE_PREFIX_USER_WITH_ID = "Invalid incoming parameter. User with userId = ";
        public static final String COMPOSITE_PREFIX_USER_WITH_LOGIN = "Invalid incoming parameter. User with Login = ";
        public static final String COMPOSITE_POSTFIX_IS_NOT_EXISTS = "is not exists.";

        private InvalidIncomingParameters() {

        }
    }
}