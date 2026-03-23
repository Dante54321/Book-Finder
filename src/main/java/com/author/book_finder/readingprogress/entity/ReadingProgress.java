package com.author.book_finder.readingprogress.entity;

import com.author.book_finder.book.entity.Book;
import com.author.book_finder.enums.ReadingStatus;
import com.author.book_finder.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "reading_progress",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_reading_progress_user_book",
                        columnNames = {"user_id", "book_id"}
                )
        }
)
public class ReadingProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reading_progress_id")
    private Long readingProgressId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReadingStatus status;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "last_read_at", nullable = false)
    private LocalDateTime lastReadAt;

    public ReadingProgress() {
    }

    public ReadingProgress(User user, Book book, ReadingStatus status, LocalDateTime startedAt, LocalDateTime lastReadAt) {
        this.user = user;
        this.book = book;
        this.status = status;
        this.startedAt = startedAt;
        this.lastReadAt = lastReadAt;
    }

    public Long getReadingProgressId() {
        return readingProgressId;
    }

    public void setReadingProgressId(Long readingProgressId) {
        this.readingProgressId = readingProgressId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public ReadingStatus getStatus() {
        return status;
    }

    public void setStatus(ReadingStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getLastReadAt() {
        return lastReadAt;
    }

    public void setLastReadAt(LocalDateTime lastReadAt) {
        this.lastReadAt = lastReadAt;
    }
}