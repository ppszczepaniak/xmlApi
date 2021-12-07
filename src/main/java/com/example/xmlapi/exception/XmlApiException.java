package com.example.xmlapi.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class XmlApiException extends RuntimeException {
    private static final HttpStatus DEFAULT_STATUS = INTERNAL_SERVER_ERROR;

    HttpStatus httpStatus;

    public XmlApiException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public XmlApiException(String message) {
        this(message, DEFAULT_STATUS);
    }

}
