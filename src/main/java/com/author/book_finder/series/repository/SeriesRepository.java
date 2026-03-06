package com.author.book_finder.series.repository;

import com.author.book_finder.series.entity.Series;
import com.author.book_finder.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {

    Page<Series> findAll(Pageable pageable);

    Page<Series> findByUser_UserId(Long userUserId, Pageable pageable);

    List<Series> findByUser(User user);

    List<Series> findBySeriesNameContainingIgnoreCase(String name);

}
