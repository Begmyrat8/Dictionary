package com.example.dictionary.Models;

public class CategoryModel {
    int id;
    String img, title, color;


    public CategoryModel(int id,  String img, String title, String color) {
        this.id = id;

        this.img = img;
        this.title = title;
        this.color = color;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

}