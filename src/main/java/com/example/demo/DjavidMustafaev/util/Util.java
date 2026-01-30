package com.example.demo.DjavidMustafaev.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Util {
    public static final String LOCAL_DATE_NOW = "Asia/Yekaterinburg";
    public static final String MESSAGE_EXCEPTION = "Нельзя записывать транзакцию на будущее";
    public static final String START_DATE_KEY = "startDate";
    public static final String END_DATE_KEY = "endDate";

    public static LocalDate getCurrentDateInTimeZone() { // метод установки настоящего времени
        ZoneId zone = ZoneId.of(LOCAL_DATE_NOW);
        return LocalDate.now(zone);
    }

    public static void isAfterToday (LocalDate date) { // метод проверки на будущее время
        LocalDate today = LocalDate.now();
        if (date.isAfter(today)) {
            throw new  IllegalArgumentException(MESSAGE_EXCEPTION);
        }
    }

    public static Map<String, LocalDate> getStartAndEndDate(int year, int month) { // метод получения с 1 по последнее число месяца в мапе
        Map<String, LocalDate> mapDate = new HashMap<>();
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        mapDate.put(START_DATE_KEY, startDate);
        mapDate.put(END_DATE_KEY, endDate);
        return mapDate;
    }
}
