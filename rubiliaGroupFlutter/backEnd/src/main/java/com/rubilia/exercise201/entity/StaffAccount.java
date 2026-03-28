package com.rubilia.exercise201.entity;

import java.util.List;
import jakarta.persistence.*;
import java.util.Date;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "staff_accounts")
public class StaffAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    @JsonIgnore
    private Role role;

    @Column(nullable = true)
    private String first_name;

    @Column(nullable = true)
    private String last_name;

    @Column(nullable = true)
    private String phone_number;

    @Column(nullable = true)
    private String email;

    @Column(nullable = true)
    private String password_hash;

    @Column(nullable = true)
    private String user_name;

    @Column(columnDefinition = "tinyint(1) default 1")
    private boolean active;

    @Column(nullable = true)
    private String image;

    @Column(nullable = true)
    private String placeholder;

    @Column(nullable = false)
    private Date created_at;

    @Column(nullable = false)
    private Date updated_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private StaffAccount createdBy;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<StaffAccount> subCreatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private StaffAccount updatedBy;

    @OneToMany(mappedBy = "updatedBy", cascade = CascadeType.ALL)
    private List<StaffAccount> subUpdatedBy;

    // Constructors
    public StaffAccount() {}

    public StaffAccount(UUID id, Role role, String first_name, String last_name, String phone_number, String email, String password_hash, String user_name, boolean active, String image, String placeholder, Date created_at, Date updated_at, StaffAccount createdBy, List<StaffAccount> subCreatedBy, StaffAccount updatedBy, List<StaffAccount> subUpdatedBy) {
        this.id = id;
        this.role = role;
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone_number = phone_number;
        this.email = email;
        this.password_hash = password_hash;
        this.user_name = user_name;
        this.active = active;
        this.image = image;
        this.placeholder = placeholder;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.createdBy = createdBy;
        this.subCreatedBy = subCreatedBy;
        this.updatedBy = updatedBy;
        this.subUpdatedBy = subUpdatedBy;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public StaffAccount getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(StaffAccount createdBy) {
        this.createdBy = createdBy;
    }

    public List<StaffAccount> getSubCreatedBy() {
        return subCreatedBy;
    }

    public void setSubCreatedBy(List<StaffAccount> subCreatedBy) {
        this.subCreatedBy = subCreatedBy;
    }

    public StaffAccount getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(StaffAccount updatedBy) {
        this.updatedBy = updatedBy;
    }

    public List<StaffAccount> getSubUpdatedBy() {
        return subUpdatedBy;
    }

    public void setSubUpdatedBy(List<StaffAccount> subUpdatedBy) {
        this.subUpdatedBy = subUpdatedBy;
    }
}