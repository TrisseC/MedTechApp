package com.example.medtechapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MotionReaction extends AppCompatActivity implements SensorEventListener {

    private Handler handler;
    private Random random;
    private View root;
    private SensorManager sensorManager;
    private Vibrator vibrator;

    private boolean hasCalled = false;
    private boolean paused = true;

    private long startTime;
    private ArrayList<Long> reactionTimes = new ArrayList<>();

    private float startRotation;
    private float currentRotation;
    private float endRotation;
    private int direction; //-1 if left, 1 if right

    /**
     * Final variables
     */
    final private int rounds = 5;
    final private double maxWait = 2000.0;
    final private double minWait = 1000.0;
    final private double minRotation = 80.0;
    final private double maxRotation = 150.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion);
        root = findViewById(R.id.screen).getRootView();
        handler = new Handler();
        random = new Random();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        findViewById(R.id.arrow).setVisibility(View.INVISIBLE);
        findViewById(R.id.timer2).setVisibility(View.INVISIBLE);
        TextView progressNumber = findViewById(R.id.progressNumber);
        progressNumber.setText(0 + "/" + rounds);
    }

    public void startButton(View view) {
        final MediaPlayer tapSound = MediaPlayer.create(this, R.raw.go);
        tapSound.start();

        clearContent();
        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));

        startTest();
    }

    public void startTest(){
        paused = false;
        hasCalled = false;
        startRotation = currentRotation;
        long timer = (long) (minWait + (random.nextDouble() * (maxWait-minWait)));
        handler.postDelayed(() -> callBack(), timer);
    }

    public void callBack(){
        if (distance(currentRotation, startRotation) > 10) {
            startRotation = currentRotation;
            handler.postDelayed(() -> callBack(), 500);
            return;
        }
        clearContent();
        hasCalled = true;
        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        vibrator.vibrate(200);

        direction = random.nextInt(2)*2-1;
        findViewById(R.id.arrow).setVisibility(View.VISIBLE);
        findViewById(R.id.arrow).setRotation(90*direction-90);

        endRotation = (currentRotation + direction * (float) (minRotation + random.nextDouble()*(maxRotation-minRotation))) % 360;
        startTime = System.currentTimeMillis();
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {  }

    public void onSensorChanged(SensorEvent event) {
        currentRotation = event.values[0];

        if (paused || !hasCalled) {
            return;
        }

        float frontTilt = event.values[1];
        float sideTilt = event.values[2];
        if (frontTilt > 5 || frontTilt < -105 || sideTilt > 50 || sideTilt < -50) {
            wrongRotation();
            return;
        }

        if (distance(currentRotation, endRotation) < 20) {
            movementCompleted();
        }
    }

    public void updateProgress(){
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress((100/rounds)*(reactionTimes.size()));
        TextView progressNumber = findViewById(R.id.progressNumber);
        progressNumber.setText(reactionTimes.size() + "/" + rounds);
    }

    public void movementCompleted() {
        interrupt();
        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        long reactionTime = System.currentTimeMillis() - startTime;

        reactionTimes.add(reactionTime);

        updateProgress();
        if (reactionTimes.size() >= rounds){
            endTest();
        } else {
            TextView instruction = findViewById(R.id.instructionText2);
            instruction.setVisibility(View.VISIBLE);
            instruction.setText("Bra jobbat!\nNästa runda kommer snart att börja");

            TextView timerTV = findViewById(R.id.timer2);
            timerTV.setVisibility(View.VISIBLE);
            timerTV.setText(Long.toString(reactionTime) + "ms");

            startTest();
        }
    }

    private void endTest() {
        clearContent();
        long averageReactionTime = 0;
        for (long rt : reactionTimes){
            averageReactionTime += rt;
        }
        averageReactionTime /= rounds;

        TextView instructions = findViewById(R.id.instructionText2);
        instructions.setVisibility(View.VISIBLE);
        instructions.setText("Bra jobbat!\nKlicka på tillbakapilen längst ner till höger för att gå till menyn");

        TextView timerTV = findViewById(R.id.timer2);
        timerTV.setVisibility(View.VISIBLE);
        timerTV.setText("Genomsnitt: " + Long.toString(averageReactionTime) + "ms");

        saveScore(averageReactionTime);
    }

    private void saveScore(long score){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> set = sharedPreferences.getStringSet("movement", new HashSet<String>());
        set.add(Long.toString(score));
        editor.putStringSet("movement", set);
        editor.apply();
    }

    public void wrongRotation() {
        interrupt();
        findViewById(R.id.continueBtn3).setVisibility(View.VISIBLE);
        TextView instruction = ((TextView) findViewById(R.id.instructionText2));
        instruction.setVisibility(View.VISIBLE);
        instruction.setText("Var god och håll telefonen upprätt!");
        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));
    }

    private void interrupt() {
        vibrator.vibrate(200);
        handler.removeCallbacksAndMessages(null);
        findViewById(R.id.arrow).setVisibility(View.INVISIBLE);
        paused = true;
        hasCalled = false;
    }

    private void clearContent(){
        findViewById(R.id.continueBtn3).setVisibility(View.INVISIBLE);
        findViewById(R.id.instructionText2).setVisibility(View.INVISIBLE);
        findViewById(R.id.arrow).setVisibility(View.INVISIBLE);
        findViewById(R.id.timer2).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacksAndMessages(null);
        MediaPlayer backSound = MediaPlayer.create(this, R.raw.back);
        backSound.start();
    }

    private float distance(float a, float b) {
        float distance = Math.abs(modulo(a,360) - modulo(b,360));
        return Math.min(distance, 360-distance);
    }

    private float modulo(float x, float y) {
        float xPrime = x;
        while(xPrime<0) {
            xPrime += y;
        }
        return xPrime % y;
    }

}