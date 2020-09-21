package com.aaxena.takenotes;

import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class UserInfo extends AppCompatActivity {
   ImageView photo;
   TextView username;
   TextView email;
   Button signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        photo = findViewById(R.id.accphoto);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        signOut = findViewById(R.id.sign_out);

        signOut.setOnClickListener(v -> {
            Vibrator v2 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v2.vibrate(30);
            Toast.makeText(this, R.string.sign_out_greeting,Toast.LENGTH_LONG).show();
            int death_text = 2700;
            new Handler().postDelayed(() -> {
                ((ActivityManager)this.getSystemService(ACTIVITY_SERVICE))
                        .clearApplicationUserData();
            }, death_text);
        });

        if (account !=null){
            //User Signed In, Displaying Info
            String personName = account.getDisplayName();
            username.setText(personName);
            String personEmail = account.getEmail();
            email.setText(personEmail);
            Uri photoUrl = account.getPhotoUrl(); Glide.with(this).load(photoUrl).into(photo);
        }
        else {
            //Opened by Mistake
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(30);
            Toast.makeText(this, R.string.not_yet_in,Toast.LENGTH_LONG).show();
        }
    }

}
