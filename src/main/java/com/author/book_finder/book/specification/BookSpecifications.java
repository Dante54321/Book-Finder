package com.author.book_finder.book.specification;

import com.author.book_finder.book.entity.Book;
import com.author.book_finder.genre.entity.Genre;
import com.author.book_finder.hashtag.entity.Hashtag;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class BookSpecifications {

    // Keyword search (title + summary)
    public static Specification<Book> keywordSearch(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;

            String pattern = "%" + keyword.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("summary")), pattern)
            );
        };
    }

    // Genre filter
    public static Specification<Book> hasGenres(List<String> genres) {
        return (root, query, cb) -> {
            if (genres == null || genres.isEmpty()) return null;

            query.distinct(true);
            Join<Book, Genre> genreJoin = root.join("genres", JoinType.LEFT);

            // Case-insensitive match
            return cb.lower(genreJoin.get("genreName")).in(
                    genres.stream().map(String::toLowerCase).toList()
            );
        };
    }

    // Hashtag filter
    public static Specification<Book> hasHashtags(List<String> hashtags) {
        return (root, query, cb) -> {
            if (hashtags == null || hashtags.isEmpty()) return null;

            query.distinct(true);
            Join<Book, Hashtag> hashtagJoin = root.join("hashtags", JoinType.LEFT);

            return cb.lower(hashtagJoin.get("hashtag")).in(
                    hashtags.stream().map(String::toLowerCase).toList()
            );
        };
    }

    // Series by ID
    public static Specification<Book> belongsToSeries(Long seriesId) {
        return (root, query, cb) -> {
            if (seriesId == null) return null;
            return cb.equal(root.get("series").get("seriesId"), seriesId);
        };
    }

    // Series by name
    public static Specification<Book> belongsToSeriesName(String seriesName) {
        return (root, query, cb) -> {
            if (seriesName == null || seriesName.isBlank()) return null;
            return cb.like(cb.lower(root.get("series").get("seriesName")),
                    "%" + seriesName.toLowerCase() + "%");
        };
    }

    // User by ID
    public static Specification<Book> belongsToUser(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) return null;
            return cb.equal(root.get("user").get("userId"), userId);
        };
    }

    // User by name
    public static Specification<Book> belongsToUserName(String username) {
        return (root, query, cb) -> {
            if (username == null || username.isBlank()) return null;
            return cb.like(cb.lower(root.get("user").get("username")),
                    "%" + username.toLowerCase() + "%");
        };
    }
}