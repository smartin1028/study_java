package org.study.green.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static String defaultStrDate() {
        return defaultStrDateOfLocalDateTime(LocalDateTime.now());
    }

    public static String defaultStrDateOfLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(DEFAULT_PATTERN));
    }

    public static String convertNowDateToStrByPattern(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String convertLocalDateTimeToStr(LocalDateTime localDateTime, String pattern) {
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String convertDateToStr(Date date, String pattern) {
        Instant instant = date.toInstant();
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        LocalDateTime ldt = zdt.toLocalDateTime();
        return ldt.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String convertLocalDateToStr(LocalDate localDate, String pattern) {
        LocalDateTime datetime = localDate.atStartOfDay();
        return datetime.format(DateTimeFormatter.ofPattern(pattern));
    }
}
