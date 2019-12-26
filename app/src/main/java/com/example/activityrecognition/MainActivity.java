package com.example.activityrecognition;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public GoogleApiClient mApiClient;
    private String pre_action;
    private long pre_action_time;
    public TextView actionText;
    public ImageView actionImage;
    private MediaPlayer mePlayer;
    private AudioManager aManager;
    private static Boolean isAudioPlay;
    private DBManager dbManager = null;
    private Cursor c = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Message pass back
        IntentFilter filter = new IntentFilter();
        filter.addAction(ActivityRecognizedService.ACTION_SERVICE_STATE);
        registerReceiver(mReceiver, filter);

        //Audio setting
        aManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        mePlayer = MediaPlayer.create(MainActivity.this, R.raw.audio_1);
        mePlayer.setLooping(true);
        isAudioPlay = false;

        //Bind TextView and set some previous value
        actionText = (TextView)findViewById(R.id.actionText);
        actionImage= (ImageView)findViewById(R.id.actionImageView);
        pre_action = "None";
        pre_action_time = 0;

        //DB operation
        dbManager = new DBManager(this);
        dbManager.openDB();
        c = dbManager.getAll();
        dbManager.closeDB();

        //Activity Recognized Service start
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        c.close();
        super.onDestroy();
        mePlayer.stop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent( this, ActivityRecognizedService.class );
        PendingIntent pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates( mApiClient, 3000, pendingIntent );

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Get new action from Activity Recognized Service
            String tempAction;
            tempAction = intent.getStringExtra("ACTION");
            if(!(pre_action.equals(tempAction))){
                ShowTosatOnScreen(pre_action,tempAction);
                actionText.setText(tempAction);
                pre_action = tempAction;
                ChangeImage(pre_action);
            }
            ConditionAudioPlay(pre_action);
        }
    };

    private void ChangeImage(String act){
        switch (act){
            case "Running":
                actionImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.running));
                break;
            case "Walking":
                actionImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.walking));
                break;
            case "Still":
                actionImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.staying));
                break;
            case "In_Vehicle":
                actionImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.vehicle));
                break;
            default:
                actionImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.empty));
        }
    }

    private void ConditionAudioPlay(String act){

        if(act.equals("Walking") || act.equals("Running")){
            if(isAudioPlay == false){
                isAudioPlay = true;
                mePlayer.start();
            }
        }

        if(act.equals("Still") || act.equals("In_Vehicle") || act.equals("None")){
            if(isAudioPlay == true){
                isAudioPlay = false;
                mePlayer.pause();
            }
        }

    }

    private void ShowTosatOnScreen(String pre_act, String cur_act){

        if(pre_act.equals("None")){
            pre_action_time = System.currentTimeMillis();
        }else {
            int timeDifference;
            long currentTime = System.currentTimeMillis();
            timeDifference =(int) (currentTime - pre_action_time)/1000;
            int hour =  timeDifference / 3600;
            int minute = (timeDifference - hour * 3600) / 60;
            int second = (timeDifference - hour * 3600 - minute * 60);
            String time = hour + ":" + minute + ":" + second;
            Toast.makeText(getApplicationContext(), "You have just " + pre_act + " for " + time, Toast.LENGTH_SHORT).show();
            pre_action_time = currentTime;

        }
        String tempActionStartTime = String.valueOf(pre_action_time);
        InsertActionToDB(cur_act,tempActionStartTime);   //Write down the new action into database
    }

    private void InsertActionToDB(String act_name, String start_time){
        try{
            dbManager.openDB();
            dbManager.insert(act_name,start_time);
            dbManager.closeDB();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
