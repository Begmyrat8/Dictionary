package com.example.dictionary.Fragments;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionary.Activity.WordDict;
import com.example.dictionary.Adaptor.WordsAdapter;
import com.example.dictionary.Datebase.DatabaseAccess;
import com.example.dictionary.Datebase.DatabaseOpenHelper;
import com.example.dictionary.Models.DictionaryFragmentViewModel;
import com.example.dictionary.Models.Word;
import com.example.dictionary.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.List;
import java.util.Locale;


public class DictionaryFragment extends Fragment implements View.OnClickListener {

    private WordsAdapter wordsAdapter;

    private EditText searchWords;
    private ImageButton clearTextBtn;
    private RecyclerView recyclerView;

    private BottomSheetBehavior mBottomSheetBehavior;
    private ImageButton closeBottomSheet;
    private TextView def_Word, def_Value;
    private ImageView defPronounce;
    private TextToSpeech textToSpeech;

    private ImageView defBookmark;

    private SQLiteDatabase database;
    DatabaseAccess databaseAccess;
    Cursor c = null;

    String COL_TITLE = "title";
    String COL_LANG = "lang";

    DictionaryFragmentViewModel viewModel;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dictionary, container, false);

    }

    @SuppressLint("FragmentLiveDataObserve")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initBottomSheet(view);

        databaseAccess = DatabaseAccess.getInstances(getActivity().getApplicationContext());
        databaseAccess.open();
        recyclerView = (RecyclerView) view.findViewById(R.id.dictionary_words_list);

        def_Word = (TextView) view.findViewById(R.id.bottom_sheet_word);
        def_Value = (TextView) view.findViewById(R.id.bottom_sheet_value);

        defPronounce = (ImageView) view.findViewById(R.id.bottom_sheet_pronounce_word);
        defBookmark = (ImageView) view.findViewById(R.id.bottom_sheet_bookmark);

        clearTextBtn = (ImageButton) view.findViewById(R.id.dictionary_clearText);

        closeBottomSheet = (ImageButton) view.findViewById(R.id.bottom_sheet_close_bottom_sheet);

        searchWords = (EditText) view.findViewById(R.id.dictionary_input_search);

        viewModel = ViewModelProviders.of(this).get(DictionaryFragmentViewModel.class);
        viewModel.init(getContext(), 1,"");
        viewModel.getWords().observe(this, new androidx.lifecycle.Observer<List<WordDict>>() {
            @Override
            public void onChanged(@Nullable List<WordDict> categoryDetails) {
                wordsAdapter.notifyDataSetChanged();
            }
        });

        initRecycler(view);

        searchWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }

            }
        });
        searchWords.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                }
            }
        });

        clearTextBtn.setVisibility(View.INVISIBLE);

        searchWords.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    clearTextBtn.setVisibility(View.VISIBLE);
                    clearTextBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            searchWords.setText("");
                            clearTextBtn.setVisibility(View.GONE);
                            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        }
                    });
                } else {
                    clearTextBtn.setVisibility(View.GONE);
                }

                wordsAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

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
                    textToSpeech.setLanguage(new Locale(""));
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

    private void initRecycler(View view){
        wordsAdapter = new WordsAdapter(getContext(), viewModel.getWords().getValue(), mBottomSheetBehavior, searchWords, new WordsAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position, String object, final String lang) {

                def_Word.setText(object);

                DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(getContext());
                database = dbHelper.getWritableDatabase();

                c = database.rawQuery("select value from words where title = '" + object + "';", null);
                while (c.moveToNext()) {
                    String a = c.getString(0);
                    def_Value.setText(a);
                }

//                if (lang.equals("1")){
//                    defPronounce.setEnabled(false);
//                }
//                else if (lang.equals("0")){
//                    defPronounce.setEnabled(true);
//                }

                Word bookmarkWord = databaseAccess.getWordFromBookmark(object);
                int isMark = bookmarkWord == null? 0:1;
                defBookmark.setTag(isMark);

                int icon = bookmarkWord == null? R.drawable.ic_baseline_bookmark_border_24:R.drawable.ic_baseline_bookmark_24;
                defBookmark.setImageResource(icon);

                defBookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int i = (int) defBookmark.getTag();

                        if (i==0){

                            ContentValues contentValues = new ContentValues();
                            contentValues.put(COL_TITLE, def_Word.getText().toString());
                            contentValues.put(COL_LANG, lang);
                            database.insert("bookmark", null, contentValues);
                            defBookmark.setImageResource(R.drawable.ic_baseline_bookmark_24);

                            wordsAdapter.notifyDataSetChanged();
                            defBookmark.setTag(1);

                        }else if (i==1){

                            database.delete("bookmark", "title" + "=\"" + def_Word.getText().toString()+"\"", null) ;
                            defBookmark.setImageResource(R.drawable.ic_baseline_bookmark_border_24);

                            wordsAdapter.notifyDataSetChanged();
                            defBookmark.setTag(0);
                        }

                    }

                });
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setAdapter(wordsAdapter);

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
    public void onClick(View v) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!searchWords.getText().toString().isEmpty()){
            searchWords.setText("");
        }

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
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
