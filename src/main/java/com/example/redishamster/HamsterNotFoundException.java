package com.example.redishamster;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class HamsterNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 7428051251365675318L;

    public HamsterNotFoundException(String message) {
        super(message);
    }
}