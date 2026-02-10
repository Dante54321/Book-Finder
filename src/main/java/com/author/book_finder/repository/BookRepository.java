package com.author.book_finder.repository;

import com.author.book_finder.entity.Book;
import com.author.book_finder.entity.User;
import com.author.book_finder.entity.Genre;
import com.author.book_finder.entity.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByUser(User user);
    List<Book> findBySeries(Series series);
    List<Book> findByGenre(Genre genre);
    List<Book> findByTitleContainingIgnoreCase(String title);


}
