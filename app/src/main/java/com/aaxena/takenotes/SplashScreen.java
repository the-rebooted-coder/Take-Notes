package com.aaxena.takenotes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class SplashScreen extends AppCompatActivity {
    protected AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        fireSplashScreen();
        TextView appName = findViewById(R.id.title);
        appName.setText(R.string.app_name);
        appName.startAnimation(fadeIn);
        fadeIn.setDuration(1200);
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