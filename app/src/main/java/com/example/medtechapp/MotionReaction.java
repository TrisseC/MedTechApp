package com.example.medtechapp;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;

public class MotionReaction extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MediaPlayer backSound = MediaPlayer.create(this, R.raw.back);
        backSound.start();
    }
}