package com.example.indoorbeacon.app.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.example.indoorbeacon.app.R;
import com.example.indoorbeacon.app.model.dbmodels.MedianDBModel;

/**
 * Created by TomTheBomb on 25.07.2015.
 */
public class CustomListMedianAdapter  extends ArrayAdapter<MedianDBModel> {
    public CustomListMedianAdapter(Context context){
        super(context,0);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater tomsInflater = LayoutInflater.from(getContext());
        View customView = tomsInflater.inflate(R.layout.testarea_medians_custom_row, parent, false);

        MedianDBModel m = getItem(position);
        TextView id_median = (TextView) customView.findViewById(R.id.id_medians);
        TextView median_median = (TextView) customView.findViewById(R.id.median_median);
        TextView macAddress = (TextView) customView.findViewById(R.id.median_macAddress);

        id_median.setText(""+ m.get_id());
        median_median.setText("" + m.getMedian()+" |");
        macAddress.setText(""+m.getMacAddress());


        return customView;
    }
}
