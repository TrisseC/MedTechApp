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
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MotionReaction extends AppCompatActivity implements SensorEventListener {

    private Handler handler;
    private Random random;
    private View root;
    private SensorManager sensorManager;
    private Vibrator vibrator;

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
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        findViewById(R.id.arrow).setVisibility(View.INVISIBLE);
        findViewById(R.id.timer2).setVisibility(View.INVISIBLE);
    }

    public void startTest(View view){
        final MediaPlayer tapSound = MediaPlayer.create(this, R.raw.go);
        tapSound.start();

        clearContent();
        paused = false;
        hasCalled = false;
        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
        ((Button) findViewById(R.id.continueBtn3)).setText("Fortsätt");

        long timer = (long) (minWait + (random.nextDouble() * (maxWait-minWait)));
        handler.postDelayed(() -> callBack(view), timer);
    }

    public void callBack(View view){
        hasCalled = true;
        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        vibrator.vibrate(200);

        direction = random.nextInt(2)*2-1;
        findViewById(R.id.arrow).setVisibility(View.VISIBLE);
        findViewById(R.id.arrow).setRotation(90*direction-90);
        endRotation = (startRotation + (45 + random.nextInt(135))*direction)%360;

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

        if (paused) {
            startRotation = event.values[0];
            return;
        }

        if (frontTilt > 10 || frontTilt < -110 || sideTilt > 30 || sideTilt < -30) {
            wrongRotation();
            return;
        }

        if (!hasCalled && distance(currentRotation, startRotation) > 15) {
            tooFast();
            return;
        }

        if (hasCalled && distance(currentRotation, endRotation) < 15) {
            movementCompleted();
        }
    }

    public void movementCompleted() {
        interrupt();
        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
    }

    public void wrongRotation() {
        interrupt();
        TextView instruction = ((TextView) findViewById(R.id.instructionText2));
        instruction.setVisibility(View.VISIBLE);
        instruction.setText("Var god och håll telefonen upprätt!");
        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
    }

    public void tooFast() {
        interrupt();
        TextView instruction = ((TextView) findViewById(R.id.instructionText2));
        instruction.setVisibility(View.VISIBLE);
        instruction.setText("Håll mobilen stilla innan testet startar!");
        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
    }

    private void interrupt() {
        vibrator.vibrate(200);
        handler.removeCallbacksAndMessages(null);
        findViewById(R.id.arrow).setVisibility(View.INVISIBLE);
        paused = true;
        hasCalled = false;
        findViewById(R.id.continueBtn3).setVisibility(View.VISIBLE);
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