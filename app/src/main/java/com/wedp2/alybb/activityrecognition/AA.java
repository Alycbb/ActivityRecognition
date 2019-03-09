package com.wedp2.alybb.activityrecognition;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.DetectedActivity;

public class AA extends AppCompatActivity {

    private TextView aa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);
        Log.v("aa","see me!!!!");
        aa = (TextView)findViewById(R.id.textView);
        Intent i = getIntent();
        judge(i);
    }

    public void judge(Intent intent)
    {
        int a = intent.getIntExtra("type",-1);
        if(a != DetectedActivity.WALKING && a != DetectedActivity.ON_FOOT)
        {
            Intent back = new Intent(Constants.string_action);
            LocalBroadcastManager.getInstance(this).sendBroadcast(back);

        }
    }
}
