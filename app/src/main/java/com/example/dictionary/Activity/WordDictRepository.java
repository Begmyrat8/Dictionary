package com.example.dictionary.Activity;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.dictionary.Datebase.DatabaseAccess;

import java.util.ArrayList;
import java.util.List;

public class WordDictRepository {

    private static WordDictRepository mInstance;
    DatabaseAccess databaseAccess;
    private List<WordDict> dataSet = new ArrayList<>();


    public static WordDictRepository getInstance(){
        if (mInstance==null){
            mInstance = new WordDictRepository();
        }
        return mInstance;
    }

    public MutableLiveData<List<WordDict>> getDetails(Context context, int n, String i){
        dataSet.clear();
        setDetails(context, n, i);

        MutableLiveData<List<WordDict>> data = new MutableLiveData<>();
        data.setValue(dataSet);
        return data;
    }

    private void setDetails(Context context, int n, String catN) {

        databaseAccess = DatabaseAccess.getInstances(context.getApplicationContext());
        databaseAccess.open();

        if (n ==1){
            dataSet = databaseAccess.getAllData();
        }
        else if (n == 2){

        }
        else if (n == 3){
//            dataSet = databaseAccess.getAllBookmark();
        }
        else if (n == 4){
//            dataSet = databaseAccess.getAllGermanCategoryData(catN, "0");
        }
        else if (n == 5){
//            dataSet = databaseAccess.getAllGermanCategoryData(catN, "1");
        }

    }


}