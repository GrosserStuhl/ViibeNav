package com.example.indoorbeacon.app.controller;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.indoorbeacon.app.model.InfoActivity;
import com.example.indoorbeacon.app.model.Measurement;


/**
 * Created by TomTheBomb on 23.06.2015.
 */
public class Application{

    public MainActivity main;

    /*
    GUI elements
    Consider the UI is divided into three layers:
     ________________________________________
    |                                        |
    |                 Overview               |
    |________________________________________|
    |                                        |
    |               RSSIs around             |
    |________________________________________|
    |                                        |
    |           Navigation elements          |
    |________________________________________|
     */

    // Layer 1
    TextView totalAnchor,lastX,lastY,lastEtage;
    // Layer 2
    TextView anzahlBeaconView,tempRSSIsView;
    // Layer 3
    EditText x_koord, y_koord, etage;
    Handler layer3Handler;

    Handler calcMediansHandler;
    Measurement measurement;


    Application(MainActivity main){
        this.main = main;
        measurement = new Measurement();

        // initialize GUI elemnts
//        initGUI();
//        initHandler();
    }

//    private void initGUI(){
//        // Layer 1
//        totalAnchor = (TextView) main.findViewById(R.id.totalAnchor);
//        lastX = (TextView) main.findViewById(R.id.lastX);
//        lastY = (TextView) main.findViewById(R.id.lastY);
//        lastEtage = (TextView) main.findViewById(R.id.lastEtage);
//
//        // Layer 2
//        anzahlBeaconView = (TextView) main.findViewById(R.id.anzahlBeaconFeld);
//        tempRSSIsView= (TextView) main.findViewById(R.id.tempRSSIFeld);
//        // Layer 3
//        x_koord = (EditText) main.findViewById(R.id.x_koord);
//        x_koord.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                // If the event is a key-down event on the "enter" button
//                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
//                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                    main.getRadioMap().setPosition_x(Integer.valueOf(x_koord.getText().toString()));
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        y_koord = (EditText) main.findViewById(R.id.y_koord);
//        y_koord.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                // If the event is a key-down event on the "enter" button
//                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
//                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                    main.getRadioMap().setPosition_y(Integer.valueOf(y_koord.getText().toString()));
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        etage = (EditText) main.findViewById(R.id.etage);
//        etage.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                // If the event is a key-down event on the "enter" button
//                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
//                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                    // Perform action on key press
//                    //e.g display a toast of editTexts content
////                    Toast.makeText(main.getBaseContext(),x_koord.getText(), Toast.LENGTH_SHORT).show();
//
//                    main.getRadioMap().setFloor(Integer.valueOf(etage.getText().toString()));
////                    Log.d("Measurement", etage.getText().toString());
//                    return true;
//                }
//                return false;
//            }
//        });
//
//    }
//
//    private void initHandler(){
//        layer3Handler = new Handler(){
//            @Override
//            public void handleMessage(Message msg) {
//                updateLayer3();
//            }
//        };
//        calcMediansHandler = new Handler(){
//            @Override
//            public void handleMessage(Message msg) {
//                // sets device state to measuring, which deactivates GUI elements
//                measurement.setState(Measurement.State.isMeasuring);
//
//                ArrayList<OnyxBeacon> calcBeacons = new ArrayList<>();
//                int amountOfMeasuredBeacons = 0;
//                Iterator it = OnyxBeacon.filterSurroundingBeacons().iterator();
//                while (it.hasNext()) {
//                    amountOfMeasuredBeacons++;
//                    OnyxBeacon tmp = (OnyxBeacon) it.next();
//                    tmp.setMeasurementStarted(true);
//                    calcBeacons.add(tmp);
//                }
//
//                measurement.overallCalcProgress(System.currentTimeMillis(), calcBeacons, main);
//
//            }
//        };
//    }
//
//
//    /**
//     * By invoking this method you start median measurement for the beacons found nearby.
//     * It will only start median measurement for the beacons already listed in the onyxBeaconHashMap.
//     */
//    public void startMeasurement(View view){
//        calcMediansHandler.sendEmptyMessage(0);
//    }
//
//
//
//    public void updateLayer1(){
//        totalAnchor.setText(""+main.getRadioMap().size());
//        lastX.setText(""+main.getRadioMap().getLastAnchor().getCoordinate().getX());
//        lastY.setText(""+main.getRadioMap().getLastAnchor().getCoordinate().getY());
//        lastEtage.setText("" + main.getRadioMap().getLastAnchor().getCoordinate().getFloor());
//    }
//
//    public void updateLayer2(){
//        anzahlBeaconView.setText(""+OnyxBeacon.beaconMap.size());
//        String tempRSSIs = "";
//        Iterator it = OnyxBeacon.beaconMap.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry<String,OnyxBeacon> pair = (Map.Entry)it.next();
//            if(tempRSSIs.isEmpty())
//                tempRSSIs += "  "+pair.getValue().getRssi();
//            else
//                tempRSSIs +="|"+ pair.getValue().getRssi();
//        }
//        tempRSSIsView.setText(tempRSSIs);
//    }
//
//    private void updateLayer3(){
//        x_koord.setText("" + main.getRadioMap().getPosition_x());
//        y_koord.setText(""+main.getRadioMap().getPosition_y());
//        etage.setText("" + main.getRadioMap().getFloor());
//    }
//
//
//
//    public void clickUp(View view){
//
//
//        main.getRadioMap().setY_up();
//        Runnable r = new Runnable(){
//            @Override
//            public void run() {
//                long futureTime = System.currentTimeMillis()+200;
//                while(System.currentTimeMillis() < futureTime){
//                    synchronized(this){
//                        try{
//                            wait(futureTime-System.currentTimeMillis());
//                        }catch(Exception e){}
//                    }
//                }
//                layer3Handler.sendEmptyMessage(0);
//            }
//        };
//        Thread th = new Thread(r);
//        th.start();
//    }
//
//    public void clickRight(View view){
//
//
//        main.getRadioMap().setX_up();
//        Runnable r = new Runnable(){
//            @Override
//            public void run() {
//                long futureTime = System.currentTimeMillis()+200;
//                while(System.currentTimeMillis() < futureTime){
//                    synchronized(this){
//                        try{
//                            wait(futureTime-System.currentTimeMillis());
//                        }catch(Exception e){}
//                    }
//                }
//                layer3Handler.sendEmptyMessage(0);
//            }
//        };
//        Thread th = new Thread(r);
//        th.start();
//    }
//
//    public void clickDown(View view){
//
//
//        main.getRadioMap().setY_down();
//        Runnable r = new Runnable(){
//            @Override
//            public void run() {
//                long futureTime = System.currentTimeMillis()+200;
//                while(System.currentTimeMillis() < futureTime){
//                    synchronized(this){
//                        try{
//                            wait(futureTime-System.currentTimeMillis());
//                        }catch(Exception e){}
//                    }
//                }
//                layer3Handler.sendEmptyMessage(0);
//            }
//        };
//        Thread th = new Thread(r);
//        th.start();
//    }
//
//    public void clickLeft(View view){
//
//
//        main.getRadioMap().setX_down();
//        Runnable r = new Runnable(){
//            @Override
//            public void run() {
//                long futureTime = System.currentTimeMillis()+200;
//                while(System.currentTimeMillis() < futureTime){
//                    synchronized(this){
//                        try{
//                            wait(futureTime-System.currentTimeMillis());
//                        }catch(Exception e){}
//                    }
//                }
//                layer3Handler.sendEmptyMessage(0);
//            }
//        };
//        Thread th = new Thread(r);
//        th.start();
//    }
//
//    public void exportClicked(View view){
//        Intent intent = new Intent(main, ExportActivity.class);
//        main.startActivityForResult(intent, 0);
//        main.getBluetoothScan().stopScan();
//        // EVENTUELL?!
//        saveAnchorPointsToDatabase();
//    }
//
//    public void prefsClicked(View view){
//        Intent intent = new Intent(main, TestAreaActivity.class);
//        main.startActivityForResult(intent, 0);
//    }
//
//    private void saveAnchorPointsToDatabase(){
//        Iterator it = main.getRadioMap().getData().entrySet().iterator();
//        while(it.hasNext()){
//            Map.Entry<Coordinate,AnchorPoint> pair = (Map.Entry)it.next();
//            main.getDbHandler().addAnchor(pair.getValue());
//        }
//    }

    public void clickInfo(View view){
        Intent intent = new Intent(main, InfoActivity.class);
        main.startActivityForResult(intent, 0);
    }

    public Handler getLayer3Handler() {
        return layer3Handler;
    }
}
