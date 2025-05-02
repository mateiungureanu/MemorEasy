package com.unibuc.mds.memoreasy.Models;

import java.time.LocalDateTime;
import java.util.Date;

public class Chapter {


    private int id_chapter;
    private int id_category;
    private String name;
    private LocalDateTime last_accessed;

    public Chapter(int id1, int id2, String n, LocalDateTime date) {
        id_chapter=id1;
        id_category=id2;
        name=n;
        last_accessed=date;
    }

    public int getId_chapter() {
        return id_chapter;
    }

    public int getId_category() {
        return id_category;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getLast_accessed() {
        return last_accessed;
    }

    public void setId_chapter(int id_chapter) {
        this.id_chapter = id_chapter;
    }

    public void setId_category(int id_category) {
        this.id_category = id_category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLast_accessed(LocalDateTime last_accessed) {
        this.last_accessed = last_accessed;
    }
}
