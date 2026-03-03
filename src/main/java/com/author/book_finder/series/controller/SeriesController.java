package com.author.book_finder.series.controller;


import com.author.book_finder.series.dto.SeriesCreateRequestDTO;
import com.author.book_finder.series.dto.SeriesDetailsDTO;
import com.author.book_finder.series.dto.SeriesResponseDTO;
import com.author.book_finder.series.service.SeriesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/series")
public class SeriesController {

    private final SeriesService seriesService;

    public SeriesController(SeriesService seriesService) {
        this.seriesService = seriesService;
    }

    // CREATE SERIES
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SeriesResponseDTO> createSeries(@RequestBody SeriesCreateRequestDTO dto) {

        SeriesResponseDTO response = seriesService.createSeries(dto);

        return ResponseEntity.ok(response);

    }

    // GET ALL SERIES
    @GetMapping
    public ResponseEntity<Page<SeriesResponseDTO>> getAllSeries(Pageable pageable) {

        Page<SeriesResponseDTO> seriesPage = seriesService.getAllSeries(pageable);

        return ResponseEntity.ok(seriesPage);
    }

    // GET MY SERIES
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<SeriesResponseDTO>> getMySeries(Pageable pageable) {

        Page<SeriesResponseDTO> seriesPage = seriesService.getMySeries(pageable);

        return ResponseEntity.ok(seriesPage);
    }

    // GET SERIES DETAILS
    @GetMapping("/{id}")
    public ResponseEntity<SeriesDetailsDTO> getSeriesDetails(@PathVariable Long id) {

        SeriesDetailsDTO details = seriesService.getSeriesDetails(id);

        return ResponseEntity.ok(details);
    }

    // UPDATE SERIES
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<SeriesResponseDTO> updateSeries(
            @PathVariable Long id,
            @RequestBody SeriesCreateRequestDTO dto) {

        SeriesResponseDTO response = seriesService.updateSeries(id, dto);

        return ResponseEntity.ok(response);
    }

    // DELETE SERIES
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> deleteSeries(@PathVariable Long id) {

        seriesService.deleteSeries(id);

        return ResponseEntity.noContent().build();
    }

}
