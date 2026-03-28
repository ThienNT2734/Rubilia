package com.rubilia.exercise201.dto;

import java.util.UUID;

public class ProductCategoryDTO {
    private UUID id;
    private CategoryDTO category;

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }
}