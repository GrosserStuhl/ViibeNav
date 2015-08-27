package com.example.indoorbeacon.app.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.indoorbeacon.app.R;
import com.example.indoorbeacon.app.model.dbmodels.AnchorPointDBModel;


/**
 * Created by TomTheBomb on 24.07.2015.
 */
public class CustomListAnchorAdapter extends ArrayAdapter<AnchorPointDBModel> {

    public CustomListAnchorAdapter(Context context){
        super(context,0);

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater tomsInflater = LayoutInflater.from(getContext());
        View customView = tomsInflater.inflate(R.layout.testarea_anchors_custom_row, parent, false);

        AnchorPointDBModel a = getItem(position);
        TextView _id = (TextView) customView.findViewById(R.id._id);
        TextView _x = (TextView) customView.findViewById(R.id._x);
        TextView _y = (TextView) customView.findViewById(R.id._y);
        TextView _f = (TextView) customView.findViewById(R.id._f);
        TextView allBeacons = (TextView) customView.findViewById(R.id.allBeacons);

        _id.setText(""+ a.get_id());
        _x.setText("" + a.getCoord().getX()+" |");
        _y.setText(""+a.getCoord().getY()+" |");
        _f.setText(""+a.getCoord().getFloor()+" |");

        String in = "";
        for(int i : a.getBeaconIds())
            in += " "+i+" |";

        allBeacons.setText(in);
        return customView;
    }
}
