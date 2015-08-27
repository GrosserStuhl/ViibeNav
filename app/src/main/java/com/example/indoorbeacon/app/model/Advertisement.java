package com.example.indoorbeacon.app.model;

import android.util.Log;
import com.example.indoorbeacon.app.model.beaconFilter.MinorFilter;
import java.nio.CharBuffer;
/**
 * Created by TomTheBomb on 23.06.2015.
 */
public class Advertisement {

    public static final String filterUUID = "20CAE8A0-A9CF-11E3-A5E2-0800200C9A66" ;
    private static final String TAG = "Advertisement";

    public static boolean validateUUID(byte[] scanRecord){
        int startByte = 2;
        boolean patternFound = false;
        while (startByte <= 5) {
            if (    ((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                    ((int) scanRecord[startByte + 3] & 0xff) == 0x15) { //Identifies correct data length
                patternFound = true;
                break;
            }
            startByte++;
        }

        if (patternFound) {
            //Convert to hex String
            byte[] uuidBytes = new byte[16];
            System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
            String hexString = bytesToHex(uuidBytes);

            //Here is your UUID
            String uuid = hexString.substring(0, 8) + "-" +
                    hexString.substring(8, 12) + "-" +
                    hexString.substring(12, 16) + "-" +
                    hexString.substring(16, 20) + "-" +
                    hexString.substring(20, 32);

            Log.d(TAG, "UUID: " + uuid);

            if (uuid.equals(filterUUID))
                return true;
        }

        return false;
    }

    public static OnyxBeacon extractAD(final String deviceAddress, final int rssi, final byte[] scanRecord){
        int startByte = 2;
        boolean patternFound = false;
        while (startByte <= 5) {
            if (    ((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                    ((int) scanRecord[startByte + 3] & 0xff) == 0x15) { //Identifies correct data length
                patternFound = true;
                break;
            }
            startByte++;
        }

        if(!patternFound)
            return null;

        if (patternFound) {
            //Convert to hex String
            byte[] uuidBytes = new byte[16];
            System.arraycopy(scanRecord, startByte+4, uuidBytes, 0, 16);
            String hexString = bytesToHex(uuidBytes);

            //Here is your UUID
            String uuid =  hexString.substring(0,8) + "-" +
                    hexString.substring(8,12) + "-" +
                    hexString.substring(12,16) + "-" +
                    hexString.substring(16,20) + "-" +
                    hexString.substring(20,32);


            //Here is your Major value
            int major = (scanRecord[startByte+20] & 0xff) * 0x100 + (scanRecord[startByte+21] & 0xff);

            //Here is your Minor value
            int minor = (scanRecord[startByte+22] & 0xff) * 0x100 + (scanRecord[startByte+23] & 0xff);

            int txPower = scanRecord[29]  ;

            CharBuffer macAddress = Util.strToCharBuff(deviceAddress);


            // ADDITIONAL FILTER TO BE DELETED LATER ON!!!!
            if(MinorFilter.inFilter(minor)) {

                //creates new OnyxBeacon
                if (!OnyxBeacon.inBeaconMap(macAddress)) {
                    OnyxBeacon newBeacon = new OnyxBeacon(macAddress, uuid, major, minor, rssi, txPower);
                    newBeacon.setLastSignalMeasured(System.currentTimeMillis());
                } else {
                    OnyxBeacon.updateBeaconRSSIinMap(macAddress, rssi, System.currentTimeMillis());
                }


                //need to return this beacon which is listed in HashMap!
                return OnyxBeacon.getBeaconInMap(macAddress);

            }

        }
        return null;
    }

    /**
     * bytesToHex method
     * Found on the internet
     * http://stackoverflow.com/a/9855338
     */
    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
