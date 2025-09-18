package com.example.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Schema(description = "Paper creation request payload")
public class PaperRequest {
    @NotBlank
    @Size(max = 200)
    @Schema(description = "Paper title", example = "Introduction to Machine Learning", required = true)
    private String title;

    @NotBlank
    @Schema(description = "Paper abstract", example = "This paper provides a comprehensive introduction to machine learning concepts...", required = true)
    private String abstractText;

    @NotBlank
    @Schema(description = "Paper content", example = "Machine learning is a subset of artificial intelligence...", required = true)
    private String content;

    @Schema(description = "List of tags associated with the paper", example = "[\"machine learning\", \"AI\", \"algorithms\"]")
    private List<String> tags;

    public PaperRequest() {
    }

    public PaperRequest(String title, String abstractText, String content, List<String> tags) {
        this.title = title;
        this.abstractText = abstractText;
        this.content = content;
        this.tags = tags;
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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
