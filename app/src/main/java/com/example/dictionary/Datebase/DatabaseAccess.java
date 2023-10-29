package com.example.dictionary.Datebase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.dictionary.Activity.WordDict;
import com.example.dictionary.Models.CategoryModel;
import com.example.dictionary.Models.Word;
import com.example.dictionary.Models.Words;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {

    private final SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instances;
    Cursor c = null;
    String COL_TITLE = "title";
    String COL_LANG = "lang";


    public DatabaseAccess(Context context) {
        this.openHelper = new com.example.dictionary.Datebase.DatabaseOpenHelper(context);
    }

    public static DatabaseAccess getInstances(Context context) {
        if (instances == null) {
            instances = new DatabaseAccess(context);
        }
        return instances;
    }

    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public List<WordDict> getAllData() {
        c = database.rawQuery("select * from words", null);
        List<WordDict> stringArrayList = new ArrayList<>();
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String title = c.getString(1);
            String lang = c.getString(2);

            stringArrayList.add(new WordDict(id, title, lang));
        }
        return stringArrayList;
    }

    public List<Words> getAllBookmark() {
        c = database.rawQuery("select * from bookmark Order by id Desc", null);
        List<Words> stringArrayList = new ArrayList<>();
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String title = c.getString(1);
            String lang = c.getString(2);


            stringArrayList.add(new Words(id, title, lang));
        }
        return stringArrayList;
    }

    public List<Words> getAllHistory() {
        c = database.rawQuery("select * from history  Order by id Desc", null);
        List<Words> stringArrayList = new ArrayList<>();
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String title = c.getString(1);
            String lang = c.getString(2);


            stringArrayList.add(new Words(id, title, lang));
        }
        return stringArrayList;
    }

    @SuppressLint("Range")
    public Word getWordFromBookmark(String title) {
        c = database.rawQuery("Select * from bookmark Where upper([title]) = upper(?)", new String[]{title});
        Word words = null;
        while (c.moveToNext()) {
            words = new Word();
            words.title = c.getString(c.getColumnIndex("title"));
        }

        return words;
    }

    public List<Words> getCategoryData(String categoryNumber, String catLang) {
        c = database.rawQuery("select * from words where category = '" + categoryNumber + "' and lang = '" + catLang + "';", null);
        List<Words> stringArrayList = new ArrayList<>();
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String title = c.getString(1);
            String lang = c.getString(2);


            stringArrayList.add(new Words(id, title, lang));
        }
        return stringArrayList;
    }

    public String getWordByTitle(String title){
        String word = null;

        try {
            c = database.rawQuery("select value from words where title = '" + title + "';", null);
            if (c==null) return null;
            while (c.moveToNext()) {
                word = c.getString(0);

            }
        }catch (Exception e){

        }
        return word;
    }

    public void insertWordIntoTable(String word, String lang, String table){

        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_TITLE, word);
            contentValues.put(COL_LANG, lang);
            database.insert(table, null, contentValues);
        }catch (Exception e){

        }
    }

    public void deleteWordFromTable(String word, String table){
        try {
            database.delete(table, "title" + "=\"" + word+"\"", null) ;
        }catch (Exception e){

        }
    }

    public void clearAllDataFromTable(String table){
        try {
            database.delete(table, null , null);
        }catch (Exception e){

        }
    }

    @SuppressLint("Range")
    public String getLastRowOfTable(String table){
        String selectQuery= "SELECT * FROM " + table +" ORDER BY id DESC LIMIT 1";

        String str = "";
        try {
            Cursor cursor = database.rawQuery(selectQuery, null);
            if(cursor.moveToFirst())
                str  =  cursor.getString( cursor.getColumnIndex("title") );
            cursor.close();
        }catch (Exception e){

        }
        return str;
    }

    public List<CategoryModel> getAllCategory() {
        c = database.rawQuery("select * from categories", null);
        List<CategoryModel> stringArrayList = new ArrayList<>();
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String title = c.getString(1);
            String img = c.getString(2);
            String color = c.getString(3);

            stringArrayList.add(new CategoryModel(id, title, img, color));
        }
        return stringArrayList;
    }
}

