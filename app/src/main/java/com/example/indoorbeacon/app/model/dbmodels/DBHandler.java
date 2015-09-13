package com.example.indoorbeacon.app.model.dbmodels;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.indoorbeacon.app.model.Coordinate;
import com.example.indoorbeacon.app.model.Definitions;
import com.example.indoorbeacon.app.model.Orientation;
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
public class DBHandler extends SQLiteOpenHelper {

    private static final String TAG = "DBHandler";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "radiomap.db";

    // ANCHORS TABLE
    public static final String TABLE_ANCHORS = "anchorpoints";
    public static final String ANCHORS_COLUMN_ID = "_id";
    // POSITION OF ANCHOR
    public static final String COLUMN_X = "x";
    public static final String COLUMN_Y = "y";
    public static final String COLUMN_FLOOR = "floor";
    public static final String COLUMN_FRONT = "front";
    public static final String COLUMN_BACK = "back";
    public static final String COLUMN_INFO_ID = "infoid";

    // MAXIMUM AMOUNT OF BEACONS IN ONE TABLE
    public static final String TABLE_BEACON_MEDIAN_TO_ANCHOR = "beaconmediantoanchor";
    public static final String BEACON_MEDIAN_TO_ANCHOR_ID = "id";
    public static final String COLUMN_BEACON_1 = "beacon1";
    public static final String COLUMN_BEACON_2 = "beacon2";
    public static final String COLUMN_BEACON_3 = "beacon3";
    public static final String COLUMN_BEACON_4 = "beacon4";
    public static final String COLUMN_BEACON_5 = "beacon5";
    public static final String COLUMN_BEACON_6 = "beacon6";

    // MEDIANS TABLE
    public static final String TABLE_MEDIANS = "medians";
    public static final String MEDIANS_COLUMN_ID = "_id";
    public static final String COLUMN_MEDIAN_VALUE = "median";
    public static final String COLUMN_MACADDRESS = "macaddress";

    // BEACONS TABLE
    public static final String TABLE_BEACONS = "beacons";
    public static final String BEACONS_COLUMN_ID = "id";
    public static final String COLUMN_MAJOR = "major";
    public static final String COLUMN_MINOR = "minor";

    // INFO TABLE
    public static final String TABLE_INFO = "info";
    public static final String INFO_COLUMN_ID = "id";
    public static final String COLUMN_PERSON_NAME = "personname";
    public static final String COLUMN_ROOM_NAME = "roomname";
    public static final String COLUMN_ENVIRONMENT = "environment";
    public static final String COLUMN_CATEGORY = "category";

    private Context c;
    private static DBHandler singleton;


    private DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        this.c = context;
    }

    public static DBHandler createDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        // Avoid possible errors with multiple threads accessing this method -> synchronized
        synchronized (DBHandler.class) {
            if (singleton == null) {
                singleton = new DBHandler(context, name, factory, version);
            }
        }
        return singleton;
    }

    public static DBHandler getDB() {
        return singleton;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {


        // CREATE BEACONS TABLE
        String query1 = "CREATE TABLE " + TABLE_BEACONS + "(" +
                "'" + BEACONS_COLUMN_ID + "'" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "'" + COLUMN_MACADDRESS + "'" + " TEXT UNIQUE, " +
                "'" + COLUMN_MAJOR + "'" + " INTEGER, " +
                "'" + COLUMN_MINOR + "'" + " INTEGER " +
                ");";
        db.execSQL(query1);

        // CREATE MEDIANS TABLE - laut: http://www.w3schools.com/sql/sql_foreignkey.asp
        String query2 = "CREATE TABLE " + TABLE_MEDIANS + "(" +
                "'" + MEDIANS_COLUMN_ID + "'" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "'" + COLUMN_MEDIAN_VALUE + "'" + " INTEGER, " +
                "'" + COLUMN_MACADDRESS + "'" + " TEXT "/*FOREIGN KEY REFERENCES "+TABLE_BEACONS+"("+BEACONS_COLUMN_ID+")"*/ +
                ");";
        db.execSQL(query2);

        // CREATE ANCHORS TABLE
        String query3 = "CREATE TABLE " + TABLE_ANCHORS + "(" +
                "'" + ANCHORS_COLUMN_ID + "'" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "'" + COLUMN_X + "'" + " INTEGER, " +
                "'" + COLUMN_Y + "'" + " INTEGER, " +
                "'" + COLUMN_FLOOR + "'" + " INTEGER, " +
                // 90 degrees
                "'" + COLUMN_FRONT + "'" + " INTEGER," +
                // 270 degrees
                "'" + COLUMN_BACK + "'" + " INTEGER," +
                "'" + COLUMN_INFO_ID + "'" + " INTEGER  " +
                ");";
        db.execSQL(query3);

        // CREATE INFO TABLE
        String query4 = "CREATE TABLE " + TABLE_INFO + "(" +
                "'" + INFO_COLUMN_ID + "'" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "'" + COLUMN_PERSON_NAME + "'" + " TEXT, " +
                "'" + COLUMN_ROOM_NAME + "'" + " TEXT, " +
                "'" + COLUMN_ENVIRONMENT + "'" + " TEXT, " +
                "'" + COLUMN_CATEGORY + "'" + " TEXT " +
                ");";
        db.execSQL(query4);

        // CREATE INFO TABLE
        String query5 = "CREATE TABLE " + TABLE_BEACON_MEDIAN_TO_ANCHOR + "(" +
                "'" + BEACON_MEDIAN_TO_ANCHOR_ID + "'" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "'" + COLUMN_BEACON_1 + "'" + " INTEGER, " +
                "'" + COLUMN_BEACON_2 + "'" + " INTEGER, " +
                "'" + COLUMN_BEACON_3 + "'" + " INTEGER, " +
                "'" + COLUMN_BEACON_4 + "'" + " INTEGER, " +
                "'" + COLUMN_BEACON_5 + "'" + " INTEGER, " +
                "'" + COLUMN_BEACON_6 + "'" + " INTEGER " +
                ");";
        db.execSQL(query5);


        // TEST ZWECKE
        ContentValues valuesTest = new ContentValues();
        valuesTest.put(COLUMN_PERSON_NAME, "Robert Tscharn");
        valuesTest.put(COLUMN_ROOM_NAME, "Diana Löffler ihr Raum");
        valuesTest.put(COLUMN_CATEGORY, "WCs");

        db.insertOrThrow(TABLE_INFO, null, valuesTest);

        ContentValues valuesTest2 = new ContentValues();
        valuesTest2.put(COLUMN_PERSON_NAME, "Diana Löffler");
        valuesTest2.put(COLUMN_ROOM_NAME, "Robert Tscharn sein Raum");
        valuesTest2.put(COLUMN_CATEGORY, "Büro");

        db.insertOrThrow(TABLE_INFO, null, valuesTest2);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_ANCHORS);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_MEDIANS);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_BEACONS);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_INFO);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_BEACON_MEDIAN_TO_ANCHOR);
        onCreate(db);
    }

    public Coordinate getTarget(String targetString){
        SQLiteDatabase db = getWritableDatabase();
        final String query = "SELECT " + COLUMN_X + "," + COLUMN_Y + " FROM '" + TABLE_ANCHORS + "'"+
                " JOIN '" + TABLE_INFO + "' ON " + TABLE_ANCHORS + "."+COLUMN_INFO_ID +"="+ TABLE_INFO+"."+INFO_COLUMN_ID+" WHERE ("+
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

        ArrayList<DeviationToCoord> devsToCoords = new ArrayList<>();
        final String LOCAL_COLUMN_DEVIATION = "deviation";

        for (int i = 0; i < map.length; i++) {
            final String macAddress = map[i].getMacAddressStr();
            final double median = map[i].getMedian();

            String queryOrientation = "";
            if (map[i].getOrientation().equals(Orientation.back))
                queryOrientation = "( " + TABLE_ANCHORS + "." + COLUMN_BACK + " = " + TABLE_BEACON_MEDIAN_TO_ANCHOR + "." + BEACON_MEDIAN_TO_ANCHOR_ID + " ) ";
            else if (map[i].getOrientation().equals(Orientation.front))
                queryOrientation = "( " + TABLE_ANCHORS + "." + COLUMN_FRONT + " = " + TABLE_BEACON_MEDIAN_TO_ANCHOR + "." + BEACON_MEDIAN_TO_ANCHOR_ID + " ) ";

            String query = "SELECT " + TABLE_ANCHORS + "." + COLUMN_FLOOR + "," + TABLE_ANCHORS + "." + COLUMN_X + ", " + TABLE_ANCHORS + "." + COLUMN_Y + "," + TABLE_MEDIANS + "." + MEDIANS_COLUMN_ID + ", " + calcManhattenDB_Cmd(median) + " AS " + LOCAL_COLUMN_DEVIATION + " " +
                    " FROM '" + TABLE_MEDIANS + "' JOIN '" + TABLE_ANCHORS + "' JOIN '" + TABLE_BEACON_MEDIAN_TO_ANCHOR + "' WHERE macAddress = '" + macAddress + "' AND " +
                    " ( " + TABLE_BEACON_MEDIAN_TO_ANCHOR + "." + COLUMN_BEACON_1 + " = " + TABLE_MEDIANS + "." + MEDIANS_COLUMN_ID + " " +
                    "   OR " + TABLE_BEACON_MEDIAN_TO_ANCHOR + "." + COLUMN_BEACON_2 + " = " + TABLE_MEDIANS + "." + MEDIANS_COLUMN_ID + "  " +
                    "   OR " + TABLE_BEACON_MEDIAN_TO_ANCHOR + "." + COLUMN_BEACON_3 + " = " + TABLE_MEDIANS + "." + MEDIANS_COLUMN_ID + "  " +
                    "   OR " + TABLE_BEACON_MEDIAN_TO_ANCHOR + "." + COLUMN_BEACON_4 + " = " + TABLE_MEDIANS + "." + MEDIANS_COLUMN_ID + "  " +
                    "   OR " + TABLE_BEACON_MEDIAN_TO_ANCHOR + "." + COLUMN_BEACON_5 + " = " + TABLE_MEDIANS + "." + MEDIANS_COLUMN_ID + "  " +
                    "   OR " + TABLE_BEACON_MEDIAN_TO_ANCHOR + "." + COLUMN_BEACON_6 + " = " + TABLE_MEDIANS + "." + MEDIANS_COLUMN_ID + "  " +
                    "  ) AND " + queryOrientation +
                    " GROUP BY " + COLUMN_MEDIAN_VALUE + " HAVING deviation <=" + Definitions.POSITIONING_THRESHOLD + " ORDER BY " + LOCAL_COLUMN_DEVIATION + " ASC LIMIT " + Definitions.POSITIONING_LIMIT + ";";

            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                Coordinate coordinate = new Coordinate(c.getInt(c.getColumnIndex(COLUMN_FLOOR)), c.getInt(c.getColumnIndex(COLUMN_X)), c.getInt(c.getColumnIndex(COLUMN_Y)));

                float deviation = c.getInt(c.getColumnIndex(LOCAL_COLUMN_DEVIATION));
                devsToCoords.add(new DeviationToCoord(deviation, coordinate));
//                Log.d(TAG, "Deviation-Median" + c.getInt(c.getColumnIndex(MEDIANS_COLUMN_ID)) +
//                        " deviation: " + deviation +
//                        " -> Coord: " + coordinate + " macAddress " + macAddress);
                c.moveToNext();
            }
            c.close();
        }
        db.close();
//        Log.d(TAG,"DEV TO COORDS SIZE: "+devsToCoords.size()+ " FROM "+map.length+ " Macs");
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

    //Delete an anchor from the database
    public void deleteAnchor(String anchorName) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_ANCHORS + " WHERE " + anchorName + "=\"" + anchorName + "\";");
    }

    public void deleteAllTables() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_ANCHORS + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_MEDIANS + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_BEACONS + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_INFO + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_BEACON_MEDIAN_TO_ANCHOR + "'");
        onCreate(db);
    }


    /*
    Uses euclidean distance
     */
    private String calcEuclideanDB_Cmd(double median) {
        return "SQRT(ABS(MAX(POWER(" + COLUMN_MEDIAN_VALUE + " - " + median + "))))";
    }

    /*
    Uses Manhatten distrance for DB
     */
    private String calcManhattenDB_Cmd(double median) {
        return "ABS(MIN(" + COLUMN_MEDIAN_VALUE + " - " + median + "))";
    }


    public Coordinate getCoordFromAnchorId(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COLUMN_X + ", " + COLUMN_Y + ", " + COLUMN_FLOOR + " FROM '" + TABLE_ANCHORS + "' WHERE " + ANCHORS_COLUMN_ID + " = '" + id + "';";

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
        String query = "SELECT "+ ANCHORS_COLUMN_ID +","+ COLUMN_X +","+ COLUMN_Y +","+ COLUMN_FLOOR + ","+
                INFO_COLUMN_ID + ","+ COLUMN_PERSON_NAME + ","+ COLUMN_ROOM_NAME + ","+ COLUMN_ENVIRONMENT + ","+ COLUMN_CATEGORY +
                " FROM '" + TABLE_ANCHORS + "' JOIN '"+TABLE_INFO +"' ON "+TABLE_ANCHORS+"."+COLUMN_INFO_ID +
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
                environment = c.getString(c.getColumnIndex(COLUMN_ENVIRONMENT));
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
        String query = "SELECT " + COLUMN_X + ", " + COLUMN_Y + ", " + COLUMN_FLOOR + " FROM '" + TABLE_ANCHORS + "';";

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
        Log.d(TAG, "DONE FETCHING ALL ANCHORPOINTS "+res.size());
        c.close();
        db.close();
        return res;
    }

    public ArrayList<String> getSearchSpecificPersonEntries(String key) {
        ArrayList<String> res = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();

//            String[] keys = key.split("\\s+");
//            StringBuilder sb = new StringBuilder();
//            for(String k : keys)
//                sb.append(" OR LIKE '% " + k + " %'");

        String query = "SELECT *  FROM '" + TABLE_INFO + "' WHERE " +
                "(" +
                COLUMN_PERSON_NAME + " LIKE " + "'%" + key + "%'" /*+ sb.toString()*/ +
                ");";


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
        String query = "SELECT *  FROM '" + TABLE_INFO + "' WHERE " +
                "(" +
                COLUMN_ROOM_NAME + " LIKE " + "'%" + key + "%'" +
                ");";

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

    public ArrayList<Coordinate> getDirectNeighborAnchors(Coordinate centerPos) {
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
            String query = "SELECT * FROM '" + TABLE_ANCHORS + "' WHERE " + subQuery + ";";
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

    public ArrayList<Coordinate> getOuterNeighborAnchors(Coordinate centerPos) {
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
            String query = "SELECT * FROM '" + TABLE_ANCHORS + "' WHERE " + subQuery + ";";
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
        return c.getDatabasePath(DBHandler.DATABASE_NAME).toString();
    }

    public boolean deleteDBFile() {
        return new File(c.getDatabasePath(DBHandler.DATABASE_NAME).toString()).delete();
    }
}
