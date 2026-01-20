package com.gestionstock.backend.dto;

public class CategoryOrderStatsDto {
    private String categoryName;
    private Long totalQuantity;

    public CategoryOrderStatsDto(String categoryName, Long totalQuantity) {
        this.categoryName = categoryName;
        this.totalQuantity = totalQuantity;
    }

    // Getters
    public String getCategoryName() {
        return categoryName;
    }

    public Long getTotalQuantity() {
        return totalQuantity;
    }

    // Setters
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setTotalQuantity(Long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
}