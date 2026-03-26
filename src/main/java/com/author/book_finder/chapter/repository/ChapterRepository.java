package com.author.book_finder.chapter.repository;

import com.author.book_finder.chapter.entity.Chapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    boolean existsByBookBookIdAndChapterNumber(Long bookId, int chapterNumber);

    boolean existsByBookBookIdAndChapterNumberAndChapterIdNot(Long bookId, int chapterNumber, Long chapterId);

    Page<Chapter> findByBookBookIdOrderByChapterNumberAsc(Long bookId, Pageable pageable);

    List<Chapter> findByBookBookIdOrderByChapterNumberAsc(Long bookId);
}