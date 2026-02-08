package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class AuthRequest {
    
    @Schema(description = "Username for authentication (optional for login)", example = "john_doe")
    private String username;

    @Schema(description = "Email address (required for login)", example = "john.doe@example.com")
    private String email;
    
    @Schema(description = "Password for authentication", example = "secret123")
    private String password;

    @Schema(description = "First Name", example = "John")
    private String firstName;

    @Schema(description = "Last Name", example = "Doe")
    private String lastName;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}
