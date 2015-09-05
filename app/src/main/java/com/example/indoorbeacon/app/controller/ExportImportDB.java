package com.example.indoorbeacon.app.controller;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.indoorbeacon.app.R;
import com.example.indoorbeacon.app.model.dbmodels.DBHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;


/**
 * Created by TomTheBomb on 04.09.2015.
 */
public class ExportImportDB extends Activity {

    public static final String TAG = "ExportImportDB";
    public static final String SAVE_DB_FILENAME = "radiomap.db";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);
    }

    public void importClicked(View view){
        importDB();
    }


    //importing database
    private void importDB() {
        try {
            String currentDBPath = DBHandler.getDB().getDBPath();
            String backupDBPath  = Environment.getExternalStorageDirectory() + "/radiomap/radiomap.db";
            File backupDB = new File(backupDBPath);
//            boolean readable = backupDB.setReadable(true, false);
//            Log.d(TAG, "Backup readable: "+readable);

            File currentDB  = new File(currentDBPath);
            currentDB.delete();
            currentDB.getParentFile().mkdirs();

            FileChannel src = new FileInputStream(backupDB).getChannel();
            FileChannel dst = new FileOutputStream(currentDB).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
            Toast.makeText(getBaseContext(), currentDB.toString()+" worked! ",
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.e("ERROR",e.toString());
            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG)
                    .show();
        }
    }

}
