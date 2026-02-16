package com.cocos.cocos.common.datetime;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.enums.message.FailMessage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DateParser {

    private static final DateTimeFormatter ISO_DATE_TIME =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final DateTimeFormatter ISO_DATE =
            DateTimeFormatter.ISO_LOCAL_DATE;

    private static final DateTimeFormatter LEGACY_DOT_DATE =
            DateTimeFormatter.ofPattern("yyyy.MM.dd");

    private DateParser() {
    }

    public static LocalDateTime parseToLocalDateTime(final String value) {
        if (value == null || value.isBlank()) {
            throw new CocosException(FailMessage.BAD_REQUEST_INVALID_VISITED_AT_FORMAT);
        }

        try {
            return LocalDateTime.parse(value, ISO_DATE_TIME);
        } catch (DateTimeParseException ignored) {
        }

        try {
            return LocalDate.parse(value, ISO_DATE).atStartOfDay();
        } catch (DateTimeParseException ignored) {
        }

        try {
            return LocalDate.parse(value, LEGACY_DOT_DATE).atStartOfDay();
        } catch (DateTimeParseException ignored) {
        }

        throw new CocosException(FailMessage.BAD_REQUEST_INVALID_VISITED_AT_FORMAT);
    }
}
