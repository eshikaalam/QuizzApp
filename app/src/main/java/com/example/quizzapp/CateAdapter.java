package com.example.quizzapp;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizzapp.Models.Category;
import com.example.quizzapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CateAdapter extends RecyclerView.Adapter<CateAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Category> category;
    private ArrayList<Category> filteredCategory;

    public CateAdapter(Context context, ArrayList<Category> category) {
        this.context = context;
        this.category = category;
        this.filteredCategory = new ArrayList<>(category);
    }

    @NonNull
    @Override
    public CateAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CateAdapter.MyViewHolder holder, int position) {
        holder.bind(filteredCategory.get(position));
    }

    @Override
    public int getItemCount() {
        return filteredCategory.size();
    }

    public void filterList(ArrayList<Category> filteredList) {
        filteredCategory = filteredList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title, no;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            no = itemView.findViewById(R.id.no);
            title = itemView.findViewById(R.id.nameCat);
        }

        public void bind(Category cat) {
            title.setText(cat.getCategory());
            no.setText("#" + (filteredCategory.indexOf(cat) + 1));
        }
    }
}
