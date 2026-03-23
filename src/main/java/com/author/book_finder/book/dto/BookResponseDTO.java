package com.author.book_finder.book.dto;

import com.author.book_finder.enums.PublicationStatus;

import java.time.LocalDate;

public class BookResponseDTO {

    private Long bookId;
    private Integer volumeNumber;
    private String title;
    private LocalDate publishDate;
    private String authorUsername;
    private String seriesName;
    private String coverUrl;
    private PublicationStatus publicationStatus;

    public BookResponseDTO(Long bookId,
                           Integer volumeNumber,
                           String title,
                           LocalDate publishDate,
                           String authorUsername,
                           String seriesName,
                           String coverUrl,
                           PublicationStatus publicationStatus) {
        this.bookId = bookId;
        this.volumeNumber = volumeNumber;
        this.title = title;
        this.publishDate = publishDate;
        this.authorUsername = authorUsername;
        this.seriesName = seriesName;
        this.coverUrl = coverUrl;
        this.publicationStatus = publicationStatus;

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

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public PublicationStatus getPublicationStatus() {
        return publicationStatus;
    }

    public void setPublicationStatus(PublicationStatus publicationStatus) {
        this.publicationStatus = publicationStatus;
    }
}
