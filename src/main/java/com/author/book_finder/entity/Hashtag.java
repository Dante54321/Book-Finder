package com.author.book_finder.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hashtags")

public class Hashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hashtagId;

    @Column(nullable = false, unique = true)
    private String hashtag;

    //--------------------------
    // Many-to-Many relationship
    //--------------------------
    @OneToMany(mappedBy = "hashtag", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookHashtag> bookHashtags = new ArrayList<>();

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

    public List<BookHashtag> getBookHashtags() {
        return bookHashtags;
    }
    public void setBookHashtags(List<BookHashtag> bookHashtags) {
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
