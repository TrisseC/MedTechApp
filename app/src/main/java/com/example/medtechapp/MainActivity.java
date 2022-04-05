package com.example.medtechapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openMoveActivity(View view){
        Intent intent = new Intent(this, Motion.class);
        startActivity(intent);
    }

    public void openHighscores(View view){
        Intent intent = new Intent(this, Highscores.class);
        startActivity(intent);
    }

    public void openVisual(View view){
        Intent intent = new Intent(this, Visual.class);
        startActivity(intent);
    }

    public void openSound(View view){
        Intent intent = new Intent(this, Sound.class);
        startActivity(intent);
    }
}