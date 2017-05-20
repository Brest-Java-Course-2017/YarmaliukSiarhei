package com.segniertomato.work.message;


public class MessageError {

    private MessageError() {
    }

    public static final String EMPLOYEE_NOT_EXISTS = "Error. Employee is not exists.";
    public static final String INVESTIGATION_NOT_EXISTS = "Error. Investigation is not exists.";


    public static final class InvalidIncomingParameters {

        public static final String OFFSET_CAN_NOT_BE_LOWER_THAN_ZERO = "Invalid incoming parameter. Offset should be equals or greater than zero.";
        public static final String LIMIT_CAN_NOT_BE_LOWER_THAN_ZERO = "Invalid incoming parameter. Count should be equals or greater than zero.";

        public static final String START_DATE_IN_PERIOD_CAN_NOT_BE_NULL = "Invalid incoming parameter. Start date in period can't be null.";
        public static final String END_DATE_IN_PERIOD_CAN_NOT_BE_NULL = "Invalid incoming parameter. End date in period can't be null.";

        public static final String START_AND_END_DATES_SHOULD_MATCH_PATTERN = "Invalid incoming parameter. Start and end dates in period should match pattern.";

        public static final String EMPLOYEE_ID_SHOULD_BE_GREATER_THAN_ZERO = "Invalid incoming parameter. Employee id should be greater than zero.";
        public static final String EMPLOYEE_ID_CAN_NOT_BE_NULL = "Invalid incoming parameter. Employee id can't be null.";
        public static final String EMPLOYEE_CAN_NOT_BE_NULL = "Invalid incoming parameter. Employee can't be null.";
        public static final String EMPLOYEE_ID_SHOULD_BE_NULL_OR_MINUS_ONE = "Invalid incoming parameter. Employee id should be null or minus one.";
        public static final String EMPLOYEE_NAME_SHOULD_MATCH_PATTERN = "Invalid incoming parameter. Employee's name should match pattern.";
        public static final String EMPLOYEE_AGE_AND_WORKING_DATES_SHOULD_MATCH_PATTERN =
                "Invalid incoming parameter. Employee's age and start working dates should match pattern.";

        public static final String EMPLOYEE_PARTICIPATED_INVESTIGATIONS_CAN_NOT_BE_NULL =
                "Invalid incoming parameter. Employee's participated investigations can't be null";

        public static final String INVESTIGATION_ID_CAN_NOT_BE_NULL = "Invalid incoming parameter. Investigation id can't be null.";
        public static final String INVESTIGATION_ID_SHOULD_BE_GREATER_THAN_ZERO = "Invalid incoming parameter. Investigation id should be greater than zero.";
        public static final String INVESTIGATION_ID_SHOULD_BE_NULL_OR_MINUS_ONE = "Invalid incoming parameter. Investigation id should be null or minus one.";
        public static final String INVESTIGATION_CAN_NOT_BE_NULL = "Invalid incoming parameter. Investigation can't be null.";
        public static final String INVESTIGATION_DESCRIPTION_CAN_NOT_BE_NULL = "Invalid incoming parameter. Investigation description can't be null.";

        public static final String INVESTIGATION_INVOLVED_STAFF_CAN_NOT_BE_NULL =
                "Invalid incoming parameter. Investigation's involved staff can't be null";

        private InvalidIncomingParameters() {
        }
    }
}
