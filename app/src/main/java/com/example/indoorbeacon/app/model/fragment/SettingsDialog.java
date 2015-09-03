package com.example.indoorbeacon.app.model.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.indoorbeacon.app.R;
import com.example.indoorbeacon.app.model.Definitions;

/**
 * Created by TomTheBomb on 29.08.2015.
 */
public class SettingsDialog extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View v = inflater.inflate(R.layout.dialog_settings, null);
        final EditText FP_AMT_THRESH = (EditText) v.findViewById(R.id.FP_AMT_THRESH);
        final EditText OF_AMT_THRESH = (EditText) v.findViewById(R.id.OF_AMT_THRESH);
        final EditText FSPL_CONST = (EditText) v.findViewById(R.id.FSPL_const);

        FP_AMT_THRESH.setText(""+ Definitions.MEASUREMENT_AMT_THRESHOLD);
        FSPL_CONST.setText(""+ Definitions.FSPL_ELEMENT);
        OF_AMT_THRESH.setText(""+ Definitions.ON_THE_FLY_AMT_THRESHOLD);

        builder.setView(v);
        // Add action buttons
        builder.setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                if(!isEmpty(FSPL_CONST))
                    Definitions.FSPL_ELEMENT = Float.parseFloat(FSPL_CONST.getText().toString());
                if(!isEmpty(FP_AMT_THRESH))
                    Definitions.MEASUREMENT_AMT_THRESHOLD = Integer.valueOf(FP_AMT_THRESH.getText().toString());
                if(!isEmpty(OF_AMT_THRESH))
                    Definitions.ON_THE_FLY_AMT_THRESHOLD = Integer.valueOf(OF_AMT_THRESH.getText().toString());
            }
        });
        builder.setNegativeButton(R.string.cancel_info, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SettingsDialog.this.getDialog().cancel();
            }
        });
        return builder.create();
    }

    private boolean isEmpty(EditText t){
        return t.getText().toString().isEmpty();
    }
}
