package com.example.medtechapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private View root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        root = findViewById(R.id.screen).getRootView();
        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
    }

    public void openHighscores(View view){
        playClickSound();
        Intent intent = new Intent(this, Highscores.class);
        startActivity(intent);
    }

    public void openVibration(View view){
        playClickSound();
        Intent intent = new Intent(this, TouchReaction.class);
        intent.putExtra("state", "vibration");
        startActivity(intent);
    }

    public void openVisual(View view){
        playClickSound();
        Intent intent = new Intent(this, TouchReaction.class);
        intent.putExtra("state", "visual");
        startActivity(intent);
    }

    public void openSound(View view){
        playClickSound();
        Intent intent = new Intent(this, TouchReaction.class);
        intent.putExtra("state", "sound");
        startActivity(intent);
    }

    private void playClickSound() {
        MediaPlayer clickSound = MediaPlayer.create(this, R.raw.go);
        clickSound.start();
    }

    //spela ljud på "back"
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MediaPlayer backSound = MediaPlayer.create(this, R.raw.back);
        backSound.start();
    }
}