package com.banking.model;

import java.time.LocalDateTime;

/**
 * Represents a bank customer.
 */
public class Customer {

    private String customerId;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Customer() {}

    public Customer(String customerId, String fullName, String email, String phone, String address) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getCustomerId()                      { return customerId; }
    public void   setCustomerId(String customerId)     { this.customerId = customerId; }
    public String getFullName()                        { return fullName; }
    public void   setFullName(String fullName)         { this.fullName = fullName; }
    public String getEmail()                           { return email; }
    public void   setEmail(String email)               { this.email = email; }
    public String getPhone()                           { return phone; }
    public void   setPhone(String phone)               { this.phone = phone; }
    public String getAddress()                         { return address; }
    public void   setAddress(String address)           { this.address = address; }
    public boolean isActive()                          { return active; }
    public void   setActive(boolean active)            { this.active = active; }
    public LocalDateTime getCreatedAt()                { return createdAt; }
    public void   setCreatedAt(LocalDateTime createdAt){ this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt()                { return updatedAt; }
    public void   setUpdatedAt(LocalDateTime updatedAt){ this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Customer{id=" + customerId + ", name=" + fullName + ", active=" + active + "}";
    }
}
