package com.example.medtechapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

public class MotionReaction extends AppCompatActivity {

    private View root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion);
        root = findViewById(R.id.screen).getRootView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MediaPlayer backSound = MediaPlayer.create(this, R.raw.back);
        backSound.start();
    }
}