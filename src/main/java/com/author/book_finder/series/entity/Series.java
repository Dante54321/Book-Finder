package com.author.book_finder.series.entity;

import com.author.book_finder.book.entity.Book;
import com.author.book_finder.user.entity.User;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "series")
public class Series {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seriesId;

    @Column(nullable = false)
    private String seriesName;

    @Column(length = 1600)
    private String description;

    @Column(nullable = false)
    private LocalDate publishDate;

    // -----------------------------
    // Many-to-One relationship
    // -----------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // -----------------------------
    // One-to-Many relationship
    // -----------------------------
    @OneToMany(mappedBy ="series", cascade = CascadeType.ALL)
    @OrderBy("volumeNumber ASC")
    private List<Book> books = new ArrayList<>();

    // Constructors
    public Series() {}

    public Series(String seriesName, String description, LocalDate publishDate, User user) {
        this.seriesName = seriesName;
        this.description = description;
        this.publishDate = publishDate;
        this.user = user;
    }

    // -----------------------------
    // Helper Methods
    // -----------------------------
    public void addBook(Book book) {
        books.add(book);
        book.setSeries(this);
    }

    public void removeBook(Book book) {
        books.remove(book);
        book.setSeries(null);
    }

    // -----------------------------
    // Equals & HashCode
    // -----------------------------
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Series series = (Series) o;
        return seriesId != null && seriesId.equals(series.seriesId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // Getters and Setters
    public Long getSeriesId() {
        return seriesId;
    }
    public void setSeriesId(Long seriesId) {
        this.seriesId = seriesId;
    }

    public String getSeriesName() {
        return seriesName;
    }
    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
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

    public List<Book> getBooks() {
        return books;
    }


    @Override
    public String toString() {
        return "Series{" +
                "seriesId=" + seriesId +
                ", title='" + seriesName + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + publishDate +
                ", user=" + (user != null ? user.getUserId() : null) +
                '}';
    }
}


