package com.orderflow.inventoryservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "stock")
public class Stock {

    public Stock() {
    }

    public Stock(String productCode, Integer availableQuantity) {
        this.productCode = productCode;
        this.availableQuantity = availableQuantity;
    }


    @Id
    private String productCode;

    @Column(nullable = false)
    private Integer availableQuantity;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;


    public String getProductCode() {
        return productCode;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

}
