package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handlerNotFound(final NotFoundException e) {
        log.warn("Error: ", e);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        return new ApiError(List.of(stackTrace), e.getMessage(),
                "The required object was not found.",
                HttpStatus.NOT_FOUND.toString(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handlerConflict(final ConflictException e) {
        log.warn("Error: ", e);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        return new ApiError(List.of(stackTrace), e.getMessage(),
                "For the requested operation the conditions are not met.",
                HttpStatus.CONFLICT.toString(), LocalDateTime.now());
    }

    @ExceptionHandler({ConstraintViolationException.class,
            MethodArgumentNotValidException.class,
            ValidationException.class,
            MissingRequestValueException.class,
            IllegalArgumentException.class,
            DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handlerException(final Exception e) {
        log.warn("Error: ", e);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        return new ApiError(List.of(stackTrace), e.getMessage(),
                "Incorrectly made request.",
                HttpStatus.BAD_REQUEST.toString(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handlerThrowable(final Throwable e) {
        log.warn("Error: ", e);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        return new ApiError(List.of(stackTrace), e.getMessage(),
                "Internal Server Error.",
                HttpStatus.INTERNAL_SERVER_ERROR.toString(), LocalDateTime.now());
    }
}