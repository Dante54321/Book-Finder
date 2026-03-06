package com.author.book_finder.genre.service;

import com.author.book_finder.genre.dto.GenreResponseDTO;
import com.author.book_finder.genre.repository.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreService {

    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public List<GenreResponseDTO> getAllGenres() {
        return genreRepository.findAll()
                .stream()
                .map(g -> new GenreResponseDTO(
                        g.getGenreId(),
                        g.getGenreName()))
                .toList();
    }
}
