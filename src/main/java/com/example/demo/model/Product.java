package com.example.demo.model;

import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "products")
@Schema(description = "Product entity representing a product in the catalog")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "The unique identifier of the product", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "The name of the product", example = "Laptop")
    private String name;

    @Column(nullable = false)
    @Schema(description = "The description of the product", example = "A powerful laptop for professionals")
    private String description;

    @Column(nullable = false)
    @Schema(description = "The price of the product", example = "999.99")
    private Double price;

    @Column(nullable = false)
    @Schema(description = "The quantity in stock", example = "50")
    private Integer quantity;

    // Constructors
    public Product() {
    }

    public Product(String name, String description, Double price, Integer quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
