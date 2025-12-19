package com.example.demo.DjavidMustafaev.util;

import com.example.demo.DjavidMustafaev.dto.ExpenseDto;
import com.example.demo.DjavidMustafaev.dto.IncomeDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Component
public class Util {

    public LocalDate getCurrentDateInTimeZone() {
        ZoneId zone = ZoneId.of("Asia/Yekaterinburg");
        LocalDate now = LocalDate.now(zone);
        return now;
    }

    public void isAfterToday (ExpenseDto dto) {
        LocalDate today = LocalDate.now();
        if (dto.getDate().isAfter(today)) {
            throw new  IllegalArgumentException("Нельзя записывать транзакцию на будущее");
        }
    }

    public void isAfterToday (IncomeDto dto) {
        LocalDate today = LocalDate.now();
        if (dto.getDate().isAfter(today)) {
            throw new  IllegalArgumentException("Нельзя записывать транзакцию на будущее");
        }
    }

    public Map<String, LocalDate> getStartAndEndDate(int year, int month) {
        Map<String, LocalDate> mapDate = new HashMap<>();
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        mapDate.put("startDate", startDate);
        mapDate. put("endDate", endDate);
        return mapDate;
    }
}
