package com.example.dictionary.Adaptor;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.dictionary.Fragments.BookmarkFragment;
import com.example.dictionary.Fragments.CategoryFragment;
import com.example.dictionary.Fragments.DictionaryFragment;
import com.example.dictionary.Fragments.HistoryFragment;

public class MyViewPagerAdaptor extends FragmentStateAdapter {


    public MyViewPagerAdaptor(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new CategoryFragment();
            case 2:
                return new HistoryFragment();
            case 3:
                return new BookmarkFragment();
            default:
                return new DictionaryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
