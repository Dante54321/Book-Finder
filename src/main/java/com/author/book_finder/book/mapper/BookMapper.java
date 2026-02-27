package com.author.book_finder.book.mapper;

import com.author.book_finder.book.dto.BookDetailsDTO;
import com.author.book_finder.book.dto.BookResponseDTO;
import com.author.book_finder.book.entity.Book;
import com.author.book_finder.entity.Genre;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BookMapper {

    public BookResponseDTO toResponseDTO(Book book) {

        return new BookResponseDTO(
                book.getBookId(),
                book.getVolumeNumber(),
                book.getTitle(),
                book.getPublishDate(),
                book.getUser().getUsername(),
                book.getSeries() != null
                        ? book.getSeries().getSeriesName()
                        : null
        );
    }

    public BookDetailsDTO toDetailsDTO(Book book) {

        Set<String> genreNames = book.getGenres() == null
                ? Set.of()
                : book.getGenres()
                .stream()
                .map(Genre::getGenreName)
                .collect(Collectors.toSet());

        Set<String> hashtagNames = book.getBookHashtags() == null
                ? Set.of()
                : book.getBookHashtags()
                .stream()
                .map(bh -> bh.getHashtag().getHashtag())
                .collect(Collectors.toSet());

        return new BookDetailsDTO(
                book.getBookId(),
                book.getVolumeNumber(),
                book.getTitle(),
                book.getSummary(),
                book.getPublishDate(),
                book.getUser().getUsername(),
                book.getSeries() != null
                        ? book.getSeries().getSeriesName()
                        : null,
                genreNames,
                hashtagNames
        );
    }
}
