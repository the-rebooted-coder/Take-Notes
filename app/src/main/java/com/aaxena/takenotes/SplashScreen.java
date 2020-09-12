package com.aaxena.takenotes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;

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
                check();
                finish();
            }
        }, splash_screen_time_out);
    }
    private void check(){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account !=null){
            //User Signed In, Proceeding to Landing
            Intent i=new Intent(SplashScreen.this,Landing.class);
            startActivity(i);
        }
        else {
            //Newbie
            Intent i=new Intent(SplashScreen.this,SignUp.class);
            startActivity(i);
        }
    }
}