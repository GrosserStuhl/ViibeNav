package com.example.indoorbeacon.app.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.example.indoorbeacon.app.R;
import com.example.indoorbeacon.app.model.dbmodels.InfoDBModel;

/**
 * Created by Dima on 27/07/2015.
 */
public class CustomResultListAdapter extends ArrayAdapter<InfoDBModel> {

    public CustomResultListAdapter(Context context, InfoDBModel[] items) {
        super(context, R.layout.custom_listitem, items);
    }

    static class ViewHolder {
        private Button listButton;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;

        if (convertView == null) {
            mViewHolder = new ViewHolder();

//            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LayoutInflater inflater = LayoutInflater.from(getContext());
//            View customView = inflater.inflate(R.layout.custom_listitem, parent, false);
            convertView = inflater.inflate(R.layout.custom_listitem, parent, false);

//            String item = getItem(position);
//            Log.i("MyApp", item);
//            Button button = (Button) convertView.findViewById(R.id.roomlistButton);
//            button.setText(Html.fromHtml(item));

            mViewHolder.listButton = (Button) convertView.findViewById(R.id.roomlistButton);
            convertView.setTag(mViewHolder);

        } else mViewHolder = (ViewHolder) convertView.getTag();

        InfoDBModel item = getItem(position);
//        mViewHolder.listButton.setText(Html.fromHtml(item));
        mViewHolder.listButton.setText(item.getPerson_name()+System.lineSeparator()+item.getRoom_name());
//        mViewHolder.listButton.setFocusable(false);
//        mViewHolder.listButton.setClickable(false);

        return convertView;
    }
}
