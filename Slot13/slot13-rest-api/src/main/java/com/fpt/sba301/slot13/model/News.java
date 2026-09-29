package com.fpt.sba301.slot13.model;

public class News {
    private Long id;
    private String title;
    private String content;
    private Long categoryId;
    private boolean active;

    public News() {
    }

    public News(Long id, String title, String content, Long categoryId, boolean active) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.categoryId = categoryId;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
