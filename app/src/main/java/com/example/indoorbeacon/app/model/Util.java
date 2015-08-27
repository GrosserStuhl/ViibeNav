package com.example.indoorbeacon.app.model;

import android.widget.EditText;

import com.example.indoorbeacon.app.model.position.neighbor.MacToMedian;

import java.io.Serializable;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;


/**
 * Created by TomTheBomb on 26.06.2015.
 */
public class Util implements Serializable {

    private static final String TAG = "Util";
    public static DecimalFormat df;

    static{
        df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
    }

    public static double twoDecimals(double data){
        return Double.parseDouble(df.format(data).replaceAll(",","."));
    }

    /**
     * Converts the Value of a textView to an Integer
     * @param t
     * @return
     */
    public static int textViewToInt(EditText t){
//        Log.d("Measurement"," "+Integer.parseInt(t.getText().toString()));
//        if(isInteger(t.toString()))
//            return Integer.parseInt(t.toString());
//        else
//            return -99;
            return Integer.valueOf(t.toString());
    }

//    /**
//     * Checks if the passed String is convertable to an Integer
//     * @param string
//     * @return
//     */
//    public static boolean isInteger(String string) {
//        try {
//            Integer.parseInt(string);
//            return true;
//        } catch (NumberFormatException e) {
//            return false;
//        }
//        return true;
//    }

    public static String stringListToString(ArrayList<Integer> list){
        String res = "";
        int[] temp = new int[list.size()];

        for (int j = 0; j < list.size() ; j++)
            temp[j] = list.get(j);
        Arrays.sort(temp);
        int count = 0;
        for(int i : temp) {
            count++;
            if (count != list.size())
                res += String.valueOf(i) + ",";
            else
                res += String.valueOf(i);
        }

        return res;
    }

    public static CharBuffer strToCharBuff(String str){
            char[] data = str.toCharArray();
            ByteBuffer bb = ByteBuffer.allocate(data.length * 2);
            CharBuffer cb = bb.asCharBuffer();
            cb.put(data);
            cb.rewind();
            return cb;
    }

    public static String getDateTimeToStr(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
        return sdf.format(c.getTime());
    }

    public static String intListToString(ArrayList<Integer> data){
        String res = "( ";

        for(int i: data){
            res += i + ",";
        }

        res += ")";

        return res;
    }

    public static long timeDiff_MillisToNow(long millisAgo){
        return System.currentTimeMillis()-millisAgo;
    }


    public static MacToMedian[] mapToMacToMedianArr(HashMap<CharBuffer,OnyxBeacon> input){
        MacToMedian[] res = new MacToMedian[input.size()];
        final ArrayList<OnyxBeacon> convert = new ArrayList<>(input.values());

        for(int i=0;i<input.values().size();i++)
            res[i] = new MacToMedian(convert.get(i).getMacAddress(),convert.get(i).getMedianRSSI());

        return res;
    }

    public static MacToMedian[] listToMacToMedianArr(final ArrayList<OnyxBeacon> input){
        MacToMedian[] res = new MacToMedian[input.size()];
        for(int i=0;i<input.size();i++)
            res[i] = new MacToMedian(input.get(i).getMacAddress(), input.get(i).getOnTheFlyMedian());

        return res;
    }


    /**
     * Adds additional probability to the signals
     * @param median
     * @return
     */
    public static int probFromMedianQuality(double median){
        if(median<= Setup.STRONG_SIGNAL_QUALITY)
            return 3;
        else if (median <= Setup.MEDIUM_SIGNAL_QUALITY)
            return 2;
        else if (median <= Setup.LOW_SIGNAL_QUALITY)
            return 0;

        return 0;
    }

    public static boolean hasSufficientSendingFreq(long time){
        return Util.timeDiff_MillisToNow(time) <= Setup.TIME_LAST_SIGNAL_THRESHOLD;
    }



}
