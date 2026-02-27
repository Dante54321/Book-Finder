package com.author.book_finder.repository;

import com.author.book_finder.entity.Review;
import com.author.book_finder.book.entity.Book;
import com.author.book_finder.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByUser(User user);
    List<Review> findByBook(Book book);

}
