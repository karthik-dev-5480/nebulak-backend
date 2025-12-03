package com.nebulak.dto;

import org.springframework.security.core.Authentication;

public class AuthenticationResult {
    private final Authentication authentication;
    private final String errorMessage;

    // Success constructor
    public AuthenticationResult(Authentication authentication) {
        this.authentication = authentication;
        this.errorMessage = null;
    }

    // Failure constructor
    public AuthenticationResult(String errorMessage) {
        this.authentication = null;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return authentication != null;
    }
    
    // Getters...
    public Authentication getAuthentication() {
        return authentication;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}