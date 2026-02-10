package com.author.book_finder.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "books")

public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @Column(nullable = false)
    private int volumeNumber;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String summary;

    @Column(nullable = false)
    private LocalDate publishDate;

    //---------------------------
    // Many-to-One relationships
    //---------------------------
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "series_id")
    private Series series;

    //---------------------------
    // Many-to-Many relationships
    //---------------------------
    @ManyToMany
    @JoinTable(
            name = "book_genres",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")

    )
    private Set<Genre> genres;

    //---------------------------
    // One-to-Many relationships
    //---------------------------
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chapter> chapters = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookHashtag> bookHashtags = new ArrayList<>();


    // Constructors
    public Book() {}

    public Book(int volumeNumber, String title, String summary, LocalDate publishDate, User user, Series series, Set<Genre> genres){
        this.volumeNumber = volumeNumber;
        this.title = title;
        this.summary = summary;
        this.publishDate = publishDate;
        this.user = user;
        this.series = series;
        this.genres = genres;
    }

    // Getters and Setters
    public Long getBookId() {
        return bookId;
    }
    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public int getVolumeNumber() {
        return volumeNumber;
    }
    public void setVolumeNumber(int volumeNumber) {
        this.volumeNumber = volumeNumber;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }
    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Series getSeries() {
        return series;
    }
    public void setSeries(Series series) {
        this.series = series;
    }

    public Set<Genre> getGenres() {
        return genres;
    }
    public void setGenre(Set<Genre> genres) {
        this.genres = genres;
    }

    public List<Review> getReviews() {
        return reviews;
    }
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }
    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    public List<BookHashtag> getBookHashtags() {
        return bookHashtags;
    }
    public void setBookHashtags(List<BookHashtag> bookHashtags) {
        this.bookHashtags = bookHashtags;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", volumeNumber=" + volumeNumber +
                ", title='" + title + '\'' +
                ", publishDate=" + publishDate +
                '}';
    }

}
