package com.wedp2.alybb.activityrecognition;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Greeting extends AppCompatActivity {
    private int re = 3;
    private TextView t;
    Timer timer = new Timer();
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeting);
        t = findViewById(R.id.time);
        SimpleDateFormat sd  = new SimpleDateFormat("yyyy-MM-dd HH:mm E");
        Date d = new Date(System.currentTimeMillis());
        t.setText("Current Time :"+ sd.format(d));

        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setFlags(flag,flag);
//        timer.schedule(task,1000,1000);
        handler = new Handler();
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Greeting.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);
    }



    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    re--;
                    if(re < 0){
                        timer.cancel();
                        t.setVisibility(View.GONE);
                    }
                }
            });
        }
    };

    public void onClick(View view){
        switch(view.getId()){
            case R.id.time:
                Intent intent = new Intent(Greeting.this,MainActivity.class);
                startActivity(intent);
                finish();
                if(runnable != null){
                    handler.removeCallbacks(runnable);
                }
                break;
            default:
                break;
        }
    }
}
