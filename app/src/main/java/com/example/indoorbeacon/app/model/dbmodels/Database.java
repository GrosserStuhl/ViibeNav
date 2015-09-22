package com.example.indoorbeacon.app.model.dbmodels;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.indoorbeacon.app.model.Coordinate;
import com.example.indoorbeacon.app.model.Definitions;
import com.example.indoorbeacon.app.model.Util;
import com.example.indoorbeacon.app.model.position.neighbor.DeviationToCoord;
import com.example.indoorbeacon.app.model.position.neighbor.MacToMedian;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;


/**
 * Created by TomTheBomb on 14.07.2015.
 */
public class Database extends SQLiteOpenHelper {

    private static final String TAG = "Database";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "radiomap.db";

    // Fingerprint TABLE
    public static final String TABLE_FINGERPRINT = "fingerprint";
    public static final String FINGERPRINT_COLUMN_ID = "fingerprintid";
    public static final String COLUMN_FLOOR = "floor";
    public static final String COLUMN_X = "x";
    public static final String COLUMN_Y = "y";
    public static final String COLUMN_INFO_ID = "fingerprint_infoid";

    public static final String TABLE_FP_HAS_MEDIAN = "fp_has_median";
    public static final String FP_HAS_MEDIAN_COLUMN_ID = "fp_has_medianid";
    public static final String COLUMN_MEDIAN_ID = "fp_has_median_medianid";
    public static final String COLUMN_FINGERPRINT_ID = "fp_has_median_fingerprintid";

    // MEDIANS TABLE
    public static final String TABLE_MEDIAN = "median";
    public static final String MEDIAN_COLUMN_ID = "medianid";
    public static final String COLUMN_MEDIAN_VALUE = "median";
    public static final String COLUMN_BEACON_ID = "median_beaconid";
    public static final String COLUMN_ORIENTATION ="orientation";

    // BEACONS TABLE
    public static final String TABLE_BEACON = "beacon";
    public static final String BEACON_COLUMN_ID = "beaconid";
    public static final String COLUMN_MAJOR = "major";
    public static final String COLUMN_MINOR = "minor";
    public static final String COLUMN_MAC_ADDRESS = "macAddress";
    public static final String COLUMN_UUID = "uuid";

    // INFO TABLE
    public static final String TABLE_INFO = "info";
    public static final String INFO_COLUMN_ID = "infoid";
    public static final String COLUMN_PERSON_NAME = "personname";
    public static final String COLUMN_ROOM_NAME = "roomname";
    public static final String COLUMN_ENVIRONMENT = "environment";
    public static final String COLUMN_CATEGORY = "category";

    private static Database singleton;

    private Context context;

    private Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);

        this.context = context;
    }

    public static Database createDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        // Avoid possible errors with multiple threads accessing this method -> synchronized
        synchronized(Database.class) {
            if (singleton == null) {
                singleton = new Database(context, name, factory, version);
            }
        }
        return singleton;
    }

    public static Database getDB(){
        return singleton;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("SELECT load_extension('./libsqlitefunctions.so')");

        // CREATE BEACONS TABLE
        String query1 = "CREATE TABLE "+ TABLE_BEACON + "(" +
                "'"+ BEACON_COLUMN_ID +"'"+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "'"+ COLUMN_MAC_ADDRESS +"'"+ " TEXT, " +
                "'"+ COLUMN_MAJOR+"'"+ " INTEGER, "+
                "'"+ COLUMN_MINOR +"'"+ " INTEGER UNIQUE, "+
                "'"+ COLUMN_UUID +"'"+ " TEXT "+
                ");";
        db.execSQL(query1);

        // CREATE MEDIANS TABLE
        String query2 = "CREATE TABLE "+ TABLE_MEDIAN + "(" +
                "'"+ MEDIAN_COLUMN_ID +"'"+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "'"+ COLUMN_MEDIAN_VALUE+"'"+ " INTEGER, "+
                "'"+ COLUMN_BEACON_ID +"'"+ " INTEGER, "/*FOREIGN KEY REFERENCES "+TABLE_BEACON+"("+BEACONS_COLUMN_ID+")"*/+
                "'"+ COLUMN_ORIENTATION +"'"+ " TEXT "+
                ");";
        db.execSQL(query2);

        // CREATE FINGERPRINT TABLE
        String query3 = "CREATE TABLE "+ TABLE_FINGERPRINT + "(" +
                "'"+ FINGERPRINT_COLUMN_ID +"'"+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "'"+ COLUMN_FLOOR +"'"+ " INTEGER, "+
                "'"+ COLUMN_X +"'"+ " INTEGER, "+
                "'"+ COLUMN_Y +"'"+ " INTEGER, "+
                "'"+ COLUMN_INFO_ID +"'"+ " INTEGER  " +
                ");";
        db.execSQL(query3);

        // CREATE INFO TABLE
        String query4 = "CREATE TABLE "+ TABLE_INFO + "(" +
                "'"+ INFO_COLUMN_ID +"'"+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "'"+ COLUMN_PERSON_NAME +"'"+ " TEXT, "+
                "'"+ COLUMN_ROOM_NAME +"'"+ " TEXT, "+
                "'"+ COLUMN_ENVIRONMENT +"'"+ " TEXT, "+
                "'"+ COLUMN_CATEGORY+"'"+ " TEXT "+
                ");";
        db.execSQL(query4);

        // CREATE FP_HAS_MEDIAN TABLE
        String query5 = "CREATE TABLE "+ TABLE_FP_HAS_MEDIAN + "(" +
                "'"+ FP_HAS_MEDIAN_COLUMN_ID +"'"+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "'"+ COLUMN_MEDIAN_ID +"'"+ " INTEGER, "+
                "'"+ COLUMN_FINGERPRINT_ID +"'"+ " INTEGER "+
                ");";
        db.execSQL(query5);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_FINGERPRINT);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_MEDIAN);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_BEACON);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_INFO);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_FP_HAS_MEDIAN);
        onCreate(db);
    }

    public Coordinate getTarget(String targetString){
        SQLiteDatabase db = getWritableDatabase();
        final String query = "SELECT " + COLUMN_X + "," + COLUMN_Y + " FROM '" + TABLE_FINGERPRINT + "'"+
                " JOIN '" + TABLE_INFO + "' ON " + TABLE_FINGERPRINT + "."+COLUMN_INFO_ID +"="+ TABLE_INFO+"."+INFO_COLUMN_ID+" WHERE ("+
                COLUMN_PERSON_NAME + " LIKE '%"+targetString+"%' OR " +
                COLUMN_ROOM_NAME + " LIKE '%"+targetString+"%');";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        int x = -1;
        int y = -1;
        int floor = -1;

        Coordinate coord = new Coordinate(floor,x,y);

        while (!c.isAfterLast()) {
            if(!c.isNull(c.getColumnIndex(COLUMN_X)))
                coord.setX(c.getInt(c.getColumnIndex(COLUMN_X)));
            if (!c.isNull(c.getColumnIndex(COLUMN_Y)))
                coord.setY(c.getInt(c.getColumnIndex(COLUMN_Y)));
            c.moveToNext();
        }
        c.close();
        db.close();
        return coord;
    }


    public ArrayList<DeviationToCoord> getAllDistancesFromMedians(MacToMedian[] map) {
        SQLiteDatabase db = getWritableDatabase();

        Log.d(TAG,"How big is mactomedian: "+map.length);

        ArrayList<DeviationToCoord> devsToCoords = new ArrayList<>();
        final String LOCAL_COLUMN_DEVIATION = "deviation";

        for (int i = 0; i < map.length; i++) {
            final String macAddress = map[i].getMacAddressStr();

//            String queryOrientation = "";
//            if (map[i].getOrientation().equals(Orientation.back))
//                queryOrientation = "( " + TABLE_FINGERPRINT + "." + COLUMN_BACK + " = " + TABLE_FP_HAS_MEDIAN + "." + BEACON_MEDIAN_TO_ANCHOR_ID + " ) ";
//            else if (map[i].getOrientation().equals(Orientation.front))
//                queryOrientation = "( " + TABLE_FINGERPRINT + "." + COLUMN_FRONT + " = " + TABLE_FP_HAS_MEDIAN + "." + BEACON_MEDIAN_TO_ANCHOR_ID + " ) ";
//
//            String query = "SELECT " + TABLE_FINGERPRINT + "." + COLUMN_FLOOR + "," + TABLE_FINGERPRINT + "." + COLUMN_X + ", " + TABLE_FINGERPRINT + "." + COLUMN_Y + "," + TABLE_MEDIAN + "." + MEDIANS_COLUMN_ID + ", " + calcManhattenDB_Cmd(median) + " AS " + LOCAL_COLUMN_DEVIATION + " " +
//                    " FROM '" + TABLE_MEDIAN + "' JOIN '" + TABLE_FINGERPRINT + "' JOIN '" + TABLE_FP_HAS_MEDIAN + "' WHERE macAddress = '" + macAddress + "' AND " +
//                    " ( " + TABLE_FP_HAS_MEDIAN + "." + COLUMN_BEACON_1 + " = " + TABLE_MEDIAN + "." + MEDIANS_COLUMN_ID + " " +
//                    "   OR " + TABLE_FP_HAS_MEDIAN + "." + COLUMN_BEACON_2 + " = " + TABLE_MEDIAN + "." + MEDIANS_COLUMN_ID + "  " +
//                    "   OR " + TABLE_FP_HAS_MEDIAN + "." + COLUMN_BEACON_3 + " = " + TABLE_MEDIAN + "." + MEDIANS_COLUMN_ID + "  " +
//                    "   OR " + TABLE_FP_HAS_MEDIAN + "." + COLUMN_BEACON_4 + " = " + TABLE_MEDIAN + "." + MEDIANS_COLUMN_ID + "  " +
//                    "   OR " + TABLE_FP_HAS_MEDIAN + "." + COLUMN_BEACON_5 + " = " + TABLE_MEDIAN + "." + MEDIANS_COLUMN_ID + "  " +
//                    "   OR " + TABLE_FP_HAS_MEDIAN + "." + COLUMN_BEACON_6 + " = " + TABLE_MEDIAN + "." + MEDIANS_COLUMN_ID + "  " +
//                    "  ) AND " + queryOrientation +
//                    " GROUP BY " + COLUMN_MEDIAN_VALUE + " HAVING deviation <=" + Definitions.POSITIONING_THRESHOLD + " ORDER BY " + LOCAL_COLUMN_DEVIATION + " ASC LIMIT " + Definitions.POSITIONING_LIMIT + ";";

//            String query = "SELECT " + TABLE_FINGERPRINT + "." + COLUMN_FLOOR + "," + TABLE_FINGERPRINT + "." + COLUMN_X + ", " + TABLE_FINGERPRINT + "." + COLUMN_Y + ", " +
//                    TABLE_BEACON + "." + COLUMN_MAC_ADDRESS + ", " + TABLE_MEDIAN + "."+MEDIAN_COLUMN_ID+", MIN(ABS("+TABLE_MEDIAN+"."+COLUMN_MEDIAN_VALUE+" - "+map[i].getMedian()+")) AS " + LOCAL_COLUMN_DEVIATION + " " +
//                    " FROM '" + TABLE_FP_HAS_MEDIAN + "' JOIN '" + TABLE_FINGERPRINT + "' ON "+COLUMN_FINGERPRINT_ID+"="+FINGERPRINT_COLUMN_ID+" JOIN '" +
//                    TABLE_MEDIAN + "' ON "+COLUMN_MEDIAN_ID+"="+MEDIAN_COLUMN_ID+ " JOIN '"+TABLE_BEACON+"' ON "+COLUMN_BEACON_ID+"="+BEACON_COLUMN_ID +
//                    " WHERE "+COLUMN_MAC_ADDRESS+" = '" + macAddress + "' AND "+ COLUMN_ORIENTATION + " = '" + map[i].getOrientation().toString()+"'"+
//                    " GROUP BY " + MEDIAN_COLUMN_ID + " HAVING "+LOCAL_COLUMN_DEVIATION+" <=" + Definitions.POSITIONING_THRESHOLD + " ORDER BY " + LOCAL_COLUMN_DEVIATION + " ASC LIMIT " + Definitions.POSITIONING_LIMIT + ";";

            String query = "SELECT fingerprint.floor, fingerprint.x, fingerprint.y, beacon.macAddress, median.medianid, MIN(ABS(median.median+78)) as deviation FROM fp_has_median JOIN fingerprint ON fp_has_median.fp_has_median_fingerprintid = fingerprint.fingerprintid JOIN " +
                    " median ON fp_has_median.fp_has_median_medianid = median.medianid JOIN beacon ON median.median_beaconid = beacon.beaconid"+
                    " WHERE beacon.macAddress = '"+map[i].getMacAddressStr()+"' AND median.orientation='"+map[i].getOrientation()+"' GROUP by median.medianid HAVING deviation<="+ Definitions.POSITIONING_THRESHOLD+
                    " ORDER BY deviation ASC LIMIT "+Definitions.POSITIONING_LIMIT+";";

            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();

            int counter = 0;
            while (!c.isAfterLast()) {
                Coordinate coordinate = new Coordinate(c.getInt(c.getColumnIndex(COLUMN_FLOOR)), c.getInt(c.getColumnIndex(COLUMN_X)), c.getInt(c.getColumnIndex(COLUMN_Y)));

                float deviation = c.getInt(c.getColumnIndex(LOCAL_COLUMN_DEVIATION));
                devsToCoords.add(new DeviationToCoord(deviation, coordinate));
//                Log.d(TAG, "Deviation-Median" + c.getInt(c.getColumnIndex(MEDIANS_COLUMN_ID)) +
//                        " deviation: " + deviation +
//                        " -> Coord: " + coordinate + " macAddress " + macAddress);
                Log.d(TAG,"Which deviation? "+deviation);
                counter++;
                c.moveToNext();
            }

            Log.d(TAG,"How many results from one beacon? "+counter+ " FROM MEDIAN "+map[i].getMedian());

            c.close();
//            Log.d(TAG, "How many deviations from DB: " + devsToCoords.size());
//            Log.d(TAG,"QUERY: "+query);
        }
        db.close();
        Log.d(TAG,"DEV TO COORDS SIZE: "+devsToCoords.size()+ " FROM "+map.length+ " Macs");
//        return devsToCoords.toArray(new DeviationToCoord[devsToCoords.size()]);
        return devsToCoords;
    }

    /**
     * Gets the most recent ID, which is the latest inserted entry for a specific TABLE
     *
     * @param db
     * @param TABLE
     * @param id
     * @return
     */
    public int getLastID(SQLiteDatabase db, final String TABLE, final String id) {
        final String query = "SELECT MAX(" + id + ") FROM '" + TABLE + "'";
        Cursor cur = db.rawQuery(query, null);
        cur.moveToFirst();
        int ID = cur.getInt(0);
        cur.close();
        return ID;
    }

    public Coordinate getCoordFromAnchorId(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COLUMN_X + ", " + COLUMN_Y + ", " + COLUMN_FLOOR + " FROM '" + TABLE_FINGERPRINT + "' WHERE " + FINGERPRINT_COLUMN_ID + " = '" + id + "';";

        // Cursor point to a location in your results
        Cursor c = db.rawQuery(query, null);
        // Move to the first row in your results
        c.moveToFirst();

        int x = -1;
        int y = -1;
        int floor = -1;

        while (!c.isAfterLast()) {
            x = c.getInt(c.getColumnIndex(COLUMN_X));
            y = c.getInt(c.getColumnIndex(COLUMN_Y));
            floor = c.getInt(c.getColumnIndex(COLUMN_FLOOR));
            c.moveToNext();
        }
        c.close();
        db.close();
        Coordinate coord = new Coordinate(floor, x, y);
        return coord;
    }


    public HashMap<Coordinate,InfoModel> getCoordinateToInfoModelMap() {
        HashMap<Coordinate,InfoModel> res = new HashMap<>();
        SQLiteDatabase db = getWritableDatabase();
        //SELECT _id,x,y,floor,personname,roomname,environment,category  FROM anchorpoints JOIN
//        info on anchorpoints.infoid = info.id
        String query = "SELECT "+ FINGERPRINT_COLUMN_ID +","+ COLUMN_X +","+ COLUMN_Y +","+ COLUMN_FLOOR + ","+
                INFO_COLUMN_ID + ","+ COLUMN_PERSON_NAME + ","+ COLUMN_ROOM_NAME + ","+ COLUMN_ENVIRONMENT + ","+ COLUMN_CATEGORY +
                " FROM '" + TABLE_FINGERPRINT + "' JOIN '"+TABLE_INFO +"' ON "+TABLE_FINGERPRINT+"."+COLUMN_INFO_ID +
                " = " + TABLE_INFO + "." + INFO_COLUMN_ID + ";";

        // Cursor point to a location in your results
        Cursor c = db.rawQuery(query, null);
        // Move to the first row in your results
        c.moveToFirst();

        int x = -1;
        int y = -1;
        int floor = -1;

        int info_id = 0;
        String personname = "";
        String roomname = "";
        String environment = "";
        String category = "";

        while (!c.isAfterLast()) {
                x = c.getInt(c.getColumnIndex(COLUMN_X));
                y = c.getInt(c.getColumnIndex(COLUMN_Y));
                floor = c.getInt(c.getColumnIndex(COLUMN_FLOOR));

                info_id = c.getInt(c.getColumnIndex(INFO_COLUMN_ID));
                personname = c.getString(c.getColumnIndex(COLUMN_PERSON_NAME));
                roomname = c.getString(c.getColumnIndex(COLUMN_ROOM_NAME));
                environment = c.getString(c.getColumnIndex(COLUMN_ENVIRONMENT)) == null ? "" : c.getString(c.getColumnIndex(COLUMN_ENVIRONMENT));
                category = c.getString(c.getColumnIndex(COLUMN_CATEGORY));

            res.put(new Coordinate(floor, x, y), new InfoModel(info_id, personname, roomname, environment, category));
            c.moveToNext();
        }

        Log.d(TAG, "DONE FETCHING COORDINATE TO INFOMODEL HASHMAP " + res.size());
        c.close();
        db.close();
        return res;
    }

    public LinkedList<Coordinate> getAllAnchors(){
        LinkedList<Coordinate> res = new LinkedList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COLUMN_X + ", " + COLUMN_Y + ", " + COLUMN_FLOOR + " FROM '" + TABLE_FINGERPRINT + "';";

        // Cursor point to a location in your results
        Cursor c = db.rawQuery(query, null);
        // Move to the first row in your results
        c.moveToFirst();

        int x = -1;
        int y = -1;
        int floor = -1;

        while (!c.isAfterLast()) {
            x = c.getInt(c.getColumnIndex(COLUMN_X));
            y = c.getInt(c.getColumnIndex(COLUMN_Y));
            floor = c.getInt(c.getColumnIndex(COLUMN_FLOOR));
            res.add(new Coordinate(floor, x, y));
            c.moveToNext();
        }
        Log.d(TAG, "DONE FETCHING ALL FINGERPRINTS " + res.size());
        c.close();
        db.close();
        return res;
    }

    public ArrayList<String> getSearchSpecificPersonEntries(String key) {
        ArrayList<String> res = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();

            String[] keys = key.split("\\s+");
            StringBuilder sb = new StringBuilder();

        for (int i = 0; i < keys.length; i++)
            if(i==0)
                sb.append(" "+COLUMN_PERSON_NAME+" LIKE '%"+keys[i]+"%' ");
            else
                sb.append(" OR "+COLUMN_PERSON_NAME+" LIKE '%" + keys[i] + "%'");

        String query = "SELECT DISTINCT "+COLUMN_PERSON_NAME+" FROM '" + TABLE_INFO + "' WHERE " + "("+sb.toString() + ");";


        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();


        String person_name = "";

        while (!c.isAfterLast()) {
            person_name = c.getString(c.getColumnIndex(COLUMN_PERSON_NAME));
            res.add(person_name);
            c.moveToNext();
        }

        Log.d(TAG, "DONE GETTING SEARCH SPECIFIC PERSON -ENTRIES " + res.size());
        c.close();
        db.close();

        return res;
    }

    public ArrayList<String> getSearchSpecificRoomEntries(String key) {
        ArrayList<String> res = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();

        String[] keys = key.split("\\s+");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < keys.length; i++)
            if(i==0)
                sb.append(" "+COLUMN_ROOM_NAME+" LIKE '%"+keys[i]+"%' ");
            else
                sb.append(" OR "+COLUMN_ROOM_NAME+" LIKE '%" + keys[i] + "%'");

        String query = "SELECT DISTINCT "+COLUMN_ROOM_NAME+" FROM '" + TABLE_INFO + "' WHERE " + "("+sb.toString() + ");";


        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();


        String room_name = "";

        while (!c.isAfterLast()) {
            room_name = c.getString(c.getColumnIndex(COLUMN_ROOM_NAME));
            res.add(room_name);
            c.moveToNext();
        }

        Log.d(TAG, "DONE GETTING SEARCH SPECIFIC ROOM -ENTRIES " + res.size());
        c.close();
        db.close();

        return res;
    }

    public ArrayList<String> getAllDistinctCategories() {
        ArrayList<String> res = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT DISTINCT CATEGORY FROM '" + TABLE_INFO + "';";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        String category = "";

        while (!c.isAfterLast()) {
            if(!c.isNull(c.getColumnIndex(COLUMN_CATEGORY))) {
                category = c.getString(c.getColumnIndex(COLUMN_CATEGORY));
                res.add(category);
            }
//            Log.d("DBHandler", category);
            c.moveToNext();
        }
        c.close();
        db.close();

        return res;
    }

    public ArrayList<InfoModel> getAllEntriesForSpecificCategory(String key) {
        ArrayList<InfoModel> res = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM '" + TABLE_INFO + "' WHERE CATEGORY ='" + key + "';";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        int id = 0;
        String person_name = "";
        String room_name = "";
        String environment = "";
        String category = "";

        while (!c.isAfterLast()) {
            id = c.getInt(c.getColumnIndex(INFO_COLUMN_ID));
            person_name = c.getString(c.getColumnIndex(COLUMN_PERSON_NAME));
            room_name = c.getString(c.getColumnIndex(COLUMN_ROOM_NAME));
            environment = c.getString(c.getColumnIndex(COLUMN_ENVIRONMENT));
            category = c.getString(c.getColumnIndex(COLUMN_CATEGORY));
            res.add(new InfoModel(id, person_name, room_name, environment, category));
            c.moveToNext();
        }

        Log.d(TAG, "DONE GETTING ALL ENTRIES FOR SPECIFIC CATEGORY" + res.size());
        c.close();
        db.close();

        return res;
    }

    public ArrayList<Coordinate> getDirectNeighborFPs(Coordinate centerPos) {
        ArrayList<Coordinate> neighbors = new ArrayList<>();
//        neighbors.add(centerPos);
        double x = centerPos.getX();
        double y = centerPos.getY();
        double floor = -1;

        //Untere Seite
        y -= 1;
        x -= 1;
        Coordinate temp = new Coordinate(floor, x, y);
        neighbors.add(temp);
        x += 1;
        temp = new Coordinate(floor, x, y);
        neighbors.add(temp);
        x += 1;
        temp = new Coordinate(floor, x, y);
        neighbors.add(temp);

        //Obere Seite
        y += 2;
        x -= 2;
        temp = new Coordinate(floor, x, y);
        neighbors.add(temp);
        x += 1;
        temp = new Coordinate(floor, x, y);
        neighbors.add(temp);
        x += 1;
        temp = new Coordinate(floor, x, y);
        neighbors.add(temp);

        //Rechter und Linker Punkt in der Mitte
        y -= 1;
        temp = new Coordinate(floor, x, y);
        neighbors.add(temp);
        x -= 2;
        temp = new Coordinate(floor, x, y);
        neighbors.add(temp);

        SQLiteDatabase db = getWritableDatabase();
        ArrayList<Coordinate> result = new ArrayList<>();
        String subquery = "";

        for (int i = 0; i < neighbors.size(); i++) {
            String subQuery = COLUMN_X + "=" + neighbors.get(i).getX() + " AND " + COLUMN_Y + "=" + neighbors.get(i).getY() + ";";
            String query = "SELECT * FROM '" + TABLE_FINGERPRINT + "' WHERE " + subQuery + ";";
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();

            int f_coord = -1;
            int x_coord = 0;
            int y_coord = 0;

            while (!c.isAfterLast()) {
//                f_coord = c.getInt(c.getColumnIndex(COLUMN_FLOOR));
                x_coord = c.getInt(c.getColumnIndex(COLUMN_X));
                y_coord = c.getInt(c.getColumnIndex(COLUMN_Y));
                Coordinate tmp = new Coordinate(f_coord, x_coord, y_coord);
                result.add(tmp);
                c.moveToNext();
            }
            c.close();
        }
        Log.d(TAG, "GOT ADJESCENT COORDS: " + result.size());
        db.close();

        Log.d(TAG, "DIRECT NEIGHBOR:\n" + Util.primitiveListToString(result));

        return result;
    }

    public ArrayList<Coordinate> getOuterNeighborFPs(Coordinate centerPos) {
        ArrayList<Coordinate> neighbors = new ArrayList<>();
//        neighbors.add(centerPos);
        double x = centerPos.getX();
        double y = centerPos.getY();
        double floor = -1;

        //Untere Seite
        y -= 2;
        x -= 2;
        Coordinate temp = new Coordinate(floor, x, y);
        neighbors.add(temp);
        for (int i = 0; i < 4; i++) {
            x += 1;
            temp = new Coordinate(floor, x, y);
            neighbors.add(temp);
        }

        //Obere Seite
        y += 4;
        x -= 4;
        for (int i = 0; i < 4; i++) {
            x += 1;
            temp = new Coordinate(floor, x, y);
            neighbors.add(temp);
        }

        //Rechte Seite
        for (int i = 0; i < 3; i++) {
            y -= 1;
            temp = new Coordinate(floor, x, y);
            neighbors.add(temp);
        }

        //Linke Seite
        x -= 4;
        y += 2;
        for (int i = 0; i < 2; i++) {
            y -= 1;
            temp = new Coordinate(floor, x, y);
            neighbors.add(temp);
        }

        SQLiteDatabase db = getWritableDatabase();
        ArrayList<Coordinate> result = new ArrayList<>();
        String subquery = "";

        for (int i = 0; i < neighbors.size(); i++) {
            String subQuery = COLUMN_X + "=" + neighbors.get(i).getX() + " AND " + COLUMN_Y + "=" + neighbors.get(i).getY() + ";";
            String query = "SELECT * FROM '" + TABLE_FINGERPRINT + "' WHERE " + subQuery + ";";
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();

            int f_coord = -1;
            int x_coord = 0;
            int y_coord = 0;

            while (!c.isAfterLast()) {
//                f_coord = c.getInt(c.getColumnIndex(COLUMN_FLOOR));
                x_coord = c.getInt(c.getColumnIndex(COLUMN_X));
                y_coord = c.getInt(c.getColumnIndex(COLUMN_Y));
                Coordinate tmp = new Coordinate(f_coord, x_coord, y_coord);
                result.add(tmp);
                c.moveToNext();
            }
            c.close();
        }

//        Log.d(TAG, "GOT OUTER ADJESCENT COORDS: " + result.size());
        Log.d(TAG, "OUTER NEIGHBOR:\n" + Util.primitiveListToString(result));
        db.close();


        return result;
    }

    public String getDBPath() {
        return context.getDatabasePath(Database.DATABASE_NAME).toString();
    }

    public boolean deleteDBFile() {
        return new File(context.getDatabasePath(Database.DATABASE_NAME).toString()).delete();
    }
}
