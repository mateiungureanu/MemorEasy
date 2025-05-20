package com.unibuc.mds.memoreasy.Models;

import java.time.LocalDateTime;
import java.util.List;

public class Chapter {


    private int chapterId;
    private int categoryId;
    private String name;
    private LocalDateTime last_accessed;
    private List<Flashcard> flashcardList;

    public Chapter() {
    }

    public Chapter(int id1, int id2, String n, LocalDateTime date) {
        chapterId = id1;
        categoryId = id2;
        name = n;
        last_accessed = date;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int id_chapter) {
        this.chapterId = id_chapter;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int id_category) {
        this.categoryId = id_category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getLast_accessed() {
        return last_accessed;
    }

    public void setLast_accessed(LocalDateTime last_accessed) {
        this.last_accessed = last_accessed;
    }

    public List<Flashcard> getFlashcardList() {
        return flashcardList;
    }

    public void setFlashcardList(List<Flashcard> flashcardList) {
        this.flashcardList = flashcardList;
    }
}
