package com.example.indoorbeacon.app.controller;

import android.content.Intent;
import android.view.View;

import com.example.indoorbeacon.app.model.InfoActivity;
import com.example.indoorbeacon.app.model.Measurement;

/**
 * Created by TomTheBomb on 23.06.2015.
 */
public class Application {

    public MainActivity main;


    Measurement measurement;



    Application(MainActivity main){
        this.main = main;
        measurement = new Measurement();
    }




    public void clickInfo(View view){
        Intent intent = new Intent(main, InfoActivity.class);
        main.startActivityForResult(intent, 0);
    }

}
