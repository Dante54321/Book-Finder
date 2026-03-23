package com.author.book_finder.book.mapper;

import com.author.book_finder.book.dto.BookDetailsDTO;
import com.author.book_finder.book.dto.BookResponseDTO;
import com.author.book_finder.book.entity.Book;
import com.author.book_finder.genre.entity.Genre;
import com.author.book_finder.hashtag.entity.Hashtag;
import com.author.book_finder.infrastructure.aws.S3Service;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BookMapper {

    private final S3Service s3Service;

    public BookMapper(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    public BookResponseDTO toResponseDTO(Book book) {

        String coverUrl = null;
        if (book.getCoverImageKey() != null && !book.getCoverImageKey().isBlank()) {
            coverUrl = s3Service.getPublicUrl(book.getCoverImageKey());
        }

        return new BookResponseDTO(
                book.getBookId(),
                book.getVolumeNumber(),
                book.getTitle(),
                book.getPublishDate(),
                book.getUser().getUsername(),
                book.getSeries() != null
                        ? book.getSeries().getSeriesName()
                        : null,
                coverUrl
        );
    }

    public BookDetailsDTO toDetailsDTO(Book book) {

        Set<String> genreNames = book.getGenres() == null
                ? Set.of()
                : book.getGenres()
                .stream()
                .map(Genre::getGenreName)
                .collect(Collectors.toSet());

        Set<String> hashtagNames = book.getHashtags() == null
                ? Set.of()
                : book.getHashtags()
                .stream()
                .map(Hashtag::getHashtag)
                .collect(Collectors.toSet());

        String coverUrl = null;
        if (book.getCoverImageKey() != null && !book.getCoverImageKey().isBlank()) {
            coverUrl = s3Service.getPublicUrl(book.getCoverImageKey());
        }

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
                hashtagNames,
                coverUrl
        );
    }
}
