package com.author.book_finder.repository;

import com.author.book_finder.entity.Book;
import com.author.book_finder.entity.User;
import com.author.book_finder.entity.Genre;
import com.author.book_finder.entity.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT COALESCE(MAX(b.volumeNumber), 0) FROM Book b WHERE b.series.seriesId = :seriesId")
    Integer findMaxVolumeBySeriesId(@Param("seriesId") Long seriesId);

    boolean existsBySeries_SeriesIdAndVolumeNumber(Long seriesId, int volumeNumber);
    boolean existsBySeries_SeriesIdAndVolumeNumberAndBookIdNot(Long seriesId, int volumeNumber, Long currentBookId);


    List<Book> findByUser(User user);
    List<Book> findBySeries(Series series);
    List<Book> findByTitleContainingIgnoreCase(String title);


}
