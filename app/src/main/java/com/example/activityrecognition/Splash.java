package com.example.activityrecognition;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * the display of main window
 * do not edit more
 */
public class Splash extends AppCompatActivity {

    TextView currentData;
    TextView currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//hide state
        getSupportActionBar().hide();//hide title
        setContentView(R.layout.activity_splash);
        currentData = (TextView)findViewById(R.id.dataText);
        currentTime = (TextView)findViewById(R.id.timeText);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd" );// HH:mm:ss
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        currentData.setText("Current Data: "+simpleDateFormat.format(date));
        currentTime.setText("Current time: "+ simpleTimeFormat.format(date));
        Thread myThread=new Thread(){
            @Override
            public void run() {
                try{
                    sleep(3000);
                    Intent it = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(it);
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        myThread.start();

    }
}
