package com.example.cooltimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SeekBar seekBar;
    private TextView textView;
    private boolean isTimerOn;
    private Button button;
    private  CountDownTimer countDownTimer;
    private  int defaultInterval;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = findViewById(R.id.seekBar);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        seekBar.setMax(600);
        isTimerOn = false;
        setIntervalFromSharedPreferences(sharedPreferences);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                long progressInMillis = progress * 1000;
                updateTimer(progressInMillis);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

    }
    public void start(View view) {

        if (!isTimerOn) {
            button.setText("Stop");
            seekBar.setEnabled(false);
            isTimerOn = true;
            countDownTimer = new CountDownTimer(seekBar.getProgress() * 1000 , 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    updateTimer(millisUntilFinished);
                }
                @Override
                public void onFinish() {

                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                    if (sharedPreferences.getBoolean("enable_sound" , true) ){

                        String melodyName = sharedPreferences.getString("timer_melody" , "zakrivayu_glaza");
                        if (melodyName.equals("zakrivayu_glaza")){
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(),
                                    R.raw.zakrivayuglaza);
                            mediaPlayer.start();
                        } else if (melodyName.equals("origami")) {
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext() ,
                                    R.raw.origami);
                            mediaPlayer.start();
                        } else if (melodyName.equals("master_margarita")) {
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext() ,
                                    R.raw.mastermargaqrita);
                            mediaPlayer.start();
                        }



                    }

                    resetTimer();

                }
            };
            countDownTimer.start();
        } else {
            resetTimer();
        }

    }
    private void updateTimer(long millisUntilFinished){

        int minutes = (int) millisUntilFinished/1000/60;
        int seconds = (int) millisUntilFinished/1000 - (minutes * 60);

        String minutesString ="";
        String secondString = "";

        if (minutes < 10){
            minutesString = "0" + minutes;
        }else {
            minutesString = String.valueOf(minutes);
        }

        if (minutes < 10){
            secondString = "0" + seconds;
        }else {
            secondString = String.valueOf(seconds);
        }

        textView.setText(minutesString + ":" + secondString);

    }

    private void resetTimer(){
        countDownTimer.cancel();
        button.setText("Start");
        seekBar.setEnabled(true);
        isTimerOn = false;
        setIntervalFromSharedPreferences(sharedPreferences);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.timer_menu , menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.action_settings){
            Intent openSettings = new Intent(this , SettingsActivity.class);
            startActivity(openSettings);
            return true;
        } else if(id == R.id.action_about) {
            Intent openAbout = new Intent(this , AboutActivity.class);
            startActivity(openAbout);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setIntervalFromSharedPreferences(SharedPreferences sharedPreferences){
        try {
            defaultInterval = Integer.valueOf(sharedPreferences.getString("default_interval" , "30"));
        }catch (Exception ex){
            Toast.makeText(this , "Some error hapens" , Toast.LENGTH_LONG).show();
        }
        long defaultIntervalMillis = defaultInterval * 1000;
        updateTimer(defaultIntervalMillis);
        seekBar.setProgress(defaultInterval);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("default_interval")){
            setIntervalFromSharedPreferences(sharedPreferences);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}
