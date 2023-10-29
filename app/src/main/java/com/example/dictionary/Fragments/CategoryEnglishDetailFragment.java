package com.example.dictionary.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionary.Activity.CategoryActivity;
import com.example.dictionary.Adaptor.CategoryDetailAdaptor;
import com.example.dictionary.Datebase.DatabaseAccess;
import com.example.dictionary.R;
import com.example.dictionary.Models.Word;
import com.example.dictionary.Models.Words;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CategoryEnglishDetailFragment extends Fragment {

    List<Words> wordsList = new ArrayList<>();
    CategoryDetailAdaptor CategorydetailsAdapter;

    RecyclerView recyclerView;

    BottomSheetBehavior mBottomSheetBehavior;
    ImageButton closeBottomSheet;
    TextView def_Word, def_Value;
    ImageView defPronounce;
    TextToSpeech textToSpeech;
    ImageView defBookmark;


    DatabaseAccess databaseAccess;

    String language;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category_english, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        initBottomSheet(view);

        databaseAccess = DatabaseAccess.getInstances(getActivity().getApplicationContext());
        databaseAccess.open();

        recyclerView = (RecyclerView) view.findViewById(R.id.category_eng_words_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        CategoryActivity activity = (CategoryActivity) getActivity();
        String categoryNumber = activity.getMyData();
        wordsList = databaseAccess.getCategoryData(categoryNumber, "1");
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAA");
        System.out.println(wordsList.size());
        System.out.println(categoryNumber);


        def_Word = (TextView) view.findViewById(R.id.bottom_sheet_word);
        def_Value = (TextView) view.findViewById(R.id.bottom_sheet_value);

        defPronounce = (ImageView) view.findViewById(R.id.bottom_sheet_pronounce_word);
        defBookmark = (ImageView) view.findViewById(R.id.bottom_sheet_bookmark);


        CategorydetailsAdapter = new CategoryDetailAdaptor(getActivity(), wordsList, mBottomSheetBehavior, new CategoryDetailAdaptor.OnItemClickListener() {
            @Override
            public void onItemClicked(int position, String object, String lang) {
                def_Word.setText(object);

                language = lang;

                def_Value.setText(databaseAccess.getWordByTitle(object));

                Word bookmarkWord = databaseAccess.getWordFromBookmark(object);
                int isMark = bookmarkWord == null ? 0 : 1;
                defBookmark.setTag(isMark);

                int icon = bookmarkWord == null ? R.drawable.ic_baseline_bookmark_border_24 : R.drawable.ic_baseline_bookmark_24;
                defBookmark.setImageResource(icon);

                defBookmark.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onClick(View v) {

                        int i = (int) defBookmark.getTag();

                        if (i == 0) {

                            databaseAccess.insertWordIntoTable(def_Word.getText().toString(), lang, "bookmark");
                            defBookmark.setImageResource(R.drawable.ic_baseline_bookmark_24);

                            CategorydetailsAdapter.notifyDataSetChanged();
                            defBookmark.setTag(1);

                        } else if (i == 1) {

                            databaseAccess.deleteWordFromTable(def_Word.getText().toString(), "bookmark");
                            defBookmark.setImageResource(R.drawable.ic_baseline_bookmark_border_24);

                            CategorydetailsAdapter.notifyDataSetChanged();
                            defBookmark.setTag(0);
                        }

                    }

                });

            }
        });

        recyclerView.setAdapter(CategorydetailsAdapter);

        defPronounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = def_Word.getText().toString();
                textToSpeech.setSpeechRate(1f);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeech.speak(key,TextToSpeech.QUEUE_FLUSH,null,null);
                } else {
                    textToSpeech.speak(key, TextToSpeech.QUEUE_FLUSH, null);
                }

            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Scrolling up
                } else {
                    // Scrolling down
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {

                    textToSpeech.setLanguage(new Locale("eng"));

                }
            }
        });


        closeBottomSheet = (ImageButton) view.findViewById(R.id.bottom_sheet_close_bottom_sheet);
        closeBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });


    }
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
    private void initBottomSheet(View view) {

        View bottomSheet = (View) view.findViewById(R.id.bottom_sheet);

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.isHideable();
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull final View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

    }
    @Override
    public void onResume() {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                    if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                    else {
                        getActivity().onBackPressed();
                    }
                    return true;
                }


                return false;
            }
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}