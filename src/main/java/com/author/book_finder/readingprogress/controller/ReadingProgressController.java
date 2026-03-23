package com.author.book_finder.readingprogress.controller;

import com.author.book_finder.readingprogress.dto.CurrentlyReadingResponseDTO;
import com.author.book_finder.readingprogress.dto.StartReadingRequestDTO;
import com.author.book_finder.readingprogress.service.ReadingProgressService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reading-progress")
public class ReadingProgressController {

    private final ReadingProgressService readingProgressService;

    public ReadingProgressController(ReadingProgressService readingProgressService) {
        this.readingProgressService = readingProgressService;
    }

    @PostMapping("/start")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void startReading(@RequestBody StartReadingRequestDTO requestDTO,
                             Authentication authentication) {
        readingProgressService.startReading(requestDTO, authentication);
    }

    @GetMapping("/me/currently-reading")
    public List<CurrentlyReadingResponseDTO> getCurrentlyReading(Authentication authentication) {
        return readingProgressService.getCurrentlyReading(authentication);
    }
}