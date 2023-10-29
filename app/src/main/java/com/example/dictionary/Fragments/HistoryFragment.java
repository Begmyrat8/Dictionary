package com.example.dictionary.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.example.dictionary.Adaptor.DetailsAdapter;
import com.example.dictionary.Datebase.DatabaseAccess;
import com.example.dictionary.R;
import com.example.dictionary.Models.Word;
import com.example.dictionary.Models.Words;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class HistoryFragment extends Fragment {

    ImageButton closeBottomSheet, deleteHistory, words_fav;
    List<Words> wordsList = new ArrayList<>();
    BottomSheetBehavior mBottomSheetBehavior;
    TextView def_Word, def_Value, words;
    DetailsAdapter detailsAdapter;
    DatabaseAccess databaseAccess;
    RecyclerView recyclerView;
    TextToSpeech textToSpeech;
    ImageView defPronounce;
    ImageView defBookmark;
    String language;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initBottomSheet(view);

        recyclerView = view.findViewById(R.id.history_list);
        def_Word = view.findViewById(R.id.bottom_sheet_word);
        def_Value = view.findViewById(R.id.bottom_sheet_value);
        defPronounce = view.findViewById(R.id.bottom_sheet_pronounce_word);
        defBookmark = view.findViewById(R.id.bottom_sheet_bookmark);
        deleteHistory = view.findViewById(R.id.history_delete);
        closeBottomSheet = view.findViewById(R.id.bottom_sheet_close_bottom_sheet);
        words_fav = view.findViewById(R.id.words_fav);
        words = view.findViewById(R.id.words);
        deleteHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.deleteAll))
                        .setCancelable(true)

                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                databaseAccess.clearAllDataFromTable("history");
                                takeWordList();
                            }
                        })

                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

    }

    @Override
    public void onResume() {

        super.onResume();
        takeWordList();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                    if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    } else {
                        getActivity().onBackPressed();
                    }
                    return true;
                }


                return false;
            }
        });

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
                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_EXPANDED:
                    case BottomSheetBehavior.STATE_HIDDEN:
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

    }

    public void takeWordList() {

        databaseAccess = DatabaseAccess.getInstances(getActivity().getApplicationContext());
        databaseAccess.open();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        wordsList = databaseAccess.getAllHistory();

        detailsAdapter = new DetailsAdapter(getActivity(), wordsList, mBottomSheetBehavior, new DetailsAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position, String object, final String lang) {
                // Handle Object of list item here
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

                            detailsAdapter.notifyDataSetChanged();
                            defBookmark.setTag(1);

                        } else if (i == 1) {

                            databaseAccess.deleteWordFromTable(def_Word.getText().toString(), "bookmark");
                            defBookmark.setImageResource(R.drawable.ic_baseline_bookmark_border_24);

                            detailsAdapter.notifyDataSetChanged();
                            defBookmark.setTag(0);
                        }
                    }

                });

            }
        }, R.drawable.delete);

        recyclerView.setAdapter(detailsAdapter);

        defPronounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = def_Word.getText().toString();
                textToSpeech.setSpeechRate(1f);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeech.speak(key, TextToSpeech.QUEUE_FLUSH, null, null);
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

                    textToSpeech.setLanguage(new Locale("ru"));

                }
            }
        });

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

}