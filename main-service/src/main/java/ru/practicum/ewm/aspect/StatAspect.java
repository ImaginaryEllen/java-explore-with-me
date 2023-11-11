package ru.practicum.ewm.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.StatsDtoRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Aspect
@RequiredArgsConstructor
public class StatAspect {
    private final Logger logger = Logger.getLogger(StatAspect.class.getName());

    private final StatsClient client;

    private final HttpServletRequest request;

    @Value("${spring.application.name}")
    String app;

    @Pointcut("@annotation(ru.practicum.ewm.aspect.AddStat)")
    public void processingMethods() {
    }

    @AfterReturning(pointcut = "processingMethods()")
    public void logAfterReturning(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSourceLocation().getWithinType().getName();
        logger.log(Level.INFO, "The method - " + methodName + " was executed successfully from class " + className);
        StatsDtoRequest hit = new StatsDtoRequest(app, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        client.post(hit);
    }
}
