package com.example.indoorbeacon.app.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.indoorbeacon.app.R;
import com.example.indoorbeacon.app.model.DistanceCalculation;
import com.example.indoorbeacon.app.model.OnyxBeacon;
import com.example.indoorbeacon.app.model.Util;


/**
 * Created by TomTheBomb on 26.05.2015.
 */

public class CustomListAdapter extends ArrayAdapter<OnyxBeacon> {


    public CustomListAdapter(Context context){
        super(context,0);

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater tomsInflater = LayoutInflater.from(getContext());
        View customView = tomsInflater.inflate(R.layout.info_custom_row, parent, false);

        OnyxBeacon beaconItem = getItem(position);
        TextView macAdressText = (TextView) customView.findViewById(R.id.macAdressText);
        TextView majMinText = (TextView) customView.findViewById(R.id.majMinText);
        TextView distancingText = (TextView) customView.findViewById(R.id.distancingText);
        ImageView onyxImage = (ImageView) customView.findViewById(R.id.onyxImage);

        macAdressText.setText(beaconItem.getMacAddress().toString());
        majMinText.setText("Major: "+beaconItem.getMajor()+", Minor: "+beaconItem.getMinor());
        distancingText.setText("FSPL: "+ Util.twoDecimals(DistanceCalculation.calculateDistanceFromFreeSpacePathLossModel((double) beaconItem.getRssi(), beaconItem.getTxPower()))+"m | Regr."+ Util.twoDecimals(DistanceCalculation.calculateDistanceFromBestFitRegression((double) beaconItem.getRssi(), beaconItem.getTxPower()))+"m");
        onyxImage.setImageResource(R.mipmap.onyx_transparent);

        return customView;
    }
}
