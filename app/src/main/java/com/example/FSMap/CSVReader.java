package com.example.FSMap;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CSVReader {


    ArrayList<String[]> roomlist = new ArrayList<String[]>();

    public ArrayList<String[]> GetRoomList() {
        return roomlist;
    }

    public void CreateRoomList(byte[] info) {

        if (info == null)
            return;
        String line = "";
        String splitBy = ",";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(info)));
            while ((line = br.readLine()) != null) {
                String[] roominfo = line.split(splitBy);
                roomlist.add(roominfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}