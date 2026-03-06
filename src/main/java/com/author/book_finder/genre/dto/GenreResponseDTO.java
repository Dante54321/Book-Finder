package com.author.book_finder.genre.dto;

public class GenreResponseDTO {

    private Long genreId;
    private String genreName;

    public GenreResponseDTO(Long genreId, String genreName) {
        this.genreId = genreId;
        this.genreName = genreName;
    }

    public Long getGenreId() {
        return genreId;
    }

    public String getGenreName() {
        return genreName;
    }
}
