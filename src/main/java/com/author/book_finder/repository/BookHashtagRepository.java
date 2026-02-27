package com.author.book_finder.repository;

import com.author.book_finder.entity.BookHashtag;
import com.author.book_finder.entity.Hashtag;
import com.author.book_finder.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookHashtagRepository extends JpaRepository<BookHashtag, Long> {

    List<BookHashtag> findByHashtag(Hashtag hashtag);
    List<BookHashtag> findByBook(Book book);

}
