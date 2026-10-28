package fu.edu.sba301.assignment02.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class NewsArticleRequest {
    private String newsArticleId;

    @Size(max = 400, message = "News title max 400 characters")
    private String newsTitle;

    @NotBlank(message = "Headline is required")
    @Size(max = 150, message = "Headline max 150 characters")
    private String headline;

    @Size(max = 4000, message = "News content max 4000 characters")
    private String newsContent;

    @Size(max = 400, message = "News source max 400 characters")
    private String newsSource;

    @NotNull(message = "Category is required")
    private Short categoryId;

    private Boolean newsStatus = true;

    private Short createdById;

    private Set<Integer> tagIds;

    public NewsArticleRequest() {
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

    public Short getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Short categoryId) {
        this.categoryId = categoryId;
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

    public Set<Integer> getTagIds() {
        return tagIds;
    }

    public void setTagIds(Set<Integer> tagIds) {
        this.tagIds = tagIds;
    }
}
