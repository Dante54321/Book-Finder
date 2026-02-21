package com.author.book_finder.dto;

public class ChapterResponseDTO {
    private Long chapterId;
    private String title;
    private int chapterNumber;
    private boolean isPreview;
    private String contentType;

    public ChapterResponseDTO(Long chapterId, String title, int chapterNumber, boolean isPreview, String contentType) {
        this.chapterId = chapterId;
        this.title = title;
        this.chapterNumber = chapterNumber;
        this.isPreview = isPreview;
        this.contentType = contentType;
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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
