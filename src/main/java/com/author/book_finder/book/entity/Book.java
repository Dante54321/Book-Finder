package com.author.book_finder.book.entity;

import com.author.book_finder.chapter.entity.Chapter;
import com.author.book_finder.genre.entity.Genre;
import com.author.book_finder.hashtag.entity.Hashtag;
import com.author.book_finder.review.entity.Review;
import com.author.book_finder.series.entity.Series;
import com.author.book_finder.user.entity.User;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(
        name = "books",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"series_id", "volume_number"}
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

    @Column(length = 2400)
    private String summary;

    @Column(nullable = false)
    private LocalDate publishDate;

    @Column(name = "cover_image_key", length = 512)
    private String coverImageKey;

    // ---------------------------
    // Many-to-One relationships
    // ---------------------------

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id")
    private Series series;

    // ---------------------------
    // Many-to-Many relationships
    // ---------------------------

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_genres",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_hashtags",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "hashtag_id")
    )
    private Set<Hashtag> hashtags = new HashSet<>();

    // ---------------------------
    // One-to-Many relationships
    // ---------------------------

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Review> reviews = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("chapterNumber ASC")
    private List<Chapter> chapters = new ArrayList<>();



    // ---------------------------
    // Constructors
    // ---------------------------

    public Book() {}

    public Book(Integer volumeNumber,
                String title,
                String summary,
                LocalDate publishDate,
                User user,
                Series series,
                Set<Genre> genres) {

        this.volumeNumber = volumeNumber;
        this.title = title;
        this.summary = summary;
        this.publishDate = publishDate;
        this.user = user;
        this.series = series;
        this.genres = (genres != null) ? genres : new HashSet<>();
    }

    // ---------------------------
    // Helper Methods
    // ---------------------------
    public void addChapter(Chapter chapter) {
        chapters.add(chapter);
        chapter.setBook(this);
    }

    public void removeChapter(Chapter chapter) {
        chapters.remove(chapter);
        chapter.setBook(null);
    }

    public void addReview(Review review) {
        reviews.add(review);
        review.setBook(this);
    }

    public void removeReview(Review review) {
        reviews.remove(review);
        review.setBook(null);
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
        genre.getBooks().add(this);
    }

    public void removeGenre(Genre genre) {
        genres.remove(genre);
        genre.getBooks().remove(this);
    }

    public void addHashtag(Hashtag hashtag) {
        hashtags.add(hashtag);
        hashtag.getBooks().add(this);
    }

    public void removeHashtag(Hashtag hashtag) {
        hashtags.remove(hashtag);
        hashtag.getBooks().remove(this);
    }

    // -----------------------
    // Equals & HashCode
    // -----------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return bookId != null && bookId.equals(book.bookId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // ---------------------------
    // Getters and Setters
    // ---------------------------

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

    public void setCoverImageKey (String coverImageKey) {
        this.coverImageKey = coverImageKey;
    }

    public String getCoverImageKey() {
        return coverImageKey;
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
        return Collections.unmodifiableSet(genres);
    }

    public void replaceGenres(Set<Genre> genres) {
        this.genres.clear();
        if (genres != null) {
            genres.forEach(this::addGenre);
        }
    }

    public Set<Hashtag> getHashtags() {
        return Collections.unmodifiableSet(hashtags);
    }

    public void replaceHashtags(Set<Hashtag> hashtags) {
        this.hashtags.clear();
        if (hashtags != null) {
            hashtags.forEach(this::addHashtag);
        }
    }

    public Set<Review> getReviews() {
        return Collections.unmodifiableSet(reviews);
    }

    public List<Chapter> getChapters() {
        return Collections.unmodifiableList(chapters);
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
