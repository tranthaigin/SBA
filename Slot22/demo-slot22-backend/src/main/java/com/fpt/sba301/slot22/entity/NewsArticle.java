package com.fpt.sba301.slot22.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "NewsArticle")
public class NewsArticle {
    @Id
    @Column(name = "NewsArticleID", length = 20, columnDefinition = "nvarchar(20)")
    private String newsArticleId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CategoryID", nullable = false)
    private Category category;

    public NewsArticle() {
    }

    public String getNewsArticleId() { return newsArticleId; }
    public void setNewsArticleId(String newsArticleId) { this.newsArticleId = newsArticleId; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}
