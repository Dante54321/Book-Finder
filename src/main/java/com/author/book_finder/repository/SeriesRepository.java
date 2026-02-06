package com.author.book_finder.repository;

import com.author.book_finder.entity.Series;
import com.author.book_finder.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {

    List<Series> findByUser(User user);

    List<Series> findBySeriesNameContainingIgnoreCase(String name);

}
