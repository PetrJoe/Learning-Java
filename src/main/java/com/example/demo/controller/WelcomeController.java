package com.example.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Welcome", description = "Welcome endpoint")
public class WelcomeController {

    @GetMapping("/")
    @Operation(summary = "Welcome endpoint", description = "Returns API information and links")
    @ApiResponse(responseCode = "200", description = "Welcome message")
    public ResponseEntity<Map<String, String>> welcome() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to Product Management API");
        response.put("version", "1.0.0");
        response.put("swagger-ui", "http://localhost:8080/swagger-ui.html");
        response.put("api-docs", "http://localhost:8080/api-docs");
        response.put("products-endpoint", "http://localhost:8080/api/products");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Returns the health status of the application")
    @ApiResponse(responseCode = "200", description = "Application is healthy")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Application is running");
        return ResponseEntity.ok(response);
    }
}
