package com.example.indoorbeacon.app.model.dbmodels;

/**
 * Created by TomTheBomb on 27.08.2015.
 */
public class InfoModel {

    private int id;
    private String personName;
    private String roomName;
    private String environment;
    private String category;

    public InfoModel(int id, String personName, String roomName, String environment, String category) {
        this.id = id;
        this.personName = personName;
        this.roomName = roomName;
        this.environment = environment;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public String getPersonName() {
        return personName;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getCategory() {
        return category;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
