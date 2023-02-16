package com.example.logintesting;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CSVReader {


    InputStream csv;
    ArrayList<String[]> roomlist = new ArrayList<String[]>();

    public void SetFile(InputStream path){
        csv = path;
    }


    public ArrayList<String[]> GetRoomList (){
        return roomlist;
    }

    public void CreateRoomList() {
        if (csv == null)
            return;;
        String line = "";
        String splitBy = ",";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(csv));
            while ((line = br.readLine()) != null) {
                String[] roominfo = line.split(splitBy);
                roomlist.add(roominfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
