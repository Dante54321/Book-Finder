package com.author.book_finder.entity;

import com.author.book_finder.book.entity.Book;
import jakarta.persistence.*;
import com.author.book_finder.enums.ContentType;

@Entity
@Table(
        name = "chapters",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"book_id", "chapter_number"}
        )
)
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chapterId;

    @Column(nullable = false)
    private int chapterNumber;

    @Column(nullable = false)
    private String title;

    // Store the S3 object key
    @Column(nullable = false)
    private String s3Key;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType contentType;

    public boolean isPreview() {
        return this.chapterNumber == 1;
    }

    // -------------------------
    // Many-to-One relationship
    // -------------------------

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    // -------------------------
    // Constructors
    // -------------------------

    public Chapter() {}

    public Chapter(int chapterNumber,
                   String title,
                   String s3Key,
                   ContentType contentType) {
        this.chapterNumber = chapterNumber;
        this.title = title;
        this.s3Key = s3Key;
        this.contentType = contentType;
    }

    // ----------------------
    // Equals & HashCode
    // ----------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chapter chapter = (Chapter) o;
        return chapterId != null && chapterId.equals(chapter.chapterId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // -------------------------
    // Getters and Setters
    // -------------------------

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

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
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
