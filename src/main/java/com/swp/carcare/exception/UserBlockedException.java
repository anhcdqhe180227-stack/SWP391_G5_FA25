package com.swp.carcare.exception;

import org.springframework.security.core.AuthenticationException;

public class UserBlockedException extends AuthenticationException {
    public UserBlockedException(String msg) {
        super(msg);
    }
}
