package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class TokenRefreshRequest {
    @Schema(description = "Refresh token received during login", example = "a1b2c3d4-...")
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
