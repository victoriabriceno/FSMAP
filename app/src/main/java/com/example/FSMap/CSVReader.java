package com.example.FSMap;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

//This class handles reading in the remote CSV to generate the markers at their
// designated location with their defined type
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
            //Read line by line, each line being split into a String array and finally added to the list
            while ( (line = br.readLine()) != null ) {
                String[] roominfo = line.split(splitBy);
                roomlist.add(roominfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}