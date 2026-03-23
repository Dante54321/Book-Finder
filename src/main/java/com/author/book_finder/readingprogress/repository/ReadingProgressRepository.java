package com.author.book_finder.readingprogress.repository;

import com.author.book_finder.readingprogress.entity.ReadingProgress;
import com.author.book_finder.enums.ReadingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import com.author.book_finder.enums.PublicationStatus;

public interface ReadingProgressRepository extends JpaRepository<ReadingProgress, Long> {

    Optional<ReadingProgress> findByUser_UserIdAndBook_BookId(Long userId, Long bookId);

    List<ReadingProgress> findByUser_UserIdAndStatusOrderByLastReadAtDesc(Long userId, ReadingStatus status);
}