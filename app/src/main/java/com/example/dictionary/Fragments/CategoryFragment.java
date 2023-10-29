package com.example.dictionary.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionary.Adaptor.CategoryAdaptor;
import com.example.dictionary.Datebase.DatabaseAccess;
import com.example.dictionary.Models.CategoryModel;
import com.example.dictionary.R;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

   List<CategoryModel> categoryList = new ArrayList<>();
    CategoryAdaptor categoryAdaptor;
    DatabaseAccess databaseAccess;
    RecyclerView categoryRecycler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        databaseAccess = DatabaseAccess.getInstances(getActivity().getApplicationContext());
        databaseAccess.open();

        categoryRecycler = view.findViewById(R.id.categoryList);
        categoryRecycler.setHasFixedSize(true);
        categoryList = databaseAccess.getAllCategory();

        setCategoryRecycler(categoryList);
    }

    private void setCategoryRecycler(List<CategoryModel> categoryList) {
        GridLayoutManager manager = new GridLayoutManager(getContext(),2,RecyclerView.VERTICAL,false);
        categoryRecycler.setLayoutManager(manager);

        categoryAdaptor = new CategoryAdaptor(getContext(), categoryList);
        categoryRecycler.setAdapter(categoryAdaptor);
    }



}