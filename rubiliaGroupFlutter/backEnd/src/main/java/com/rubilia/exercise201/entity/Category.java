package com.rubilia.exercise201.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private Category parent;

    @OneToMany(mappedBy = "parent")
    @JsonIgnore // Bỏ qua subCategories để tránh lồng nhau sâu
    private List<Category> subCategories;

    @Column(name = "category_name", nullable = false, unique = true)
    private String categoryName;

    @Column(name = "category_description")
    private String categoryDescription;

    @Column
    private String icon;

    @Column
    private String image;

    @Column
    private String placeholder;

    @Column(columnDefinition = "boolean default true")
    private boolean active;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @JsonIgnore
    private StaffAccount createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    @JsonIgnore
    private StaffAccount updatedBy;

    // Constructors
    public Category() {}

    public Category(UUID id, Category parent, List<Category> subCategories, String categoryName, String categoryDescription, String icon, String image, String placeholder, boolean active, Date createdAt, Date updatedAt, StaffAccount createdBy, StaffAccount updatedBy) {
        this.id = id;
        this.parent = parent;
        this.subCategories = subCategories;
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
        this.icon = icon;
        this.image = image;
        this.placeholder = placeholder;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public List<Category> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<Category> subCategories) {
        this.subCategories = subCategories;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public StaffAccount getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(StaffAccount createdBy) {
        this.createdBy = createdBy;
    }

    public StaffAccount getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(StaffAccount updatedBy) {
        this.updatedBy = updatedBy;
    }

    // Phương thức trung gian để hỗ trợ tương thích với mã cũ (nếu cần)
    public Date getCreated_at() {
        return getCreatedAt();
    }

    public void setCreated_at(Date createdAt) {
        setCreatedAt(createdAt);
    }

    public Date getUpdated_at() {
        return getUpdatedAt();
    }

    public void setUpdated_at(Date updatedAt) {
        setUpdatedAt(updatedAt);
    }
}