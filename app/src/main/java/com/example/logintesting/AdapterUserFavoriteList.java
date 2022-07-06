package com.example.logintesting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterUserFavoriteList extends RecyclerView.Adapter<AdapterUserFavoriteList.FavoriteViewHolder> {


    Context context;
    ArrayList<UserFavoriteList> listFavorite;

    public AdapterUserFavoriteList(Context context,ArrayList<UserFavoriteList> listFavorite) {
        this.context = context;
        this.listFavorite = listFavorite;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_favoritemarkers,parent,false);
        return new FavoriteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {

        UserFavoriteList userFavoriteList = listFavorite.get(position);
        holder.TitleOfTheMarker.setText(userFavoriteList.getMarkerTitle());

    }

    @Override
    public int getItemCount() {
        return listFavorite.size();
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder{

        TextView TitleOfTheMarker;
        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);

            TitleOfTheMarker = itemView.findViewById(R.id.TitleMarker);
        }
    }

}
