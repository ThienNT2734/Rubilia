package com.rubilia.exercise201.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "countries")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "countries_seq")
    @SequenceGenerator(name = "countries_seq", sequenceName = "countries_seq", allocationSize = 1)
    private Integer id;

    @Column(nullable = false, length = 2)
    private String iso;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false, length = 80)
    private String upper_name;

    @Column(length = 3)
    private String iso3;

    @Column
    private Short num_code;

    @Column(nullable = false)
    private Integer phone_code;

    // Constructors
    public Country() {}

    public Country(Integer id, String iso, String name, String upper_name, String iso3, Short num_code, Integer phone_code) {
        this.id = id;
        this.iso = iso;
        this.name = name;
        this.upper_name = upper_name;
        this.iso3 = iso3;
        this.num_code = num_code;
        this.phone_code = phone_code;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpper_name() {
        return upper_name;
    }

    public void setUpper_name(String upper_name) {
        this.upper_name = upper_name;
    }

    public String getIso3() {
        return iso3;
    }

    public void setIso3(String iso3) {
        this.iso3 = iso3;
    }

    public Short getNum_code() {
        return num_code;
    }

    public void setNum_code(Short num_code) {
        this.num_code = num_code;
    }

    public Integer getPhone_code() {
        return phone_code;
    }

    public void setPhone_code(Integer phone_code) {
        this.phone_code = phone_code;
    }
}