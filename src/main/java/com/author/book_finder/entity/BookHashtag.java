package com.author.book_finder.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "book_hashtags",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"book_id", "hashtag_id"}
        )
)
public class BookHashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookHashtagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id", nullable = false)
    private Hashtag hashtag;

    public BookHashtag() {}

    public BookHashtag(Book book, Hashtag hashtag) {
        this.book = book;
        this.hashtag = hashtag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookHashtag that = (BookHashtag) o;
        return bookHashtagId != null && bookHashtagId.equals(that.bookHashtagId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
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
                ", book=" + (book != null ? book.getBookId() : null) +
                ", hashtag=" + (hashtag != null ? hashtag.getHashtagId() : null) +
                '}';
    }
}
