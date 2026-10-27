package fu.edu.sba301.assignment02.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryRequest {
    @NotBlank(message = "Category name cannot be blank")
    @Size(max = 100, message = "Category name maximum 100 characters")
    private String categoryName;

    @NotBlank(message = "Category description cannot be blank")
    @Size(max = 250, message = "Category description maximum 250 characters")
    private String categoryDesciption;

    private Short parentCategoryId;
    private Boolean isActive = true;

    public CategoryRequest() {
    }

    public CategoryRequest(String categoryName, String categoryDesciption, Short parentCategoryId, Boolean isActive) {
        this.categoryName = categoryName;
        this.categoryDesciption = categoryDesciption;
        this.parentCategoryId = parentCategoryId;
        this.isActive = isActive;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDesciption() {
        return categoryDesciption;
    }

    public void setCategoryDesciption(String categoryDesciption) {
        this.categoryDesciption = categoryDesciption;
    }

    public Short getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Short parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
