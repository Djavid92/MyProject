package djavidmustafaev.io.financetracker.util;

import djavidmustafaev.io.financetracker.handler.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
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

    public static BigDecimal calculate(List<BigDecimal> numbers, char operator) {
        if (numbers == null || numbers.size() < 2)
            throw new BusinessException("Нужно минимум 2 числа");
        BigDecimal result = numbers.get(0);
        for (int i = 1; i < numbers.size(); i++) {
            BigDecimal next = numbers.get(i);
            result = switch (operator) {
                case '+' -> result.add(next);
                case '-' -> result.subtract(next);
                case '*' -> result.multiply(next);
                case '/' -> {
                    if (next.compareTo(BigDecimal.ZERO) == 0)
                        throw new ArithmeticException("Деление на ноль");
                    yield result.divide(next, 2, RoundingMode.HALF_UP);
                }
                default -> throw new BusinessException("Неизвестный оператор: " + operator);
            };
        }
        return result;
    }

}
