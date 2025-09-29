package com.example.demo.DjavidMustafaev.aspect;

import com.example.demo.DjavidMustafaev.handler.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.MappingException;
import org.hibernate.service.spi.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Aspect
@Component
@Slf4j
public class ExceptionHandlingAspect {
    @Around("execution(* com.example.demo.DjavidMustafaev.service.FinanceFacade.*(..))")
    public Object handleException(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        try {
            log.info("Выполнение метода: {}", methodName);
            Object result = joinPoint.proceed();
            log.info("Метод {} успешно выполнился", methodName);
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Ошибка при добавлении транзакции в методе {}", methodName);
            throw new BusinessException("Ошибка сохранения: неправильная дата добавления");

        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка целостности данных в методе {} с аргументами: {}", methodName, args, e);
            throw new BusinessException("Ошибка сохранения: нарушение целостности данных", e);

        } catch (DataAccessException e) {
            log.error("Ошибка доступа к данным в методе {}", methodName, e);
            throw new ServiceException("Ошибка при работе с базой данных", e);

        } catch (MappingException e) {
            log.error("Ошибка маппинга данных в методе {}", methodName, e);
            throw new ServiceException("Ошибка преобразования данных", e);

        } catch (NullPointerException e) {
            log.error("Обнаружены null значения в методе {}", methodName, e);
            if (methodName.startsWith("calculateTotal")) {
                return BigDecimal.ZERO;
            }
            throw new ServiceException("Ошибка: обнаружены null значения", e);

        } catch (Exception e) {
            log.error("Неожиданная ошибка в методе {}", methodName, e);
            throw new ServiceException("Внутренняя ошибка сервиса", e);
        }
    }
}
