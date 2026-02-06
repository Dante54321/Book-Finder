package com.author.book_finder.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "book_hashtags")

public class BookHashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookHashtagId;

    //--------------------------
    // Many-to-one relationships
    //--------------------------
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "hashtag_id", nullable = false)
    private Hashtag hashtag;

    // Constructors
    public BookHashtag() {}

    public BookHashtag(Book book, Hashtag hashtag) {
        this.book = book;
        this.hashtag = hashtag;
    }

    // Getters and Setters
    public Long getBookHashtagId() {
        return bookHashtagId;
    }
    public void setBookHashtagId(Long bookHashtagId) {
        this.bookHashtagId = bookHashtagId;
    }

    public Book getBook() {
        return book;
    }
    public void setBook(Book book) {
        this.book = book;
    }

    public Hashtag getHashtag() {
        return hashtag;
    }
    public void setHashtag(Hashtag hashtag) {
        this.hashtag = hashtag;
    }

    @Override
    public String toString() {
        return "BookHashtag{" +
                "id=" + bookHashtagId +
                ", book=" + (book != null ? book.getTitle() : null) +
                ", hashtag=" + (hashtag != null ? hashtag.getHashtag() : null) +
                '}';
    }
}
