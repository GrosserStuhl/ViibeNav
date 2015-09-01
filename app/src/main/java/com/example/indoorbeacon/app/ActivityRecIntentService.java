package com.example.indoorbeacon.app;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by Dima on 01/09/2015.
 */
public class ActivityRecIntentService extends IntentService {

    public ActivityRecIntentService() {
        super("ActivityRecIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity mostProbAct = result.getMostProbableActivity();
            final int confidence = mostProbAct.getConfidence();
            final String mostProbActName = getNameFromType(mostProbAct.getType());
            Log.d("ActRecIntentService", "Detected Act: " + mostProbActName + " with conf: " + confidence);
            Handler mHandler = new Handler(getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Detected Act: " + mostProbActName + " with conf: " + confidence, Toast.LENGTH_SHORT).show();
                }
            });
        } else Log.e("ActRecIntentService", "Intent had no ActivityRecognitionData");

    }

    private String getNameFromType(int activityType) {
        switch (activityType) {
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.RUNNING:
                return "running";
            case DetectedActivity.WALKING:
                return "walking";
            case DetectedActivity.ON_FOOT:
                return "on_foot";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.TILTING:
                return "tilting";
            case DetectedActivity.UNKNOWN:
                return "unknown";
        }
        return "unknown";
    }
}