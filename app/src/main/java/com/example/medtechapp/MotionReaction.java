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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacksAndMessages(null);
        MediaPlayer backSound = MediaPlayer.create(this, R.raw.back);
        backSound.start();
    }

    /**
     * Called whenever the user presses the "Fortsätt" or "Starta testet" button.
     * Will clear the screen and call the startTest
     */
    public void startButton(View view) {
        final MediaPlayer tapSound = MediaPlayer.create(this, R.raw.go);
        tapSound.start();

        clearContent();
        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));

        startTest();
    }

    /**
     * Prepares the phone for the test
     */
    public void startTest(){
        paused = false;
        hasCalled = false;
        startRotation = currentRotation;
        long timer = (long) (minWait + (random.nextDouble() * (maxWait-minWait)));
        handler.postDelayed(() -> callBack(), timer);
    }

    /**
     * Called within a random time interval after startTest.
     * Displays an arrow on the screen and starts the time keeping
     */
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

    /**
     * Updates the progress bar at the bottom of the screen
     */
    public void updateProgress(){
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress((100/rounds)*(reactionTimes.size()));
        TextView progressNumber = findViewById(R.id.progressNumber);
        progressNumber.setText(reactionTimes.size() + "/" + rounds);
    }

    /**
     * Called when the user moves the phone to the correct rotation
     * This function will stop the test and register the time
     */
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

    /**
     * After the test has been completed a number of times equal to the rounds value this function is called
     * The function will end the test and calculate the average time.
     */
    private void endTest() {
        clearContent();
        long averageReactionTime = 0;
        for (long rt : reactionTimes){
            averageReactionTime += rt;
        }
        averageReactionTime /= rounds;

        TextView instructions = findViewById(R.id.instructionText2);
        instructions.setVisibility(View.VISIBLE);
        instructions.setText("Bra jobbat!\nKlicka på pilen längst ner för att gå tillbaka till menyn");

        TextView timerTV = findViewById(R.id.timer2);
        timerTV.setVisibility(View.VISIBLE);
        timerTV.setText("Genomsnitt: " + Long.toString(averageReactionTime) + "ms");

        saveScore(averageReactionTime);
    }

    /**
     * Saves the average score as a shared preference
     * @param score
     */
    private void saveScore(long score){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> set = sharedPreferences.getStringSet("movement", new HashSet<String>());
        set.add(Long.toString(score));
        editor.putStringSet("movement", set);
        editor.apply();
    }

    /**
     * Called whenever the user holds the phone incorrectly
     * Will stop the test
     */
    public void wrongRotation() {
        interrupt();
        findViewById(R.id.continueBtn3).setVisibility(View.VISIBLE);
        TextView instruction = findViewById(R.id.instructionText2);
        instruction.setVisibility(View.VISIBLE);
        instruction.setText("Var god och håll telefonen upprätt!");
        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));
    }

    /**
     * Stops the test
     */
    private void interrupt() {
        vibrator.vibrate(200);
        handler.removeCallbacksAndMessages(null);
        findViewById(R.id.arrow).setVisibility(View.INVISIBLE);
        paused = true;
        hasCalled = false;
    }

    /**
     * Sets some views to invisible
     */
    private void clearContent(){
        findViewById(R.id.continueBtn3).setVisibility(View.INVISIBLE);
        findViewById(R.id.instructionText2).setVisibility(View.INVISIBLE);
        findViewById(R.id.arrow).setVisibility(View.INVISIBLE);
        findViewById(R.id.timer2).setVisibility(View.INVISIBLE);
    }

    /**
     * Calculates the distance between two rotational degrees
     * @param a
     * @param b
     * @return distance
     */
    private float distance(float a, float b) {
        float distance = Math.abs(modulo(a,360) - modulo(b,360));
        return Math.min(distance, 360-distance);
    }

    /**
     * Calculates the modulo x of y (works on negative numbers compared to the % operation)
     * @param x
     * @param y
     * @return modulo
     */
    private float modulo(float x, float y) {
        float xPrime = x;
        while(xPrime<0) {
            xPrime += y;
        }
        return xPrime % y;
    }

    /*************************/
    /**** SENSOR FUNCTIONS ***/
    /*************************/

    @Override
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

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {  }
}