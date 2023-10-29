package com.example.dictionary.Activity;

public class WordDict {

    int id;
    String word;
    String lang;


    public WordDict(int id, String word, String lang ) {
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