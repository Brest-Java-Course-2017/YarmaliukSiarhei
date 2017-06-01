package com.segniertomato.work.rest;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.ArrayList;
import java.util.List;


class CustomJsonDeserializers {

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
        public Investigation deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {

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
        public Employee deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {

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

        Integer investigationId = node.get(InvestigationFieldNames.INVESTIGATION_ID).intValue();
        Integer number = node.get(InvestigationFieldNames.NUMBER).intValue();
        String title = node.get(InvestigationFieldNames.TITLE).textValue();
        String description = node.get(InvestigationFieldNames.DESCRIPTION).textValue();

        String startDate = node.get(InvestigationFieldNames.START_INVESTIGATION_DATE).textValue();
        String endDate = node.get(InvestigationFieldNames.END_INVESTIGATION_DATE).textValue();

        return new Investigation(investigationId, number, title, description,
                OffsetDateTime.parse(startDate, offsetDateTimeFormatter),
                OffsetDateTime.parse(endDate, offsetDateTimeFormatter));
    }

    private static Employee createEmployee(JsonNode node, DateTimeFormatter localDateFormatter) {

        LOGGER.debug("createEmployee(JsonNode, DateTimeFormatter)");

        Integer employeeId = node.get(EmployeeFieldNames.EMPLOYEE_ID).intValue();
        String name = node.get(EmployeeFieldNames.NAME).textValue();
        String age = node.get(EmployeeFieldNames.AGE).textValue();
        String startWorkingDate = node.get(EmployeeFieldNames.START_WORKING_DATE).textValue();

        return new Employee(employeeId, name,
                LocalDate.parse(age, localDateFormatter), LocalDate.parse(startWorkingDate, localDateFormatter));
    }

}
