package com.segniertomato.work.rest;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.segniertomato.work.model.Employee;
import com.segniertomato.work.model.Investigation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;


public class CustomJsonDeserializers {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final class InvestigationFieldNames {

        private static final String INVESTIGATION_ID = "investigationId";
        private static final String NUMBER = "number";
        private static final String TITLE = "title";
        private static final String DESCRIPTION = "description";
        private static final String START_INVESTIGATION_DATE = "startInvestigationDate";
        private static final String END_INVESTIGATION_DATE = "endInvestigationDate";
        private static final String INVOLVED_STAFF = "involvedStaff";
    }

    private static final class EmployeeFieldNames {

        private static final String EMPLOYEE_ID = "employeeId";
        private static final String NAME = "name";
        private static final String AGE = "age";
        private static final String START_WORKING_DATE = "startWorkingDate";
        private static final String PARTICIPATED_INVESTIGATIONS = "participatedInvestigations";
    }

    public static final class InvestigationJsonDeserializer extends JsonDeserializer<Investigation> {

        private static final Logger LOGGER = LogManager.getLogger();

        private final DateTimeFormatter offsetDateTimeFormatter;
        private final DateTimeFormatter localDateFormatter;

        public InvestigationJsonDeserializer(DateTimeFormatter offsetDateTimeFormatter, DateTimeFormatter localDateFormatter) {

            LOGGER.debug("constructor InvestigationJsonDeserializer(DateTimeFormatter, DateTimeFormatter)");
            this.offsetDateTimeFormatter = offsetDateTimeFormatter;
            this.localDateFormatter = localDateFormatter;
        }

        @Override
        public Investigation deserialize(JsonParser parser, DeserializationContext context) throws IOException {

            LOGGER.debug("deserialize(JsonParser, DeserializationContext)");

            JsonNode node = parser.getCodec().readTree(parser);

//            if (node == null) {
//                String incomingJSON = parser.getValueAsString();
//                JsonParser jsonParser = parser.getCodec().getFactory().createParser(incomingJSON);
//                node = jsonParser.getCodec().readTree(jsonParser);
//            }

            Investigation deserializedInvestigation = createInvestigation(node, offsetDateTimeFormatter);
            if (node.hasNonNull(InvestigationFieldNames.INVOLVED_STAFF)) {
                JsonNode involvedStaffNode = node.get(InvestigationFieldNames.INVOLVED_STAFF);

                List<Employee> involvedStaff = new ArrayList<>(involvedStaffNode.size());
                involvedStaffNode.forEach((itemNode) -> involvedStaff.add(createEmployee(itemNode, localDateFormatter)));

                deserializedInvestigation.setInvolvedStaff(involvedStaff);
            }

            return deserializedInvestigation;
        }

    }

    public static final class EmployeeJsonDeserializer extends JsonDeserializer<Employee> {

        private static final Logger LOGGER = LogManager.getLogger();

        private final DateTimeFormatter offsetDateTimeFormatter;
        private final DateTimeFormatter localDateFormatter;

        public EmployeeJsonDeserializer(DateTimeFormatter localDateFormatter, DateTimeFormatter offsetDateTimeFormatter) {

            LOGGER.debug("constructor EmployeeJsonDeserializer(DateTimeFormatter, DateTimeFormatter)");

            this.offsetDateTimeFormatter = offsetDateTimeFormatter;
            this.localDateFormatter = localDateFormatter;
        }

        @Override
        public Employee deserialize(JsonParser parser, DeserializationContext context) throws IOException {

            LOGGER.debug("deserialize(JsonParser, DeserializationContext)");

            JsonNode node = parser.getCodec().readTree(parser);

            Employee deserializedEmployee = createEmployee(node, localDateFormatter);

            if (node.hasNonNull(EmployeeFieldNames.PARTICIPATED_INVESTIGATIONS)) {
                JsonNode participatedInvestigationsNode = node.get(EmployeeFieldNames.PARTICIPATED_INVESTIGATIONS);

                List<Investigation> investigations = new ArrayList<>(participatedInvestigationsNode.size());
                participatedInvestigationsNode.forEach((itemNode) -> investigations.add(createInvestigation(itemNode, offsetDateTimeFormatter)));

                deserializedEmployee.setParticipatedInvestigations(investigations);
            }

            return deserializedEmployee;
        }
    }

    private static Investigation createInvestigation(JsonNode node, DateTimeFormatter offsetDateTimeFormatter) {

        LOGGER.debug("createInvestigation(JsonNode, DateTimeFormatter)");

        Integer investigationId = getTextOrIntegerValueFromNode(node.get(InvestigationFieldNames.INVESTIGATION_ID), Integer.class);

        Integer number = getTextOrIntegerValueFromNode(node.get(InvestigationFieldNames.NUMBER), Integer.class);

        String title = getTextOrIntegerValueFromNode(node.get(InvestigationFieldNames.TITLE), String.class);

        String description = getTextOrIntegerValueFromNode(node.get(InvestigationFieldNames.DESCRIPTION), String.class);

        OffsetDateTime startDate = getDateFromNode(
                node.get(InvestigationFieldNames.START_INVESTIGATION_DATE), offsetDateTimeFormatter, OffsetDateTime.class);

        OffsetDateTime endDate = getDateFromNode(
                node.get(InvestigationFieldNames.END_INVESTIGATION_DATE), offsetDateTimeFormatter, OffsetDateTime.class);

        return new Investigation(investigationId, number, title, description, startDate, endDate);
    }

    private static Employee createEmployee(JsonNode node, DateTimeFormatter localDateFormatter) {

        LOGGER.debug("createEmployee(JsonNode, DateTimeFormatter)");

        Integer employeeId = getTextOrIntegerValueFromNode(node.get(EmployeeFieldNames.EMPLOYEE_ID), Integer.class);

        String name = getTextOrIntegerValueFromNode(node.get(EmployeeFieldNames.NAME), String.class);

        LocalDate age = getDateFromNode(
                node.get(EmployeeFieldNames.AGE), localDateFormatter, LocalDate.class);

        LocalDate startWorkingDate = getDateFromNode(
                node.get(EmployeeFieldNames.START_WORKING_DATE), localDateFormatter, LocalDate.class);

        return new Employee(employeeId, name, age, startWorkingDate);
    }

    private static <T> T getTextOrIntegerValueFromNode(JsonNode node, Class<T> classValue) {

        LOGGER.debug("getTextOrIntegerValueFromNode(JsonNode, Class<T>)");

        String typeName = classValue.getName();

        if (typeName.equals(String.class.getName())) {
            String value = node.isNull() ? null : node.textValue();
            return classValue.cast(value);

        } else if (classValue.getName().equals(Integer.class.getName())) {
            Integer value = node.isNull() ? null : node.intValue();
            return classValue.cast(value);

        } else {
            throw new IllegalArgumentException("Not supported incoming class type. Incoming type should be String or Integer.");
        }

    }

    private static <T> T getDateFromNode(JsonNode node, DateTimeFormatter formatter, Class<T> classValue) {

        LOGGER.debug("getDateFomNode(JsonNode, DateTimeFormatter, Class<T>)");

        try {

            String typeName = classValue.getName();

            if (typeName.equals(OffsetDateTime.class.getName())) {
                OffsetDateTime offsetDateTime = node.isNull() ? null : OffsetDateTime.parse(node.textValue(), formatter);
                return classValue.cast(offsetDateTime);

            } else if (typeName.equals(LocalDate.class.getName())) {
                LocalDate localDate = node.isNull() ? null : LocalDate.parse(node.textValue(), formatter);
                return classValue.cast(localDate);

            } else {
                throw new IllegalArgumentException("Not supported incoming class type. Incoming type should be OffsetDateTime or LocalDate.");
            }

        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Can't parse incoming JSON into date format. Error message: "
                    + ex.getLocalizedMessage());
        }

    }

}
