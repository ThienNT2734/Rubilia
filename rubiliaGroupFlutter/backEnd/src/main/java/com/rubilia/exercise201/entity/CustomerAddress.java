package com.rubilia.exercise201.entity;

import jakarta.persistence.*;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "customer_addresses")
public class CustomerAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private Customer customer;

    @Column(nullable = false)
    private String address_line1;

    @Column
    private String address_line2;

    @Column(nullable = false)
    private String phone_number;

    @Column(nullable = false)
    private String dial_code;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String postal_code;

    @Column(nullable = false)
    private String city;

    // Constructors
    public CustomerAddress() {
    }

    public CustomerAddress(UUID id, Customer customer, String address_line1, String address_line2, String phone_number, String dial_code, String country, String postal_code, String city) {
        this.id = id;
        this.customer = customer;
        this.address_line1 = address_line1;
        this.address_line2 = address_line2;
        this.phone_number = phone_number;
        this.dial_code = dial_code;
        this.country = country;
        this.postal_code = postal_code;
        this.city = city;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getAddress_line1() {
        return address_line1;
    }

    public void setAddress_line1(String address_line1) {
        this.address_line1 = address_line1;
    }

    public String getAddress_line2() {
        return address_line2;
    }

    public void setAddress_line2(String address_line2) {
        this.address_line2 = address_line2;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getDial_code() {
        return dial_code;
    }

    public void setDial_code(String dial_code) {
        this.dial_code = dial_code;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}