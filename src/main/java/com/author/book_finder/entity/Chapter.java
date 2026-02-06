package com.author.book_finder.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "chapters")

public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chapterId;

    @Column(nullable = false)
    private int chapterNumber;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String contentUrl;

    //-------------------------
    // Many-to-One relationship
    //-------------------------
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    // Constructors
    public Chapter() {}

    public Chapter(int chapterNumber, String title, String contentUrl, Book book) {
        this.chapterNumber = chapterNumber;
        this.title = title;
        this.contentUrl = contentUrl;
        this.book = book;
    }

    // Getters and Setters
    public Long getChapterId() {
        return chapterId;
    }
    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }
    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentUrl() {
        return contentUrl;
    }
    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public Book getBook() {
        return book;
    }
    public void setBook(Book book) {
        this.book = book;
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "chapterId=" + chapterId +
                ", chapterNumber=" + chapterNumber +
                ", title='" + title + '\'' +
                ", book=" + (book != null ? book.getTitle() : null) +
                '}';
    }
}
