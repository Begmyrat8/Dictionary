package com.example.dictionary.Adaptor;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionary.Activity.WordDict;
import com.example.dictionary.Datebase.DatabaseOpenHelper;
import com.example.dictionary.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class WordsAdapter extends RecyclerView.Adapter<WordsAdapter.WordsViewHolder> implements Filterable {

    private Context mCtx;
    private List<WordDict> wordsList;
    private List<WordDict> wordsListFull;
    private BottomSheetBehavior mBottomSheetBehavior;
    private TextView textView;
    private OnItemClickListener onItemClickListener;

    private SQLiteDatabase database;
    DatabaseOpenHelper databaseHelper;

    String COL_TITLE = "title";
    String COL_LANG = "lang";

    public WordsAdapter(Context mCtx, List<WordDict> wordsName, BottomSheetBehavior mBottomSheetBehavior, TextView textView, OnItemClickListener onItemClickListener) {
        this.mCtx = mCtx;
        this.wordsList = wordsName;
        wordsListFull = new ArrayList<>(wordsList);
        this.mBottomSheetBehavior = mBottomSheetBehavior;
        this.textView = textView;
        this.onItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public WordsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.words_view, parent, false);
        return new WordsViewHolder(view, mCtx, viewType);

    }

    @Override
    public void onBindViewHolder(@NonNull final WordsViewHolder holder, @SuppressLint("RecyclerView") final int position) {
//        final Words words = wordsList.get(position);

        final WordDict words = wordsList.get(position);

        final String word = words.getWord();
        final String lang = words.getLang();

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mBottomSheetBehavior.setPeekHeight(0);

        holder.wordsName.setText(word);

        if (lang.equals("0")) {
//            holder.wordsLang.setImageResource(R.drawable.flag_deu);
            Picasso.get().load(R.mipmap.flag_ru).into(holder.wordsLang);
        } else if (lang.equals("1")) {
//            holder.wordsLang.setImageResource(R.drawable.flag_tkm);
            Picasso.get().load(R.mipmap.flag_en).into(holder.wordsLang);
        }

        databaseHelper = new DatabaseOpenHelper(mCtx);
        database = databaseHelper.getWritableDatabase();

        final float scale = mCtx.getResources().getDisplayMetrics().density;
        final int pixels = (int) (800 * scale + 0.5f);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) mCtx.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);

                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                mBottomSheetBehavior.setPeekHeight(0);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Actions to do after 1 second
                        onItemClickListener.onItemClicked(position, word, lang);

                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        mBottomSheetBehavior.setPeekHeight(pixels);
                    }
                }, 200);
                notifyDataSetChanged();

                String title = getLastRow("history");

                if (!title.equals(word)) {

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(COL_TITLE, word);
                    contentValues.put(COL_LANG, lang);

                    database.insert("history", null, contentValues);

                    notifyDataSetChanged();
                }

            }
        });

        holder.wordsFav.setVisibility(View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return wordsList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class WordsViewHolder extends RecyclerView.ViewHolder {
        TextView wordsName;
        ImageView wordsLang, wordsFav;
        public Context context;
        View mView;

        public WordsViewHolder(View itemView, Context context, int viewType) {
            super(itemView);
            mView = itemView;
            this.context = context;
            wordsName =(TextView) itemView.findViewById(R.id.words);
            wordsLang = itemView.findViewById(R.id.word_lang);
            wordsFav = itemView.findViewById(R.id.words_fav);
        }
    }

    @Override
    public Filter getFilter() {
        return wordsFilter;
    }

    private Filter wordsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<WordDict> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(wordsListFull);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (WordDict words: wordsListFull){
                    if (words.getWord().toLowerCase().contains(filterPattern)){
                        filteredList.add(words);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            wordsList = new ArrayList<WordDict>();
            wordsList.clear();
            if (wordsList != null){
                wordsList.addAll((List) results.values);
            }
            notifyDataSetChanged();
        }
    };

    public interface OnItemClickListener {
        void onItemClicked(int position, String object, String lang);
    }

    @SuppressLint("Range")
    private String getLastRow(String table){
        String selectQuery= "SELECT * FROM " + table +" ORDER BY id DESC LIMIT 1";
        database = databaseHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        String str = "";
        if(cursor.moveToFirst())

            str  =  cursor.getString( cursor.getColumnIndex("title") );
        cursor.close();
        return str;
    }

}




