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

import java.util.ArrayList;

public class AdapterUserFavoriteList extends RecyclerView.Adapter<AdapterUserFavoriteList.FavoriteViewHolder> {


    Context context;
    ArrayList<UserFavoriteList> listFavorite;
    MapsActivity mapsActivity;
    Favorites favorites;
    EditProfile editProfile;
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
        holder.orginalName.setText(userFavoriteList.getOriginalTitle());

        // Marker TO TAKE YOU TO MAP
        holder.markerClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               String markerTitle = holder.orginalName.getText().toString();

               Intent intent = new Intent(context, MapsActivity.class);
               intent.putExtra("marker_ToMap",markerTitle);
               context.startActivity(intent);

            }
        });

        holder.trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String markerTitle = holder.orginalName.getText().toString();

                Favorites.removeFromFavorite(context,markerTitle);
                int size = listFavorite.size();
                listFavorite.clear();
                notifyItemRangeRemoved(0,size);



            }
        });



        holder.TitleOfTheMarker.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH|| actionId == EditorInfo.IME_ACTION_DONE||
                event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER){

                    holder.TitleOfTheMarker.setKeyListener(null);


                    Favorites.renameMarker(context,holder.TitleOfTheMarker.getText().toString(),holder.orginalName.getText().toString());
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

        holder.editFavorites.setOnClickListener(new View.OnClickListener() {
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
        return listFavorite.size();
    }
    public static class FavoriteViewHolder extends RecyclerView.ViewHolder{

        EditText TitleOfTheMarker,orginalName;
        ImageButton trash;
        RelativeLayout markerClick;
        ImageButton editFavorites;
        KeyListener originalKeyListener;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);

            TitleOfTheMarker = itemView.findViewById(R.id.TitleMarker);
            markerClick = itemView.findViewById(R.id.marker);
            trash = itemView.findViewById(R.id.trashFavorites);
            editFavorites = itemView.findViewById(R.id.editFavorites);
            orginalName = itemView.findViewById(R.id.OriginalTitle);
            originalKeyListener = TitleOfTheMarker.getKeyListener();
            TitleOfTheMarker.setKeyListener(null);
        }



    }

}
