package com.example.FSMap;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterUserFavoriteList extends RecyclerView.Adapter<AdapterUserFavoriteList.FavoriteViewHolder> {


    Context context;
    ArrayList<UserFavoriteList> listFavorite;
    MapsActivity mapsActivity;
    Favorites favorites;
    AdapterUserFavoriteList adapterUserFavoriteList;
    CustomMarkerAdapter customMarkerAdapter;

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

        holder.trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String markerTitle = holder.TitleOfTheMarker.getText().toString();

                Favorites.removeFromFavorite(context,markerTitle);
                int size = listFavorite.size();
                listFavorite.clear();
                notifyItemRangeRemoved(0,size);

            }
        });



    }

    @Override
    public int getItemCount() {
        return listFavorite.size();
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder{

        TextView TitleOfTheMarker;
        ImageButton trash;
        RelativeLayout markerClick;
        ImageButton editFavorites;
        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);

            TitleOfTheMarker = itemView.findViewById(R.id.TitleMarker);
            markerClick = itemView.findViewById(R.id.marker);
            trash = itemView.findViewById(R.id.trashFavorites);
            editFavorites = itemView.findViewById(R.id.editFavorites);
        }
    }

}
