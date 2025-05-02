package com.unibuc.mds.memoreasy.Models;

public class Category {
    private int id_category;
    private int id_user;
    private String name;

    public Category(int id_category, int id_user, String name) {
        this.id_category = id_category;
        this.id_user = id_user;
        this.name = name;
    }

    public int getId_category() {
        return id_category;
    }

    public String getName() {
        return name;
    }
}
