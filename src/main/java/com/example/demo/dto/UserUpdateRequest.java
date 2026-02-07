package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class UserUpdateRequest {

    @Schema(description = "The first name of the user", example = "John")
    private String firstName;

    @Schema(description = "The last name of the user", example = "Doe")
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
