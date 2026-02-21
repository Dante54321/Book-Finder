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

    // Store the S3 object key
    @Column(nullable = false, length = 512)
    private String s3Key;

    @Column(nullable = false)
    private boolean isPreview; // Preview: first chapter is free, login for full access

    @Column(nullable = false)
    private String contentType; // Content type for upload "MARKDOWN" or "HTML"

    //-------------------------
    // Many-to-One relationship
    //-------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    // Constructors
    public Chapter() {}

    public Chapter(int chapterNumber, String title, String s3Key, String contentType, boolean isPreview, Book book) {
        this.chapterNumber = chapterNumber;
        this.title = title;
        this.s3Key = s3Key;
        this.contentType = contentType;
        this.isPreview = isPreview;
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

    public String getS3Key() {
        return s3Key;
    }
    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public boolean isPreview() {
        return isPreview;
    }
    public void setIsPreview(boolean preview) {
        isPreview = preview;
    }

    public String getContentType() {
        return contentType;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
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
                '}';
    }
}
