package fu.edu.sba301.assignment02.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "NewsArticle")
public class NewsArticle implements Serializable {

    @Id
    @Column(name = "NewsArticleID", length = 20)
    private String newsArticleId;

    @Column(name = "NewsTitle", length = 400)
    private String newsTitle;

    @Column(name = "Headline", nullable = false, length = 150)
    private String headline;

    @Column(name = "CreatedDate")
    private LocalDateTime createdDate;

    @Column(name = "NewsContent", length = 4000)
    private String newsContent;

    @Column(name = "NewsSource", length = 400)
    private String newsSource;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CategoryID")
    private Category category;

    @Column(name = "NewsStatus")
    private Boolean newsStatus = true;

    @Column(name = "CreatedByID")
    private Short createdById;

    @Column(name = "UpdatedByID")
    private Short updatedById;

    @Column(name = "ModifiedDate")
    private LocalDateTime modifiedDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "NewsTag",
            joinColumns = @JoinColumn(name = "NewsArticleID"),
            inverseJoinColumns = @JoinColumn(name = "TagID")
    )
    private Set<Tag> tags = new HashSet<>();

    public NewsArticle() {
    }

    public String getNewsArticleId() {
        return newsArticleId;
    }

    public void setNewsArticleId(String newsArticleId) {
        this.newsArticleId = newsArticleId;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getNewsContent() {
        return newsContent;
    }

    public void setNewsContent(String newsContent) {
        this.newsContent = newsContent;
    }

    public String getNewsSource() {
        return newsSource;
    }

    public void setNewsSource(String newsSource) {
        this.newsSource = newsSource;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Boolean getNewsStatus() {
        return newsStatus;
    }

    public void setNewsStatus(Boolean newsStatus) {
        this.newsStatus = newsStatus;
    }

    public Short getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Short createdById) {
        this.createdById = createdById;
    }

    public Short getUpdatedById() {
        return updatedById;
    }

    public void setUpdatedById(Short updatedById) {
        this.updatedById = updatedById;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
}
