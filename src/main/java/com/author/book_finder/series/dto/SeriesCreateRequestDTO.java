package com.author.book_finder.series.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class SeriesCreateRequestDTO {

    @NotBlank(message = "Title is required")
    private String seriesName;

    private String description;

    private LocalDate publishDate;


    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }
}
