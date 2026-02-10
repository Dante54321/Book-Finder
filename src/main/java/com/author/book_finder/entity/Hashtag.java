package com.author.book_finder.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "hashtags")

public class Hashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hashtagId;

    @Column(nullable = false, unique = true)
    private String hashtag;

    //--------------------------
    // One-to-Many relationship
    //--------------------------
    @OneToMany(mappedBy = "hashtag", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookHashtag> bookHashtags = new HashSet<>();

    // Constructors
    public Hashtag() {}

    public Hashtag(String hashtag) {
        this.hashtag = hashtag;
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

    public Set<BookHashtag> getBookHashtags() {
        return bookHashtags;
    }
    public void setBookHashtags(Set<BookHashtag> bookHashtags) {
        this.bookHashtags = bookHashtags;
    }

    @Override
    public String toString() {
        return "Hashtag{" +
                "hashtagId=" + hashtagId +
                ", tagText='" + hashtag + '\'' +
                '}';
    }
}
