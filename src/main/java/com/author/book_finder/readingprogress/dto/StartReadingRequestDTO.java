package com.author.book_finder.readingprogress.dto;

public class StartReadingRequestDTO {

    private Long bookId;

    public StartReadingRequestDTO() {
    }

    public StartReadingRequestDTO(Long bookId) {
        this.bookId = bookId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
}