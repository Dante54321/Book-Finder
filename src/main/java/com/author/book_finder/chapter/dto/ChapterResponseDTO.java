package com.author.book_finder.chapter.dto;

import com.author.book_finder.enums.FileType;

public class ChapterResponseDTO {
    private Long chapterId;
    private int chapterNumber;
    private String title;
    private boolean isPreview;
    private FileType fileType;
    private String previewUrl;
    private String fullUrl;

    public ChapterResponseDTO(Long chapterId, int chapterNumber, String title, boolean isPreview, FileType fileType, String previewUrl, String fullUrl) {
        this.chapterId = chapterId;
        this.chapterNumber = chapterNumber;
        this.title = title;
        this.isPreview = isPreview;
        this.fileType = fileType;
        this.previewUrl = previewUrl;
        this.fullUrl = fullUrl;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public boolean isPreview() {
        return isPreview;
    }

    public void setPreview(boolean preview) {
        isPreview = preview;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }
}
