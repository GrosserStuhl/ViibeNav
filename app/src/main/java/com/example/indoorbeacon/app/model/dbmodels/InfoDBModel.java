package com.example.indoorbeacon.app.model.dbmodels;

import java.util.ArrayList;

/**
 * Created by TomTheBomb on 27.08.2015.
 */
public class InfoDBModel {

    private int id;
    private String person_name;
    private String room_name;
    private String environment;
    private String category;

    private static ArrayList<InfoDBModel> allInfo;

    public InfoDBModel(int id, String person_name, String room_name, String environment, String category) {
        this.id = id;
        this.person_name = person_name;
        this.room_name = room_name;
        this.environment = environment;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public String getPerson_name() {
        return person_name;
    }

    public String getRoom_name() {
        return room_name;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getCategory() {
        return category;
    }

    public static ArrayList<InfoDBModel> getAllInfo() {
        return allInfo;
    }

    public static void setAllInfo(ArrayList<InfoDBModel> allInfo) {
        InfoDBModel.allInfo = allInfo;
    }
}
