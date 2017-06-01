package com.segniertomato.work.rest;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class OffsetDateTimeFormatter implements Formatter<OffsetDateTime> {

    private static final Logger LOGGER = LogManager.getLogger();

    private final DateTimeFormatter OFFSET_DATE_TIME_FORMATTER;

    public OffsetDateTimeFormatter(DateTimeFormatter dateTimeFormatter) {

        LOGGER.debug("constructor OffsetDateTimeFormatter(DateTimeFormatter)");
        OFFSET_DATE_TIME_FORMATTER = dateTimeFormatter;
    }

    @Override
    public OffsetDateTime parse(String text, Locale locale) throws ParseException {

        LOGGER.debug("parse(String, Local) - incoming text is: {}", text);

        if (text == null || text.isEmpty()) return null;
        return OffsetDateTime.parse(text, OFFSET_DATE_TIME_FORMATTER.withLocale(locale));
    }

    @Override
    public String print(OffsetDateTime offsetDateTime, Locale locale) {

        LOGGER.debug("print(OffsetDateTime, Local) - incoming offsetDateTime is: {}", offsetDateTime);

        if (offsetDateTime == null) return "";
        return offsetDateTime.format(OFFSET_DATE_TIME_FORMATTER.withLocale(locale));
    }
}
