package com.androidbull.messmanagment.ui.menu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidbull.messmanagment.R;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MyViewHolder> {

    private List<Menu> moviesList;

    public MenuAdapter(List<Menu> menus) {
        this.moviesList = menus;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Menu menu = moviesList.get(position);
        holder.lunch.setText(menu.getLunch());
        holder.dinner.setText(menu.getDinner());
    }


    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lunch, dinner;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lunch = itemView.findViewById(R.id.tv_menu_item_lunch);
            dinner = itemView.findViewById(R.id.tv_menu_item_dinner);

        }
    }

}
