package com.example.indoorbeacon.app.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.indoorbeacon.app.R;
import com.example.indoorbeacon.app.model.dbmodels.AnchorPointDBModel;

import java.util.ArrayList;


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
        TextView id = (TextView) customView.findViewById(R.id.dbAnchorID);
        TextView coord = (TextView) customView.findViewById(R.id.dbCoord);
        TextView median = (TextView) customView.findViewById(R.id.dbAnchorMedian);
        TextView info = (TextView) customView.findViewById(R.id.dbAnchorInfoID);

        id.setText("" + a.get_id());
        coord.setText("" + a.getCoord().getFloor()+" | " + a.getCoord().getX()+" | " + a.getCoord().getY()+" | ");
        info.setText(""+a.getAddInfoID());

        String in = "";
        ArrayList<Integer> tmpAllMedians = a.getAllMediansFromAnchor();
        for(int i=0; i<tmpAllMedians.size();i++)
            if(i<tmpAllMedians.size() - 1)
                in += ""+tmpAllMedians.get(i)+",";
            else
                in += ""+ tmpAllMedians.get(i);

        median.setText(in);
        return customView;
    }
}
