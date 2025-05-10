package com.unibuc.mds.memoreasy.Models;

import java.util.List;

public class Category {
    private int id_category;
    private int id_user;
    private String name;
    private List<Chapter> chapterList;

    public Category(){}

    public Category(int id_category, int id_user, String name) {
        this.id_category = id_category;
        this.id_user = id_user;
        this.name = name;
    }

    public Category(int id_category, int id_user, String name, List<Chapter> chapterList) {
        this.id_category = id_category;
        this.id_user = id_user;
        this.name = name;
        this.chapterList=chapterList;
    }

    public int getId_category() {
        return id_category;
    }

    public int getId_user(){return id_user;}

    public String getName() {
        return name;
    }

    public List<Chapter> getChapterList(){return chapterList;}

    public void setId_category(int id_category){this.id_category=id_category;}

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setChapterList(List<Chapter> chapterList) {
        this.chapterList = chapterList;
    }
}
