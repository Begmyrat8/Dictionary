package com.example.dictionary.Adaptor;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.dictionary.Activity.CategoryActivity;
import com.example.dictionary.Fragments.CategoryEnglishDetailFragment;
import com.example.dictionary.Fragments.CategoryRussianDetailFragment;

public class CategoryViewPagerAdaptor extends FragmentStateAdapter {

    public CategoryViewPagerAdaptor(@NonNull CategoryActivity fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        return position == 1 ? new CategoryEnglishDetailFragment() : new CategoryRussianDetailFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

