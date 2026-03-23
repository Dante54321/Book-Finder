package com.author.book_finder.readingprogress.dto;

import java.time.LocalDateTime;

public class CurrentlyReadingResponseDTO {

    private Long bookId;
    private String title;
    private String authorUsername;
    private String coverUrl;
    private LocalDateTime lastReadAt;

    public CurrentlyReadingResponseDTO() {
    }

    public CurrentlyReadingResponseDTO(Long bookId,
                                       String title,
                                       String authorUsername,
                                       String coverUrl,
                                       LocalDateTime lastReadAt) {
        this.bookId = bookId;
        this.title = title;
        this.authorUsername = authorUsername;
        this.coverUrl = coverUrl;
        this.lastReadAt = lastReadAt;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public LocalDateTime getLastReadAt() {
        return lastReadAt;
    }

    public void setLastReadAt(LocalDateTime lastReadAt) {
        this.lastReadAt = lastReadAt;
    }
}