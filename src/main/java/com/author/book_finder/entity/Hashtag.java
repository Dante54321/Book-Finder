package com.author.book_finder.entity;

import com.author.book_finder.book.entity.Book;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "hashtags")
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hashtagId;

    @Column(nullable = false, unique = true, updatable = false)
    private String hashtag;

    @ManyToMany(mappedBy = "hashtags")
    private Set<Book> books = new HashSet<>();

    public Hashtag() {}

    public Hashtag(String hashtag) {
        this.hashtag = hashtag.trim().toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hashtag that = (Hashtag) o;
        return hashtagId != null && hashtagId.equals(that.hashtagId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // Getters and Setters
    public Long getHashtagId() {
        return hashtagId;
    }
    public void setHashtagId(Long hashtagId) {
        this.hashtagId = hashtagId;
    }

    public String getHashtag() {
        return hashtag;
    }
    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public Set<Book> getBooks() {
        return books;
    }
    public void setBooks(Set<Book> books) {
        this.books = books;
    }


    @Override
    public String toString() {
        return "Hashtag{" +
                "hashtagId=" + hashtagId +
                ", tagText='" + hashtag + '\'' +
                '}';
    }
}
