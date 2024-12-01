package com.ortecfinance.tasklist.helper.execptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CustomException extends ResponseStatusException {

    private final String errorMessage;

    public CustomException(String errorMessage) {
        super(HttpStatus.BAD_REQUEST, errorMessage);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }


}
