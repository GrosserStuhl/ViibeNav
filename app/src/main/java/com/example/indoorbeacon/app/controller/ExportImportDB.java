package com.example.indoorbeacon.app.controller;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import com.example.indoorbeacon.app.model.dbmodels.DBHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;


/**
 * Created by TomTheBomb on 04.09.2015.
 */
public class ExportImportDB {

    public static final String TAG = "ExportImportDB";
    public static final String SAVE_DB_FILENAME = "radiomap.db";
    private Context context;

    public ExportImportDB(Context context) {
        this.context = context;
    }

    //importing database
    public void importDB() {
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
            Toast.makeText(context, currentDB.toString()+" worked! ",
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.e("ERROR",e.toString());
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG)
                    .show();
        }
    }

}
