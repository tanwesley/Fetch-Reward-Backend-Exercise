package com.tanwesley.fetchrewards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NegativePointException extends RuntimeException {

    public NegativePointException(String errorMsg) {
        super(errorMsg);
    }
}
