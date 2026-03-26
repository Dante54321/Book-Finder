package com.author.book_finder.chapter.dto;

import com.author.book_finder.enums.FileType;

public class ChapterManageListDTO {

    private Long chapterId;
    private int chapterNumber;
    private String title;
    private boolean preview;
    private FileType fileType;

    public ChapterManageListDTO(Long chapterId,
                                int chapterNumber,
                                String title,
                                boolean preview,
                                FileType fileType) {
        this.chapterId = chapterId;
        this.chapterNumber = chapterNumber;
        this.title = title;
        this.preview = preview;
        this.fileType = fileType;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isPreview() {
        return preview;
    }

    public void setPreview(boolean preview) {
        this.preview = preview;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }
}