package ru.practicum.shareit.common.exeptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final ValidationException e) throws UnsupportedEncodingException {
        log.error("400 {}", e.getMessage(), e);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(out, true, "UTF-8"));
        String stackTrace = out.toString(Charset.forName("UTF-8"));
        return new ErrorResponse(e.getMessage(), stackTrace);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(final NotFoundException e) throws UnsupportedEncodingException {
        log.error("404 {}", e.getMessage(), e);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(out, true, "UTF-8"));
        String stackTrace = out.toString(Charset.forName("UTF-8"));
        return new ErrorResponse(e.getMessage(), stackTrace);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handle(final ConflictDataException e) throws UnsupportedEncodingException {
        log.error("409 {}", e.getMessage(), e);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(out, true, "UTF-8"));
        String stackTrace = out.toString(Charset.forName("UTF-8"));
        return new ErrorResponse(e.getMessage(), stackTrace);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(final UnsupportedStatusException e) throws UnsupportedEncodingException {
        log.error("500 {}", e.getMessage(), e);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(out, true, "UTF-8"));
        String stackTrace = out.toString(Charset.forName("UTF-8"));
        return new ErrorResponse(e.getMessage(), stackTrace);
    }
}
