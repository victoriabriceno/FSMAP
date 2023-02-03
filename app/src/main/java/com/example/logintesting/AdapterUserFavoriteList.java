package com.example.logintesting;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public class AdapterUserFavoriteList extends RecyclerView.Adapter<AdapterUserFavoriteList.FavoriteViewHolder> {


    Context context;
    ArrayList<UserFavoriteList> listFavorite;
    MapsActivity mapsActivity;
    Favorites favorites;


    //Constructor
    public AdapterUserFavoriteList(Context context,ArrayList<UserFavoriteList> listFavorite, MapsActivity mapsActivity) {
        this.context = context;
        this.listFavorite = listFavorite;
        this.mapsActivity = mapsActivity;
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


        // Marker TO TAKE YOU TO MAP
        holder.markerClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               String markerTitle = holder.TitleOfTheMarker.getText().toString();

               Intent intent = new Intent(context, MapsActivity.class);
               intent.putExtra("marker_ToMap",markerTitle);
               context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return listFavorite.size();
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder{

        TextView TitleOfTheMarker;
        ImageButton removeStar;
        RelativeLayout markerClick;
        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);

            TitleOfTheMarker = itemView.findViewById(R.id.TitleMarker);
            markerClick = itemView.findViewById(R.id.marker);
            //removeStar = itemView.findViewById(R.id.removeFromFavorites);
        }
    }

}
