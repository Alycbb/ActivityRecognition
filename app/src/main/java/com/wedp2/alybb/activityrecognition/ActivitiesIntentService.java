package com.wedp2.alybb.activityrecognition;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

public class ActivitiesIntentService extends IntentService {
    private static final String TAG = "ActivitiesIntentService";

    public ActivitiesIntentService(){
        super(TAG);
    }

    protected void onHandleIntent(Intent intent){
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        Intent i = new Intent(Constants.string_action);
        Intent aa = new Intent(this,AA.class);
        DetectedActivity detectedActivity = result.getMostProbableActivity();
        i.putExtra(Constants.string_extra,detectedActivity);
        i.putExtra("type",detectedActivity.getType());
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

}
