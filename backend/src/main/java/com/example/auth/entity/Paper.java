package com.example.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "papers")
@Schema(description = "Research paper entity")
public class Paper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the paper", example = "1")
    private Long id;

    @NotBlank
    @Size(max = 200)
    @Schema(description = "Paper title", example = "Introduction to Machine Learning")
    private String title;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    @Schema(description = "Paper abstract", example = "This paper provides a comprehensive introduction to machine learning concepts...")
    private String abstractText;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    @Schema(description = "Paper content", example = "Machine learning is a subset of artificial intelligence...")
    private String content;

    @Column(name = "publication_date")
    @Schema(description = "Publication date", example = "2023-12-01T10:00:00")
    private LocalDateTime publicationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "published_by_committee_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User publishedByCommittee;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "paper_tags",
            joinColumns = @JoinColumn(name = "paper_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Set<Tag> tags = new HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Paper() {
    }

    public Paper(String title, String abstractText, String content, User author) {
        this.title = title;
        this.abstractText = abstractText;
        this.content = content;
        this.author = author;
    }

    // Getters and Setters
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

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDateTime publicationDate) {
        this.publicationDate = publicationDate;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public User getPublishedByCommittee() {
        return publishedByCommittee;
    }

    public void setPublishedByCommittee(User publishedByCommittee) {
        this.publishedByCommittee = publishedByCommittee;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
