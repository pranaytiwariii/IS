package com.example.auth.entity.inheritance;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract Document class demonstrating Abstract Class Inheritance
 * This abstract class defines common structure and behavior for all document types
 * Cannot be instantiated directly - must be extended by concrete classes
 */
@Entity
@Table(name = "documents")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "document_type", discriminatorType = DiscriminatorType.STRING)
public abstract class AbstractDocument {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    
    @NotBlank
    @Size(max = 200)
    protected String title;
    
    @Column(columnDefinition = "TEXT")
    protected String content;
    
    @Size(max = 100)
    protected String authorName;
    
    @Column(name = "author_id")
    protected Long authorId;
    
    @Column(name = "created_at")
    protected LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    protected LocalDateTime updatedAt;
    
    @Column(name = "published_at")
    protected LocalDateTime publishedAt;
    
    @Column(name = "is_published")
    protected Boolean isPublished = false;
    
    @ElementCollection
    @CollectionTable(name = "document_keywords", joinColumns = @JoinColumn(name = "document_id"))
    @Column(name = "keyword")
    protected Set<String> keywords = new HashSet<>();
    
    @Column(name = "view_count")
    protected Integer viewCount = 0;
    
    // Abstract methods that must be implemented by concrete classes
    
    /**
     * Abstract method to get document type
     * Each concrete class must specify its type
     */
    public abstract String getDocumentType();
    
    /**
     * Abstract method to validate document content
     * Each document type has different validation rules
     */
    public abstract boolean validateContent();
    
    /**
     * Abstract method to format document for display
     * Each document type may have different formatting
     */
    public abstract String formatForDisplay();
    
    /**
     * Abstract method to get document category
     * Used for classification and organization
     */
    public abstract String getCategory();
    
    // Concrete methods that provide common functionality
    
    // Default constructor
    public AbstractDocument() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructor with basic info
    public AbstractDocument(String title, String content, String authorName, Long authorId) {
        this();
        this.title = title;
        this.content = content;
        this.authorName = authorName;
        this.authorId = authorId;
    }
    
    // JPA lifecycle methods
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Common concrete methods available to all document types
    
    public void publish() {
        if (validateContent()) {
            this.isPublished = true;
            this.publishedAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    public void unpublish() {
        this.isPublished = false;
        this.publishedAt = null;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addKeyword(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            this.keywords.add(keyword.toLowerCase().trim());
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    public void removeKeyword(String keyword) {
        if (keyword != null) {
            this.keywords.remove(keyword.toLowerCase().trim());
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    public void incrementViewCount() {
        this.viewCount++;
        // Don't update updatedAt for view count changes
    }
    
    public boolean isContentValid() {
        return title != null && !title.trim().isEmpty() && 
               content != null && !content.trim().isEmpty();
    }
    
    public String getStatus() {
        if (!isContentValid()) return "Invalid";
        if (isPublished) return "Published";
        return "Draft";
    }
    
    public int getContentWordCount() {
        if (content == null) return 0;
        return content.split("\\s+").length;
    }
    
    public String getSummary() {
        if (content == null || content.length() <= 200) {
            return content;
        }
        return content.substring(0, 200) + "...";
    }
    
    // Template method pattern - defines algorithm structure, subclasses implement details
    public final String getCompleteInfo() {
        return String.format("""
            === %s DOCUMENT ===
            Title: %s
            Author: %s (ID: %d)
            Type: %s | Category: %s
            Status: %s | Views: %d
            Created: %s | Updated: %s
            %s
            Keywords: %s
            Content Summary: %s
            
            %s
            """,
            getDocumentType().toUpperCase(), // Abstract method
            title,
            authorName,
            authorId,
            getDocumentType(), // Abstract method
            getCategory(), // Abstract method
            getStatus(),
            viewCount,
            createdAt.toLocalDate(),
            updatedAt.toLocalDate(),
            isPublished ? "Published: " + publishedAt.toLocalDate() : "Not published",
            keywords.isEmpty() ? "None" : String.join(", ", keywords),
            getSummary(),
            formatForDisplay() // Abstract method
        );
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { 
        this.title = title; 
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getContent() { return content; }
    public void setContent(String content) { 
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    
    public Boolean getIsPublished() { return isPublished; }
    public void setIsPublished(Boolean isPublished) { this.isPublished = isPublished; }
    
    public Set<String> getKeywords() { return keywords; }
    public void setKeywords(Set<String> keywords) { this.keywords = keywords; }
    
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
}