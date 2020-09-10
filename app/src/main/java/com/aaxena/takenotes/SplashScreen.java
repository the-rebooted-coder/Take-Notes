package com.aaxena.takenotes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        //Initiating Flash Screen
        fireSplashScreen();
    }
    private void fireSplashScreen() {
        int splash_screen_time_out = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(30);
                Intent i=new Intent(SplashScreen.this,DeployedChooser.class);
                startActivity(i);
                finish();
            }
        }, splash_screen_time_out);
    }
}
