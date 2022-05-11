package com.example.medtechapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.Random;

public class TouchReaction extends AppCompatActivity {
    private Handler handler;
    private Random random;
    private View root;
    private String state;
    private Vibrator vibrator;

    private long startTime;
    private boolean hasCalled = false;
    private boolean paused = false;
    private ArrayList<Long> reactionTimes = new ArrayList<>();

    //final variables
    final private int rounds = 5;
    final private double maxWait = 3500.0;
    final private double minWait = 1000.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touchreaction);
        handler = new Handler();
        random = new Random();
        root = findViewById(R.id.screen).getRootView();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        state = getIntent().getStringExtra("state");

        findViewById(R.id.timer).setVisibility(View.INVISIBLE);
        findViewById(R.id.tooFastText).setVisibility(View.INVISIBLE);

        TextView instructions = findViewById(R.id.instructionText);
        if (state.equals("sound")) {
            instructions.setText("När du hör en signal, tryck så snabbt du kan." +
                    "\n Kom ihåg att höja volymen på mobilen");
        } else if (state.equals("vibration")) {
            instructions.setText("När du känner en vibration, tryck så snabbt du kan.");
        } else if (state.equals("visual")){
            instructions.setText("När den röda skärmen blir grön, tryck så snabbt du kan.");
        }
    }

    public void startTest(View view) {
        final MediaPlayer tapSound = MediaPlayer.create(this, R.raw.go);
        tapSound.start();

        hasCalled = false;
        paused = false;

        clearContent();
        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        ((Button) findViewById(R.id.continueBtn)).setText("Fortsätt");

        long timer = (long) (minWait + (random.nextDouble() * (maxWait-minWait)));
        handler.postDelayed(() -> callBack(view), timer);
    }

    public void callBack(View view){
        hasCalled = true;
        startTime = System.currentTimeMillis();

        if (state.equals("sound")) {
            final MediaPlayer testSound = MediaPlayer.create(this, R.raw.notice);
            testSound.setVolume(1,1);
            testSound.start();
            Log.w("Audio Test","Duration: "+testSound.getDuration());
            //startTime = System.currentTimeMillis();
        } else if (state.equals("vibration")) {
            vibrator.vibrate(250);
        } else if (state.equals("visual")){
            root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        }
    }

    public void updateProgress(){
        ProgressBar progressBar = findViewById(R.id.progressBar2);
        progressBar.setProgress((100/rounds)*(reactionTimes.size()));
    }

    public void stopTime(View view){
        if (paused) {
            return;
        }

        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
        findViewById(R.id.continueBtn).setVisibility(View.VISIBLE);
        paused = true;

        if (!hasCalled) {
            handler.removeCallbacksAndMessages(null);
            findViewById(R.id.tooFastText).setVisibility(View.VISIBLE);
            root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));
            return;
        }

        long reactionTime = System.currentTimeMillis() - startTime;
        TextView timerTV = findViewById(R.id.timer);
        timerTV.setVisibility(View.VISIBLE);
        timerTV.setText(Long.toString(reactionTime) + "ms");
        reactionTimes.add(reactionTime);
        hasCalled = false;
        updateProgress();
        if (reactionTimes.size() >= rounds){
            endTest();
        }
    }

    private void endTest() {
        clearContent();
        long averageReactionTime = 0;
        for (long rt : reactionTimes){
            averageReactionTime += rt;
        }
        averageReactionTime /= rounds;

        TextView instructions = findViewById(R.id.instructionText);
        instructions.setText("Bra jobbat!\nKlicka på tillbakapilen för att gå till menyn");
        findViewById(R.id.instructionText).setVisibility(View.VISIBLE);

        //skicka till highscores
        TextView timerTV = findViewById(R.id.timer);
        timerTV.setVisibility(View.VISIBLE);
        timerTV.setText("Genomsnitt: " + Long.toString(averageReactionTime) + "ms");
    }

    private void clearContent(){
        findViewById(R.id.continueBtn).setVisibility(View.INVISIBLE);
        findViewById(R.id.timer).setVisibility(View.INVISIBLE);
        findViewById(R.id.tooFastText).setVisibility(View.INVISIBLE);
        findViewById(R.id.instructionText).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MediaPlayer backSound = MediaPlayer.create(this, R.raw.back);
        backSound.start();
    }

}