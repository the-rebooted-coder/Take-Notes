package com.aaxena.takenotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import static com.aaxena.takenotes.SignUp.STATUS;

public class SplashScreen extends AppCompatActivity {
    protected AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
    String name;
    public static final String UI_MODE = "uiMode";
    String acc_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences(UI_MODE, MODE_PRIVATE);
        name = prefs.getString("uiMode", "System");
        applyUI();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        SharedPreferences prefsmanager = getSharedPreferences(STATUS, MODE_PRIVATE);
        acc_status = prefsmanager.getString("acc_status", "okay");
        if (acc_status.equals("suspended")) {
            Toast.makeText(SplashScreen.this,"Your account is temporarily suspended due to proactive use, contact the developer",Toast.LENGTH_LONG).show();
            finish();
        }
        else {
            fireSplashScreen();
            TextView appName = findViewById(R.id.title);
            appName.setText(R.string.app_name);
            appName.startAnimation(fadeIn);
            fadeIn.setDuration(1200);
        }
    }
    private void applyUI() {
        if (name.equals("Dark")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else if (name.equals("Light")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }
    private void fireSplashScreen() {
        int splash_screen_time_out = 2800;
        new Handler().postDelayed(() -> {
            check();
            finish();
        }, splash_screen_time_out);
    }
    private void check(){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account !=null){
            //User Signed In, Proceeding to Landing
            Intent i=new Intent(SplashScreen.this,Landing.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        else {
            //Newbie
            Intent i=new Intent(SplashScreen.this,WelcomeActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }
}