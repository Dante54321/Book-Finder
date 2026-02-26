package com.author.book_finder.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "books",
    uniqueConstraints = @UniqueConstraint(
            columnNames = {"series_id","volume_number"}
    )
)

public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @Column
    private Integer volumeNumber;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String summary;

    @Column(nullable = false)
    private LocalDate publishDate;

    //---------------------------
    // Many-to-One relationships
    //---------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
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
    private Set<Genre> genres = new HashSet<>();

    //---------------------------
    // One-to-Many relationships
    //---------------------------
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Review> reviews = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("chapterNumber ASC")
    private List<Chapter> chapters = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookHashtag> bookHashtags = new HashSet<>();


    // Constructors
    public Book() {}

    public Book(Integer volumeNumber, String title, String summary, LocalDate publishDate, User user, Series series, Set<Genre> genres){
        this.volumeNumber = volumeNumber;
        this.title = title;
        this.summary = summary;
        this.publishDate = publishDate;
        this.user = user;
        this.series = series;
        this.genres = genres;
    }

    // ADD HASHTAG HELPER METHOD
    public void addHashtag(Hashtag hashtag) {
        BookHashtag bookHashtag = new BookHashtag(this, hashtag);
        bookHashtags.add(bookHashtag);
    }

    // REMOVE HASHTAG HELPER METHOD
    public void removeHashtag(BookHashtag bookHashtag) {
        bookHashtags.remove(bookHashtag);
    }

    // Getters and Setters
    public Long getBookId() {
        return bookId;
    }
    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Integer getVolumeNumber() {
        return volumeNumber;
    }
    public void setVolumeNumber(Integer volumeNumber) {
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
    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    public Set<Review> getReviews() {
        return reviews;
    }
    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }
    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    public Set<BookHashtag> getBookHashtags() {
        return bookHashtags;
    }
    public void setBookHashtags(Set<BookHashtag> bookHashtags) {
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
