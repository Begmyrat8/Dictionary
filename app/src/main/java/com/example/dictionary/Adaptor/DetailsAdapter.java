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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionary.Datebase.DatabaseAccess;
import com.example.dictionary.Datebase.DatabaseOpenHelper;
import com.example.dictionary.Models.Word;
import com.example.dictionary.Models.Words;
import com.example.dictionary.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.squareup.picasso.Picasso;

import java.util.List;


public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.DetailsViewHolder> {

    private Context mCtx;
    private List<Words> wordsList;
    private BottomSheetBehavior mBottomSheetBehavior;
    private OnItemClickListener onItemClickListener;

    private SQLiteDatabase database;
    DatabaseOpenHelper databaseHelper;

    private @DrawableRes int resourceId;

    String COL_TITLE = "title";
    String COL_LANG = "lang";


    public DetailsAdapter(Context mCtx, List<Words> wordsName, BottomSheetBehavior mBottomSheetBehavior, OnItemClickListener onItemClickListener, @DrawableRes int resId) {
        this.mCtx = mCtx;
        this.wordsList = wordsName;
        this.mBottomSheetBehavior = mBottomSheetBehavior;
        this.onItemClickListener = onItemClickListener;
        this.resourceId = resId;
    }

    @NonNull
    @Override
    public DetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.words_view, parent, false);
        return new DetailsViewHolder(view, mCtx, viewType);

    }

    @Override
    public void onBindViewHolder(@NonNull final DetailsViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Words words = wordsList.get(position);

        final String word = words.getWord();
        final String lang = words.getLang();

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

        DatabaseAccess databaseAccess = DatabaseAccess.getInstances(mCtx.getApplicationContext());
        databaseAccess.open();

        Word bookmarkWord = databaseAccess.getWordFromBookmark(word);
        int isMark = bookmarkWord == null? 0:1;
        holder.wordsFav.setTag(isMark);

//        int icon = bookmarkWord == null? R.drawable.ic_baseline_bookmark_border_24:R.drawable.ic_baseline_bookmark_24;
//        holder.wordsFav.setImageResource(icon);
//
        final float scale = mCtx.getResources().getDisplayMetrics().density;
        final int pixels = (int) (800 * scale + 0.5f);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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


        holder.wordsFav.setImageResource(resourceId);
        holder.wordsFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resourceId == R.drawable.ic_delete){
                    database.delete("bookmark", "title" + "=\"" + word+"\"", null);
                    wordsList.remove(position);
                }else {
                    database.delete("history", "title" + "=\"" + word+"\"", null);
                    wordsList.remove(position);
                }
                notifyDataSetChanged();
                holder.wordsFav.setTag(0);
            }

        });
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

    public class DetailsViewHolder extends RecyclerView.ViewHolder {
        TextView wordsName;
        ImageView wordsLang, wordsFav;
        public Context context;
        View mView;

        public DetailsViewHolder(View itemView, Context context, int viewType) {
            super(itemView);
            mView = itemView;
            this.context = context;
            wordsName = itemView.findViewById(R.id.words);
            wordsLang = itemView.findViewById(R.id.word_lang);
            wordsFav = itemView.findViewById(R.id.words_fav);
        }
    }


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




