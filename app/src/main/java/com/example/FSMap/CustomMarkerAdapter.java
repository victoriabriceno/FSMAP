package com.example.FSMap;

import android.content.Context;
import android.content.Intent;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class CustomMarkerAdapter extends RecyclerView.Adapter<CustomMarkerAdapter.CustomMarkerViewHolder> {


    Context context;
    ArrayList<CustomMarkersList> listCustomMarkers;

    MapsActivity mapsActivity;

    //Constructor
    public CustomMarkerAdapter(Context context, ArrayList<CustomMarkersList> customMarkersLists, MapsActivity mapsActivity) {
        this.context = context;
        this.listCustomMarkers = customMarkersLists;
        this.mapsActivity = mapsActivity;
    }

    @NonNull
    @Override
    public CustomMarkerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_custommarker, parent, false);
        return new CustomMarkerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomMarkerAdapter.CustomMarkerViewHolder holder, int position) {

        CustomMarkersList customMarkersList = listCustomMarkers.get(position);
        holder.TitleOfTheMarker.setText(customMarkersList.getMarkerTitle());
        holder.OriginalOfTheMarker.setText(customMarkersList.getOrginalNameCustom());
        int floor = customMarkersList.floor;
        LatLng postionCustom = customMarkersList.position;
        holder.markerclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String markerTitle = holder.TitleOfTheMarker.getText().toString();

                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("marker", markerTitle);
                context.startActivity(intent);
            }
        });

        holder.trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String markerTitleCustom= holder.OriginalOfTheMarker.getText().toString();
                CustomMarker.removeFromCustom(context,markerTitleCustom);
                int size = listCustomMarkers.size();
                listCustomMarkers.clear();
                notifyItemRangeRemoved(0,size);

            }
        });

        holder.TitleOfTheMarker.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH|| actionId == EditorInfo.IME_ACTION_DONE||
                        event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER){

                    holder.TitleOfTheMarker.setKeyListener(null);


                   CustomMarker.renameCUstomMarkers(context,holder.TitleOfTheMarker.getText().toString(),holder.OriginalOfTheMarker.getText().toString(),floor ,postionCustom);
//                    int size = listFavorite.size();
//                    listFavorite.clear();
//                    notifyItemRangeInserted(0,size);



                }

                return false;
            }
        });

        holder.TitleOfTheMarker.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // Hide soft keyboard.
                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(holder.TitleOfTheMarker.getWindowToken(), 0);
                    // Make it non-editable again.
                    holder.TitleOfTheMarker.setKeyListener(null);
                }
            }
        });

        holder.pencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                holder.TitleOfTheMarker.setKeyListener(holder.originalKeyListener);
                holder.TitleOfTheMarker.requestFocus();
                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(holder.TitleOfTheMarker, InputMethodManager.SHOW_IMPLICIT);
                holder.TitleOfTheMarker.setSelection(holder.TitleOfTheMarker.getText().length());


            }
        });


    }

    @Override
    public int getItemCount() {
        return listCustomMarkers.size();
    }

    public static class CustomMarkerViewHolder extends RecyclerView.ViewHolder {

        EditText TitleOfTheMarker,OriginalOfTheMarker;
        ImageButton trash,pencil;
        RelativeLayout markerclick;

        KeyListener originalKeyListener;

        public CustomMarkerViewHolder(@NonNull View itemView) {
            super(itemView);

            TitleOfTheMarker = itemView.findViewById(R.id.TitleMarker1);
            OriginalOfTheMarker = itemView.findViewById(R.id.TitleMarker2);
            trash = itemView.findViewById(R.id.trashCustom);
            pencil = itemView.findViewById(R.id.editCustom);
            markerclick = itemView.findViewById(R.id.marker);
            originalKeyListener = TitleOfTheMarker.getKeyListener();
            TitleOfTheMarker.setKeyListener(null);
            //removeStar = itemView.findViewById(R.id.removeFromFavorites);

        }
    }

}
