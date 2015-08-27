package com.example.indoorbeacon.app.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.indoorbeacon.app.R;
import com.example.indoorbeacon.app.model.dbmodels.OnyxBeaconDBModel;

/**
 * Created by TomTheBomb on 24.07.2015.
 */
public class CustomListBeaconAdapter extends ArrayAdapter<OnyxBeaconDBModel> {

    public CustomListBeaconAdapter(Context context){
        super(context,0);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater tomsInflater = LayoutInflater.from(getContext());
        View customView = tomsInflater.inflate(R.layout.testarea_beacons_custom_row, parent, false);

        OnyxBeaconDBModel o = getItem(position);
        TextView _id = (TextView) customView.findViewById(R.id.id_beacons);
        TextView major = (TextView) customView.findViewById(R.id.major);
        TextView minor = (TextView) customView.findViewById(R.id.minor);
        TextView macAddress = (TextView) customView.findViewById(R.id.macAddress);

        _id.setText(""+ o.get_id());
        major.setText("" + o.getMajor()+" |");
        minor.setText(""+o.getMinor()+" |");
        macAddress.setText(""+o.getMacAddress());


        return customView;
    }
}
