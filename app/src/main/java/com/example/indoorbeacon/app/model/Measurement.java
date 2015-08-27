package com.example.indoorbeacon.app.model;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.example.indoorbeacon.app.controller.MainActivity;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * Created by TomTheBomb on 12.07.2015.
 */
public class Measurement {

    public static final String TAG = "Measurement";

    public MainActivity main;
    public Measurement.State state;

    public enum State{
        isMeasuring,notMeasuring;
    };

    private int measurementSize;
    private long start;

    public void overallCalcProgress(final long start, final ArrayList<OnyxBeacon> beacons, final MainActivity main){
            this.main = main;
            this.start = start;

            measurementSize = beacons.size();
        new AsyncMeasure().execute(beacons);
    }

    public class AsyncMeasure extends AsyncTask<ArrayList<OnyxBeacon>, Integer, String> {

        ArrayList<OnyxBeacon> beacons;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(main);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setTitle("Messung von " + measurementSize + " Beacons");
            dialog.setMax(measurementSize);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cleanUp();
                }
            });
            dialog.show();
        }

        @Override
        protected String doInBackground(ArrayList<OnyxBeacon>... params) {
            ArrayList<OnyxBeacon> beacons = params[0];
            this.beacons = (ArrayList<OnyxBeacon>)beacons.clone();
            while(isMeasuring()) {
                Iterator<OnyxBeacon> it = beacons.iterator();
                while (it.hasNext()) {
                    if (it.next().isCalculationDone()) {
                        publishProgress(1);
                        it.remove();
                    }
                }

                // break out if list is empty = all calcs are done
                if(beacons.isEmpty())
                    setState(State.notMeasuring);
            }
            dialog.dismiss();

            return "";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            dialog.incrementProgressBy(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
                long ende = System.currentTimeMillis()-start;
                Log.d(TAG, "Dauer: " + ende / 1000 + "s");

//                AnchorPoint a = new AnchorPoint(main.getRadioMap().getCoordinate(),this.beacons);
//                main.getRadioMap().add(a);
//                main.getRadioMap().setLastAnchor(a);
//                main.getApplicationUI().updateLayer1();
//                DBHandler.getDB().addAnchor(a);
                cleanUp();
        }

        private void cleanUp(){
            for(OnyxBeacon b : this.beacons) {
                b.resetMedianMeasurement();
            }
        }

    }

    public void setState(State state) {
        Log.d(TAG, "STATE: "+state);
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public boolean isMeasuring(){
       if(getState().equals(State.isMeasuring))
           return true;
        return false;
    }




    // AREA FOR ON THE FLY MEASUREMENT

    private Person person;

    /**
     * Only Call this function for on the Fly measurement to identify position - NOT for saving data in the RadioMap (For saving data to RadioMap use overallCalcProcess())
     * @param beacons
     * @param
     */
    public void overallOnTheFlyCalcProcess(final ArrayList<OnyxBeacon> beacons, Person person){
        this.person = person;
        this.measurementSize = beacons.size();

        new AsyncOnTheFlyMeasure().execute(beacons);
    }

    public class AsyncOnTheFlyMeasure extends AsyncTask<ArrayList<OnyxBeacon>, Integer, String> {

        ArrayList<OnyxBeacon> beacons;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(person.getTest());
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setTitle("Messung von " + measurementSize + " Beacons");
            dialog.setMax(measurementSize);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cleanUp();
                }
            });
            dialog.show();
        }

        @Override
        protected String doInBackground(ArrayList<OnyxBeacon>... params) {
            ArrayList<OnyxBeacon> beacons = params[0];
            this.beacons = (ArrayList<OnyxBeacon>)beacons.clone();
            while(isMeasuring()) {
                Iterator<OnyxBeacon> it = beacons.iterator();
                while (it.hasNext()) {
                    if (it.next().onTheFlyDone()) {
                        publishProgress(1);
                        it.remove();
                    }
                }

                // break out if list is empty = all calcs are done
                if(beacons.isEmpty())
                    setState(State.notMeasuring);
            }
            dialog.dismiss();

            return "";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            dialog.incrementProgressBy(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            // OLD CODE OLD CODE OLD CODE OLD CODE
            // person.getSupposedAnchorIds(beacons);

//            MacToMedian[] data = Util.listToMacToMedianArr(beacons);
//            person.estimatePos(data);
            cleanUp();
            person.checkLoop();
        }

        private void cleanUp(){
            for(OnyxBeacon b : this.beacons)
                b.resetOnTheFlyMeasurement();

        }

    }

}
