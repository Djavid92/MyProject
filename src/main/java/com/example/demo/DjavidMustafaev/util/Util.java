package com.example.demo.DjavidMustafaev.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class Util {
    public LocalDate getCurrentDateInTimeZone() {
        ZoneId zone = ZoneId.of("Asia/Yekaterinburg");
        LocalDate now = LocalDate.now(zone);
        return now;
    }
}
