package com.example.medtechapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class TouchReaction extends AppCompatActivity {
    private Handler handler;
    private Random random;
    private View root;
    private String state;
    private Vibrator vibrator;
    private MediaPlayer mediaPlayer = null;

    private long startTime;
    private boolean hasCalled = false;
    private boolean paused = true;
    private ArrayList<Long> reactionTimes = new ArrayList<>();

    /**
     * Final variables
     */
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

        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        findViewById(R.id.timer).setVisibility(View.INVISIBLE);
        findViewById(R.id.tooFastText).setVisibility(View.INVISIBLE);

        TextView instructions = findViewById(R.id.instructionText);
        if (state.equals("sound")) {
            instructions.setText(Html.fromHtml("<b>Tryck</b> så snabbt du kan när du hör en signal.<br>Kom ihåg att höja volymen!"));
        } else if (state.equals("vibration")) {
            instructions.setText(Html.fromHtml("<b>Tryck</b> så snabbt du kan när du känner en vibration"));
        } else if (state.equals("visual")){
            instructions.setText(Html.fromHtml("<b>Tryck</b> så snabbt du kan när den röda skärmen blir grön"));
        }
    }

    public void startTest(View view) {
        MediaPlayer tapSound = MediaPlayer.create(this, R.raw.go);
        tapSound.start();

        hasCalled = false;
        paused = false;

        clearContent();
        if (state.equals("sound")) {
            root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
            mediaPlayer = MediaPlayer.create(this, R.raw.notice);
            mediaPlayer.setVolume(1,1);
        } else if (state.equals("vibration")) {
            root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
        } else if (state.equals("visual")){
            root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        }
        ((Button) findViewById(R.id.continueBtn)).setText("Fortsätt");

        long timer = (long) (minWait + (random.nextDouble() * (maxWait-minWait)));
        handler.postDelayed(() -> callBack(), timer);
    }

    public void callBack(){
        hasCalled = true;
        startTime = System.currentTimeMillis();

        if (state.equals("sound")) {
            mediaPlayer.start();
            Log.w("Audio Test","Duration: " + mediaPlayer.getDuration());
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
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

        paused = true;
        findViewById(R.id.continueBtn).setVisibility(View.VISIBLE);

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
        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));

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
        instructions.setVisibility(View.VISIBLE);
        instructions.setText("Bra jobbat!\nKlicka på tillbakapilen för att gå till menyn");


        TextView timerTV = findViewById(R.id.timer);
        timerTV.setVisibility(View.VISIBLE);
        timerTV.setText("Genomsnitt: " + Long.toString(averageReactionTime) + "ms");

        //skicka till highscore
        saveScore(averageReactionTime);
    }

    private void clearContent(){
        findViewById(R.id.continueBtn).setVisibility(View.INVISIBLE);
        findViewById(R.id.timer).setVisibility(View.INVISIBLE);
        findViewById(R.id.tooFastText).setVisibility(View.INVISIBLE);
        findViewById(R.id.instructionText).setVisibility(View.INVISIBLE);
    }

    private void saveScore(long score){
        //taget från androidauthority och stackoverflow posts
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> set = sharedPreferences.getStringSet(state, new HashSet<String>());
        set.add(Long.toString(score));
        editor.putStringSet(state, set);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacksAndMessages(null);
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

        MediaPlayer backSound = MediaPlayer.create(this, R.raw.back);
        backSound.start();
    }

}