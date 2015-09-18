package com.example.indoorbeacon.app.model;

import android.util.Log;
import android.widget.EditText;

import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by TomTheBomb on 26.06.2015.
 */
public class Util {

    private static final String TAG = "Util";
    public static DecimalFormat df;

    static {
        df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
    }

    public static double twoDecimals(double data) {
        return Double.parseDouble(df.format(data).replaceAll(",", "."));
    }

    /**
     * Converts the Value of a textView to an Integer
     *
     * @param t
     * @return
     */
    public static int textViewToInt(EditText t) {
        return Integer.valueOf(t.toString());
    }


    public static String stringListToString(ArrayList<Integer> list) {
        String res = "";
        int[] temp = new int[list.size()];

        for (int j = 0; j < list.size(); j++)
            temp[j] = list.get(j);
        Arrays.sort(temp);
        int count = 0;
        for (int i : temp) {
            count++;
            if (count != list.size())
                res += String.valueOf(i) + ",";
            else
                res += String.valueOf(i);
        }

        return res;
    }

    public static CharBuffer strToCharBuff(String str) {
        char[] data = str.toCharArray();
        ByteBuffer bb = ByteBuffer.allocate(data.length * 2);
        CharBuffer cb = bb.asCharBuffer();
        cb.put(data);
        cb.rewind();
        return cb;
    }

    public static String getDateTimeToStr() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
        return sdf.format(c.getTime());
    }

    public static String primitiveListToString(ArrayList<? extends Object> data) {
        String res = "{ ";
        for (int i = 0; i < data.size(); i++)
            if (i < data.size() - 1)
                res += data.get(i) + " | ";
            else
                res += data.get(i);
        res += " }";
        return res;
    }

    public static long timeDiff_MillisToNow(long millisAgo) {
        return System.currentTimeMillis() - millisAgo;
    }

    public static boolean hasSufficientSendingFreq(long time) {
        return Util.timeDiff_MillisToNow(time) <= Definitions.TIME_LAST_SIGNAL_THRESHOLD;
    }

    public static ArrayList<Integer> convertFloatListToIntegerList(ArrayList<Float> floatArray) {
        ArrayList<Integer> intArray = new ArrayList<>();
        for (float temp : floatArray)
            intArray.add((int) temp);
        return intArray;
    }

    public static int convertDegreesToTime(int degrees) {
        int result = 0;
        int absDegrees = Math.abs(degrees);
        Log.d(TAG, "absDegrees: " + absDegrees);
        if (absDegrees > 345 || absDegrees <= 15)
            result = 12;
        else if (absDegrees > 15 && absDegrees <= 40)
            result = 13;
        else if (absDegrees > 40 && absDegrees <= 70)
            result = 14;
        else if (absDegrees > 70 && absDegrees <= 95)
            result = 15;
        else if (absDegrees > 95 && absDegrees <= 120)
            result = 16;
        else if (absDegrees > 120 && absDegrees <= 160)
            result = 17;
        else if (absDegrees > 160 && absDegrees <= 185)
            result = 18;
        else if (absDegrees > 185 && absDegrees <= 210)
            result = 19;
        else if (absDegrees > 210 && absDegrees <= 240)
            result = 20;
        else if (absDegrees > 240 && absDegrees <= 275)
            result = 21;
        else if (absDegrees > 275 && absDegrees <= 310)
            result = 22;
        else if (absDegrees > 310 && absDegrees <= 345)
            result = 23;

        return result;
    }

}
