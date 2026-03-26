package com.author.book_finder.chapter.dto;

import com.author.book_finder.enums.FileType;

public class ChapterEditorDTO {

    private Long chapterId;
    private Long bookId;
    private int chapterNumber;
    private String title;
    private FileType fileType;
    private String content;

    public ChapterEditorDTO(Long chapterId,
                            Long bookId,
                            int chapterNumber,
                            String title,
                            FileType fileType,
                            String content) {
        this.chapterId = chapterId;
        this.bookId = bookId;
        this.chapterNumber = chapterNumber;
        this.title = title;
        this.fileType = fileType;
        this.content = content;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
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

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}