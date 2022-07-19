package com.example.logintesting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomMarkerAdapter extends RecyclerView.Adapter<CustomMarkerAdapter.CustomMarkerViewHolder> {


    Context context;
    ArrayList<CustomMarkersList> listCustomMarkers;


    //Constructor
    public CustomMarkerAdapter(Context context,ArrayList<CustomMarkersList> customMarkersLists) {
        this.context = context;
        this.listCustomMarkers = customMarkersLists;
    }

    @NonNull
    @Override
    public CustomMarkerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_custommarker,parent,false);
        return new CustomMarkerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomMarkerAdapter.CustomMarkerViewHolder holder, int position) {

        CustomMarkersList customMarkersList = listCustomMarkers.get(position);
        holder.TitleOfTheMarker.setText(customMarkersList.getMarkerTitle());

  /*      holder.removeStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });*/

    }

    @Override
    public int getItemCount() {
        return listCustomMarkers.size();
    }

    public static class CustomMarkerViewHolder extends RecyclerView.ViewHolder{

        TextView TitleOfTheMarker;
        ImageButton removeStar;
        public CustomMarkerViewHolder(@NonNull View itemView) {
            super(itemView);

            TitleOfTheMarker = itemView.findViewById(R.id.TitleMarker1);
            //removeStar = itemView.findViewById(R.id.removeFromFavorites);
        }
    }

}
