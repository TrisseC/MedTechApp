package com.example.medtechapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.util.Random;

public class Visual extends AppCompatActivity {
    private Handler handler;
    private Random random;
    private int maxSecondsWait;
    private long startTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual);
        handler = new Handler();
        random = new Random();

        maxSecondsWait = 5; //Sets max value of how long you should wait for colour to turn green;
    }

    public void startTest(View view){
        long timeTaken;
        long timer = (long) (random.nextDouble() * 1000.0 * maxSecondsWait);
        handler.postDelayed(new Runnable() {
            public void run() {
                callChangeColour();
                // Actions to do after maxSecondsWait seconds
            }
        }, timer);
        int i = Log.w("Timer-Test", String.valueOf(timer));


        timeTaken =  System.currentTimeMillis() - startTime;
        // print timeTaken; set BG colour
    }
    public void callChangeColour(){
        startTime = System.currentTimeMillis();
        View someView = findViewById(R.id.screen);
        View root = someView.getRootView();
        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black));
    }
}