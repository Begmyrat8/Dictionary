package com.example.dictionary.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.dictionary.Adaptor.CategoryViewPagerAdaptor;
import com.example.dictionary.R;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class CategoryActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    CategoryViewPagerAdaptor categoryViewPagerAdaptor;
    String categoryName ;
    String categoryNumber ;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categoryName = getIntent().getStringExtra("categoryName");
        categoryNumber = getIntent().getStringExtra("categoryNumber");

        tabLayout = findViewById(R.id.categoryTabLayout);
        viewPager2 = findViewById(R.id.categoryView_pager);

        categoryViewPagerAdaptor = new CategoryViewPagerAdaptor(this);
        viewPager2.setAdapter(categoryViewPagerAdaptor);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Objects.requireNonNull(tabLayout.getTabAt(position)).select();
            }
        });

    }

    public String getMyData() {return categoryNumber;}
}