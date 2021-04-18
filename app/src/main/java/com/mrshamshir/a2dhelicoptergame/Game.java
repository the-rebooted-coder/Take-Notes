package com.mrshamshir.a2dhelicoptergame;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class Game extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //turn title off
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // get last bestScore from shared preferences
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        int currentHighScore = sharedPref.getInt("best", 0);

        GamePanel panel = new GamePanel(this, currentHighScore);

        // set listener for handling new high score
        panel.setHighScoreListener(new GamePanel.HighScoreListener() {
            @Override
            public void onHighScoreUpdated(int best) {
                // code to handle updates

                // Update shared preferences
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("best", best);
                editor.commit();
            }

        });
        setContentView(panel);
    }



}
