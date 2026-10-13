package com.fpt.sba301.slot22.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CategoryID")
    private Short categoryId;

    @Column(name = "CategoryName", nullable = false, length = 100, columnDefinition = "nvarchar(100)")
    private String categoryName;

    // The database column is intentionally spelled CategoryDesciption.
    @Column(name = "CategoryDesciption", nullable = false, length = 250, columnDefinition = "nvarchar(250)")
    private String categoryDescription;

    @Column(name = "ParentCategoryID")
    private Short parentCategoryId;

    @Column(name = "IsActive")
    private Boolean active;

    public Category() {
    }

    public Short getCategoryId() { return categoryId; }
    public void setCategoryId(Short categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getCategoryDescription() { return categoryDescription; }
    public void setCategoryDescription(String categoryDescription) { this.categoryDescription = categoryDescription; }
    public Short getParentCategoryId() { return parentCategoryId; }
    public void setParentCategoryId(Short parentCategoryId) { this.parentCategoryId = parentCategoryId; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
