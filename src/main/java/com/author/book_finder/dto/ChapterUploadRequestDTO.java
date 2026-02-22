package com.author.book_finder.dto;

import org.springframework.web.multipart.MultipartFile;

public class ChapterUploadRequestDTO {
    private MultipartFile file;
    private Long bookId;
    private String title;
    private int chapterNumber;
    private boolean isPreview;

    public MultipartFile getFile() {
        return file;
    }
    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public Long getBookId() {
        return bookId;
    }
    public void setBookId(Long bookId) {
        this.bookId = bookId;
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

    public boolean getIsPreview() {
        return isPreview;
    }
    public void setIsPreview(boolean isPreview) {
        this.isPreview = isPreview;
    }
}
