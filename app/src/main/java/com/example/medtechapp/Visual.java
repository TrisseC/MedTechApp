package com.example.medtechapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

public class Visual extends AppCompatActivity {
    private Handler handler;
    private Random random;
    private int maxSecondsWait;
    private long startTime;
    private View root;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual);
        handler = new Handler();
        random = new Random();
        View someView = findViewById(R.id.screen);
        root = someView.getRootView();
        maxSecondsWait = 3; //Sets max value of how long you should wait for colour to turn green;
    }

    public void startTest(View view){
        long timeTaken;
        view.setVisibility(View.INVISIBLE);
        findViewById(R.id.visualStartText).setVisibility(View.INVISIBLE);
        long timer = (long) (500.0+(random.nextDouble() * 1000.0 * maxSecondsWait));
        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        handler.postDelayed(new Runnable() {
            public void run() {
                callChangeColour();
                // Actions to do after maxSecondsWait seconds
            }
        }, timer);
        int i = Log.w("Timer-Test", String.valueOf(timer));
    }
    public void callChangeColour(){
        startTime = System.currentTimeMillis();
        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
    }

    public void stopTime(View view){
        ColorDrawable viewColor = (ColorDrawable) root.getBackground();
        int j = Log.w("Background Code" , Integer.toString(viewColor.getColor()));
        int colorId = viewColor.getColor();
        if(colorId == -10053376){
            long reactionTime = System.currentTimeMillis() - startTime;
            TextView textView = findViewById(R.id.timer);
            textView.setVisibility(View.VISIBLE);
            textView.setText(Long.toString(reactionTime));
            root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
        }
    }
}