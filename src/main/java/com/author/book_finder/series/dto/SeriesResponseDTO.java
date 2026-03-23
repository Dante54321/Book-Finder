package com.author.book_finder.series.dto;

import java.time.LocalDate;

public class SeriesResponseDTO {

    private Long seriesId;
    private String seriesName;
    private String description;
    private LocalDate publishDate;
    private String coverUrl;

    private String authorUsername;
    private int totalBooks;

    public SeriesResponseDTO(Long seriesId,
                             String seriesName,
                             String description,
                             LocalDate publishDate,
                             String authorUsername,
                             int totalBooks,
                             String coverUrl) {
        this.seriesId = seriesId;
        this.seriesName = seriesName;
        this.description = description;
        this.publishDate = publishDate;
        this.authorUsername = authorUsername;
        this.totalBooks = totalBooks;
        this.coverUrl = coverUrl;
    }

    public Long getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(Long seriesId) {
        this.seriesId = seriesId;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public int getTotalBooks() {
        return totalBooks;
    }

    public void setTotalBooks(int totalBooks) {
        this.totalBooks = totalBooks;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}
