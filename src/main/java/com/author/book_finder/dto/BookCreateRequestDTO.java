package com.author.book_finder.dto;

import java.time.LocalDate;
import java.util.Set;

public class BookCreateRequestDTO {

    private String title;
    private String summary;
    private LocalDate publishDate;

    private Long seriesId;

    private Set<Long> genreIds;
    private Set<Long> hashtagIds;


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

    public Set<Long> getHashtagIds() {
        return hashtagIds;
    }

    public void setHashtagIds(Set<Long> hashtagIds) {
        this.hashtagIds = hashtagIds;
    }
}
