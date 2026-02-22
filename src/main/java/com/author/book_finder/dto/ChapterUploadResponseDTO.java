package com.author.book_finder.dto;

public class ChapterUploadResponseDTO {
    private Long chapterId;
    private String downloadUrl;

    public ChapterUploadResponseDTO(Long chapterId, String downloadUrl) {
        this.chapterId = chapterId;
        this.downloadUrl = downloadUrl;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
}
