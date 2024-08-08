package com.growus.econnect.base.config;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AuthenticatedMatchers {
    public static final String[] swaggerArray = {
            "/api-docs",
            "/swagger-ui-custom.html",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/api-docs/**",
            "/swagger-ui.html",
            "/swagger-custom-ui.html"
    };
}
