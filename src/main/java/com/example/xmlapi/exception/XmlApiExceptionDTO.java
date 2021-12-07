package com.example.xmlapi.exception;

import lombok.Value;

@Value
public class XmlApiExceptionDTO {
    String error;

    public static XmlApiExceptionDTO from(XmlApiException xmlApiException) {
        return new XmlApiExceptionDTO(xmlApiException.getMessage());
    }
}
