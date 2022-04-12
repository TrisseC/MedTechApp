package com.example.medtechapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class Sound extends AppCompatActivity {
    private Handler handler;
    private Random random;
    private int maxSecondsWait;
    private long startTime;
    private View root;
    private ArrayList<Long> reactionTimes = new ArrayList<>();
    private boolean hasCalled = false;
    private boolean paused = false;
    private int testAmount = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);
        handler = new Handler();
        random = new Random();
        View someView = findViewById(R.id.screen);
        root = someView.getRootView();

        maxSecondsWait = 3;

        contInvis();
    }





    public void startTest(View view){
        hasCalled = false;
        paused = false;
        view.setVisibility(View.INVISIBLE);
        findViewById(R.id.soundStartText).setVisibility(View.INVISIBLE);
        findViewById(R.id.timer).setVisibility(View.INVISIBLE);
        long timer = (long) (500.0+(random.nextDouble() * 1000.0 * maxSecondsWait));
        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.background_dark));
        handler.postDelayed(() -> {
            callMakeSound(view);
            // Actions to do after maxSecondsWait seconds
        }, timer);
    }
    public void callMakeSound(View view){
        hasCalled = true;
        startTime = System.currentTimeMillis();
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.bruh);
        mp.start();
    }

    public void stopTime(View view){
        if(!paused){
            root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));
            long reactionTime;
            if (hasCalled){
                reactionTime = System.currentTimeMillis() - startTime;
                TextView timerTV = findViewById(R.id.timer);
                timerTV.setVisibility(View.VISIBLE);
                timerTV.setText(Long.toString(reactionTime) + "ms");
                reactionTimes.add(reactionTime);
            }
            else{
                //visa en textview med typ "ahh du klickade för snabbt"
            }
            contVis();
            hasCalled = false;
            paused = true;
            if (reactionTimes.size() >= testAmount){
                endTest();
            }
        }
    }

    private void contVis(){
        findViewById(R.id.continueBtn).setVisibility(View.VISIBLE);
        findViewById(R.id.continueText).setVisibility(View.VISIBLE);
    }

    private void contInvis(){
        findViewById(R.id.continueBtn).setVisibility(View.INVISIBLE);
        findViewById(R.id.continueText).setVisibility(View.INVISIBLE);
    }


    private void endTest() {
        contInvis();
        long averageReactionTime = 0;
        for (long rt : reactionTimes){
            averageReactionTime += rt;
        }
        averageReactionTime /= testAmount;

        //skicka till highscores och stäng ner
        TextView timerTV = findViewById(R.id.timer);
        timerTV.setVisibility(View.VISIBLE);
        timerTV.setText("average: " + Long.toString(averageReactionTime) + "ms");
    }
}