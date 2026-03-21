package com.author.book_finder.series.controller;


import com.author.book_finder.enums.FileType;
import com.author.book_finder.series.dto.PresignedUploadResponseDTO;
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
@RequestMapping("/api")
public class SeriesController {

    private final SeriesService seriesService;

    public SeriesController(SeriesService seriesService) {
        this.seriesService = seriesService;
    }

    // CREATE SERIES
    @PostMapping("/series/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SeriesResponseDTO> createSeries(@RequestBody SeriesCreateRequestDTO dto) {

        SeriesResponseDTO response = seriesService.createSeries(dto);

        return ResponseEntity.ok(response);

    }

    // GENERATE PRESIGNED URL FOR SERIES COVER
    @PostMapping("/series/{seriesId}/cover/upload-url")
    public ResponseEntity<PresignedUploadResponseDTO> generateCoverUploadUrl(
            @PathVariable Long seriesId,
            @RequestParam String filename,
            @RequestParam FileType fileType) {

        PresignedUploadResponseDTO responseDTO = seriesService.generateCoverUploadUrl(seriesId, filename, fileType);
        return ResponseEntity.ok(responseDTO);
    }

    // GET COVER IMAGE URL
    @GetMapping("/series/{seriesId}/cover")
    public ResponseEntity<String> getCoverUrl(@PathVariable Long seriesId) {
        return ResponseEntity.ok(seriesService.getCoverUrl(seriesId));
    }

    // GET ALL SERIES
    @GetMapping("/series/list")
    public ResponseEntity<Page<SeriesResponseDTO>> getAllSeries(Pageable pageable) {

        Page<SeriesResponseDTO> seriesPage = seriesService.getAllSeries(pageable);

        return ResponseEntity.ok(seriesPage);
    }

    // GET MY SERIES
    @GetMapping("/series/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<SeriesResponseDTO>> getMySeries(Pageable pageable) {

        Page<SeriesResponseDTO> seriesPage = seriesService.getMySeries(pageable);

        return ResponseEntity.ok(seriesPage);
    }

    // GET SERIES DETAILS
    @GetMapping("/series/{id}/details")
    public ResponseEntity<SeriesDetailsDTO> getSeriesDetails(@PathVariable Long id) {

        SeriesDetailsDTO details = seriesService.getSeriesDetails(id);

        return ResponseEntity.ok(details);
    }

    // UPDATE SERIES
    @PutMapping("/series/{id}/update")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<SeriesResponseDTO> updateSeries(
            @PathVariable Long id,
            @RequestBody SeriesCreateRequestDTO dto) {

        SeriesResponseDTO response = seriesService.updateSeries(id, dto);

        return ResponseEntity.ok(response);
    }

    // DELETE SERIES
    @DeleteMapping("/series/{id}/delete")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> deleteSeries(@PathVariable Long id) {

        seriesService.deleteSeries(id);

        return ResponseEntity.noContent().build();
    }

}
