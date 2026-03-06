package com.author.book_finder.book.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

public class BookCreateRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must be under 150 characters")
    private String title;

    @NotBlank(message = "Summary is required")
    @Size(max = 2400, message = "Summary must be under 2400 characters")
    private String summary;

    @NotNull(message = "Publish date is required")
    private LocalDate publishDate;

    private Long seriesId;

    @NotEmpty(message = "At least one genre must be selected")
    private Set<Long> genreIds;
    private Set<String> hashtags;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public Long getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(Long seriesId) {
        this.seriesId = seriesId;
    }

    public Set<Long> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(Set<Long> genreIds) {
        this.genreIds = genreIds;
    }

    public Set<String> getHashtags() {
        return hashtags;
    }

    public void setHashtagIds(Set<String> hashtagIds) {
        this.hashtags = hashtagIds;
    }
}
