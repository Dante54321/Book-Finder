package com.author.book_finder.repository;

import com.author.book_finder.entity.Chapter;
import com.author.book_finder.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {


    boolean existsByBookIdAndChapterNumber(Long bookId, int chapterNumber);
    Page<Chapter> findByBookBookIdOrderByChapterNumberAsc(Long bookId, Pageable pageable);
}


