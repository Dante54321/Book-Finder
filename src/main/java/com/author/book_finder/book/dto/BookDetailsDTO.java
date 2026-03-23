package com.author.book_finder.book.dto;

import java.time.LocalDate;
import java.util.Set;

public class BookDetailsDTO {

    private Long bookId;
    private Integer volumeNumber;
    private String title;
    private String summary;
    private LocalDate publishDate;
    private String authorUsername;
    private String seriesName;
    private Set<String> genres;
    private Set<String> hashtags;
    private String coverUrl;

    public BookDetailsDTO(Long bookId,
                          Integer volumeNumber,
                          String title,
                          String summary,
                          LocalDate publishDate,
                          String authorUsername,
                          String seriesName,
                          Set<String> genres,
                          Set<String> hashtags,
                          String coverUrl) {
        this.bookId = bookId;
        this.volumeNumber = volumeNumber;
        this.title = title;
        this.summary = summary;
        this.publishDate = publishDate;
        this.authorUsername = authorUsername;
        this.seriesName = seriesName;
        this.genres = genres;
        this.hashtags = hashtags;
        this.coverUrl = coverUrl;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Integer getVolumeNumber() {
        return volumeNumber;
    }

    public void setVolumeNumber(Integer volumeNumber) {
        this.volumeNumber = volumeNumber;
    }

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

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public Set<String> getGenres() {
        return genres;
    }

    public void setGenres(Set<String> genres) {
        this.genres = genres;
    }

    public Set<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(Set<String> hashtags) {
        this.hashtags = hashtags;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}