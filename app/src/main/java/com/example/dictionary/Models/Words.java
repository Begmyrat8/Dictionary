package com.example.dictionary.Models;

public class Words {

    int id;
    String word;
    String lang;


    public Words(int id, String word, String lang ) {
        this.id = id;
        this.word = word;
        this.lang = lang;
    }

    public int getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public String getLang() {
        return lang;
    }

}