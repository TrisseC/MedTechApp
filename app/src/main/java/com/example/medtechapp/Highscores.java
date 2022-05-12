package com.example.medtechapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Highscores extends AppCompatActivity {

    private View root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscores);

        getScores("sound");
        getScores("vibration");
        getScores("visual");
        getScores("movement");

        root = findViewById(R.id.screen).getRootView();
        root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
    }


    private void getScores(String state){
        //SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        TextView scoreTextView;
        if (state.equals("sound")) {
            scoreTextView = findViewById(R.id.ljudScores);
        } else if (state.equals("vibration")){
            scoreTextView = findViewById(R.id.vibScores);
        } else if (state.equals("visual")){
            scoreTextView = findViewById(R.id.visScores);
        } else{
            scoreTextView = findViewById(R.id.moveScores);
        }
        System.out.println("getting set of " + state);
        Set<String> set = sharedPreferences.getStringSet(state, new HashSet<String>());
        System.out.println("set: " + set.toString());

        List<String> list = new ArrayList<>(set);
        List<Integer> intList = new ArrayList<>();
        for(String s : list){
            intList.add(Integer.parseInt(s));
        }
        Collections.sort(intList);

        int counter = 1;
        scoreTextView.setText("");
        for(int score : intList){
            scoreTextView.setText(scoreTextView.getText() + "  " + Integer.toString(counter) + ": "
            + Integer.toString(score) + " ms\n");
            counter++;
            if (counter >= 21){break;}
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MediaPlayer backSound = MediaPlayer.create(this, R.raw.back);
        backSound.start();
    }
}