package com.segniertomato.work.rest;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.segniertomato.work.model.Employee;
import com.segniertomato.work.model.Investigation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;


@Configuration
public class CustomObjectMapper {

    private static final Logger LOGGER = LogManager.getLogger();

    @Bean
    @Primary
    public static ObjectMapper objectMapper() {

        LOGGER.debug("create objectMapper()");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(OffsetDateTime.class, new OffsetDateTimeJsonSerializer(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        javaTimeModule.addDeserializer(OffsetDateTime.class, new OffsetDateTimeJsonDeserializer(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        mapper.registerModule(javaTimeModule);

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(Investigation.class,
                new CustomJsonDeserializers.InvestigationJsonDeserializer(DateTimeFormatter.ISO_OFFSET_DATE_TIME, DateTimeFormatter.ISO_LOCAL_DATE));
        simpleModule.addDeserializer(Employee.class,
                new CustomJsonDeserializers.EmployeeJsonDeserializer(DateTimeFormatter.ISO_LOCAL_DATE, DateTimeFormatter.ISO_OFFSET_DATE_TIME));


//        simpleModule.addDeserializer(OffsetDateTime.class, new OffsetDateTimeJsonDeserializer(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        mapper.registerModule(simpleModule);

        return mapper;
    }

    public static class OffsetDateTimeJsonSerializer extends JsonSerializer<OffsetDateTime> {

        private static final Logger LOGGER = LogManager.getLogger();
        private DateTimeFormatter dateTimeFormatter;

        public OffsetDateTimeJsonSerializer(DateTimeFormatter offsetDateTimeFormatter) {

            LOGGER.debug("constructor OffsetDateTimeJsonSerializer(DateTimeFormatter)");
            this.dateTimeFormatter = offsetDateTimeFormatter;
        }

        @Override
        public void serialize(OffsetDateTime offsetDateTime, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException, JsonProcessingException {

            LOGGER.debug("serialize(OffsetDateTime, JsonGenerator, SerializerProvider)");
            jsonGenerator.writeString(offsetDateTime.format(dateTimeFormatter));
        }
    }

    public static class OffsetDateTimeJsonDeserializer extends JsonDeserializer<OffsetDateTime> {

        private static final Logger LOGGER = LogManager.getLogger();
        private DateTimeFormatter dateTimeFormatter;

        public OffsetDateTimeJsonDeserializer(DateTimeFormatter offsetDateTimeFormatter) {

            LOGGER.debug("constructor OffsetDateTimeJsonDeserializer(DateTimeFormatter)");
            this.dateTimeFormatter = offsetDateTimeFormatter;
        }

        @Override
        public OffsetDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {

            LOGGER.debug("deserialize(JsonParser, DeserializationContext)");
            return OffsetDateTime.parse(parser.getValueAsString(), dateTimeFormatter);
        }
    }

}
