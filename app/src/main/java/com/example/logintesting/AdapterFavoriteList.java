package com.example.logintesting;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logintesting.databinding.RowFavoritemarkersBinding;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterFavoriteList extends  RecyclerView.Adapter<AdapterFavoriteList.HolderMarkerFavorite>{

    private Context context;
    private ArrayList<ModelFavorite> favoriteList;

    //view binding for row
    private RowFavoritemarkersBinding binding;
    private static  final String TAG = "FAV_MARKER_TITLE";
    Marker markerTitle2;

    //constructor
    public AdapterFavoriteList(Context context,ArrayList<ModelFavorite> favoriteList){
        this.context = context;
        this.favoriteList = favoriteList;
    }

    @NonNull
    @Override
    public HolderMarkerFavorite onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowFavoritemarkersBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderMarkerFavorite(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderMarkerFavorite holder, int position) {
        ModelFavorite modelFavorite = favoriteList.get(position);


        loadMarkerTitle(modelFavorite, holder);
        //Handle click , markerTitle
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        //Handle click , remove from favorite
        holder.removeFromFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Favorites.removeFromFavorite(context,markerTitle2);//possible error

            }
        });


    }

    private void loadMarkerTitle(ModelFavorite modelFavorite, HolderMarkerFavorite holder) {

        String markerTitle = modelFavorite.getMarkerTitle();
        Log.d(TAG, "loadMarkerTitle: Marker title of marker:  " + markerTitle);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Favorites");
        ref.child(markerTitle).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get marker info

                Marker markerTitle2 = (Marker) snapshot.child("Markers").getValue();
                modelFavorite.setFavorite(true);
                modelFavorite.setMarkerTitle(markerTitle2);

                

                //set data to view
                holder.markerTitle.setText(markerTitle2.getTitle());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return favoriteList.size(); //return list Size
    }


    //ViewHolder class
    class HolderMarkerFavorite extends RecyclerView.ViewHolder{

         //RelativeLayout marker;
         TextView markerTitle;
         ImageButton removeFromFavorites;

        public HolderMarkerFavorite(@NonNull View itemView) {
            super(itemView);

            //init views of row
           // marker = binding.marker;
            markerTitle = binding.markerTitle;
            removeFromFavorites = binding.removeFromFavorites;


        }
    }
}
