package com.example.demo.DjavidMustafaev.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Util {

    public static LocalDate getCurrentDateInTimeZone() { // метод установки настоящего времени
        ZoneId zone = ZoneId.of("Asia/Yekaterinburg");
        return LocalDate.now(zone);
    }

    public static void isAfterToday (LocalDate date) { // метод проверки на будущее время
        LocalDate today = LocalDate.now();
        if (date.isAfter(today)) {
            throw new  IllegalArgumentException("Нельзя записывать транзакцию на будущее");
        }
    }

    public static Map<String, LocalDate> getStartAndEndDate(int year, int month) { // метод получения с 1 по последнее число месяца в мапе
        Map<String, LocalDate> mapDate = new HashMap<>();
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        mapDate.put("startDate", startDate);
        mapDate.put("endDate", endDate);
        return mapDate;
    }
}
