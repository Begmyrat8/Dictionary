package com.example.dictionary.Adaptor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionary.Activity.CategoryActivity;
import com.example.dictionary.Models.CategoryModel;
import com.example.dictionary.R;

import java.util.List;

public class CategoryAdaptor extends RecyclerView.Adapter<CategoryAdaptor.CategoryViewHolder> {

    Context context;
    List<CategoryModel> categories;

    public CategoryAdaptor(Context context, List<CategoryModel> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View categoryItems = LayoutInflater.from(parent.getContext()).inflate(R.layout.category, parent, false);
        return new CategoryAdaptor.CategoryViewHolder(categoryItems);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {


        String categoryName = categories.get(position).getTitle();
        holder.categoryTitle.setText(categoryName);
        holder.categoryBg.setBackgroundColor(Color.parseColor(categories.get(position).getColor()));

        holder.categoryImage.setImageResource(context.getResources().getIdentifier(categories.get(position).getImg(),"drawable",context.getPackageName()));

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, CategoryActivity.class);
            intent.putExtra("categoryName", categoryName);
            intent.putExtra("categoryNumber",String.valueOf(categories.get(position).getId()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static final class CategoryViewHolder extends RecyclerView.ViewHolder{

        LinearLayout categoryBg;
        ImageView categoryImage;
        TextView categoryTitle;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryBg = itemView.findViewById(R.id.categoryBg);
            categoryImage = itemView.findViewById(R.id.categoryImage);
            categoryTitle = itemView.findViewById(R.id.categoryTitle);

        }
    }
}
