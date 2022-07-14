package com.example.logintesting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public class AdapterUserFavoriteList extends RecyclerView.Adapter<AdapterUserFavoriteList.FavoriteViewHolder> {


    Context context;
    ArrayList<UserFavoriteList> listFavorite;


    //Constructor
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

  /*      holder.removeStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });*/

    }

    @Override
    public int getItemCount() {
        return listFavorite.size();
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder{

        TextView TitleOfTheMarker;
        ImageButton removeStar;
        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);

            TitleOfTheMarker = itemView.findViewById(R.id.TitleMarker);
            removeStar = itemView.findViewById(R.id.removeFromFavorites);
        }
    }

}
