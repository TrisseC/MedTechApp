package com.example.medtechapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import java.util.Random;

public class MotionReaction extends AppCompatActivity implements SensorEventListener {

    private Handler handler;
    private Random random;
    private View root;
    final private int rounds = 5;
    final private double maxWait = 3500.0;
    final private double minWait = 1000.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion);
        root = findViewById(R.id.screen).getRootView();
        handler = new Handler();
        random = new Random();
    }

    public void startTest(View view){
        final MediaPlayer tapSound = MediaPlayer.create(this, R.raw.go);
        tapSound.start();
        long timer = (long) (minWait + (random.nextDouble() * (maxWait-minWait)));
        handler.postDelayed(() -> callBack(view), timer);
    }

    public void callBack(View view){

        // SHOW ARROW
        // INIT SENSORS
        // START TIMER

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacksAndMessages(null);
        MediaPlayer backSound = MediaPlayer.create(this, R.raw.back);
        backSound.start();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // Handle STOP HERE
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}