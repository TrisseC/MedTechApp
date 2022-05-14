package com.example.medtechapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MotionReaction extends AppCompatActivity implements SensorEventListener {

    private Handler handler;
    private Random random;
    private View root;
    private SensorManager sensorManager;

    private long startTime;
    private boolean hasCalled = false;
    private boolean paused = true;
    private ArrayList<Long> reactionTimes = new ArrayList<>();

    private float startRotation;
    private int direction; //-1 if left, 1 if right
    private float endRotation;

    /**
     * Final variables
     */
    final private int rounds = 5;
    final private double maxWait = 2000.0;
    final private double minWait = 500.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion);
        root = findViewById(R.id.screen).getRootView();
        handler = new Handler();
        random = new Random();

        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        findViewById(R.id.arrow).setVisibility(View.INVISIBLE);
    }

    public void startTest(View view){
        final MediaPlayer tapSound = MediaPlayer.create(this, R.raw.go);
        tapSound.start();

        clearContent();
        paused = false;
        hasCalled = false;
        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));

        long timer = (long) (minWait + (random.nextDouble() * (maxWait-minWait)));
        handler.postDelayed(() -> callBack(view), timer);
    }

    public void callBack(View view){
        hasCalled = true;
        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));

        direction = random.nextInt(2)*2-1;
        findViewById(R.id.arrow).setVisibility(View.VISIBLE);
        findViewById(R.id.arrow).setRotation(90*direction-90);
        endRotation = (45 + random.nextInt(135))*direction;

        // SHOW ARROW
        // INIT SENSORS
        // START TIMER

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
        float currentRotation = event.values[0];
        float frontTilt = Math.round(event.values[1]);
        float sideTilt = Math.round(event.values[2]);

        ((TextView)findViewById(R.id.timer2)).setText("" + currentRotation);

        if (paused) {
            startRotation = event.values[0];
            return;
        }

        if (frontTilt > 10 || frontTilt < -110 || sideTilt > 30 || sideTilt < -30) {
            wrongRotation();
        }

        if (!hasCalled && distance(currentRotation, startRotation) > 15) {
            tooFast();
        }

        if (distance(currentRotation, endRotation) < 15) {
            movementCompleted();
        }
    }

    public void movementCompleted() {
        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
    }

    public void wrongRotation() {
        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
    }

    public void tooFast() {
        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
    }

    private void clearContent(){
        findViewById(R.id.continueBtn3).setVisibility(View.INVISIBLE);
        findViewById(R.id.instructionText2).setVisibility(View.INVISIBLE);
        findViewById(R.id.arrow).setVisibility(View.INVISIBLE);
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