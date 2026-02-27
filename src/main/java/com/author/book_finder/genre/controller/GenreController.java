package com.author.book_finder.genre.controller;


import com.author.book_finder.genre.dto.GenreResponseDTO;
import com.author.book_finder.genre.service.GenreService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<GenreResponseDTO> getAllGenres()
    {
        return genreService.getAllGenres();
    }
}
