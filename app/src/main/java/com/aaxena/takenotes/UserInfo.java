package com.aaxena.takenotes;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikhaellopez.circularimageview.CircularImageView;

import static com.aaxena.takenotes.MyName.SHARED_PREFS;
import static com.aaxena.takenotes.MyName.TEXT;

public class UserInfo extends AppCompatActivity {
   CircularImageView photo;
   TextView username;
   TextView email;
   Button signOut;
   FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#1389cd"));

        Button change_name = findViewById(R.id.chng_name);
        TextView user_save_name =  findViewById(R.id.saving_name);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String saving_as = sharedPreferences.getString(TEXT, "");
        if(saving_as.isEmpty()){
            user_save_name.setText(R.string.tap_to_save_name);
            change_name.setOnClickListener(view -> {
                Vibrator v2 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v2.vibrate(20);
                Intent toName = new Intent(UserInfo.this,MyName.class);
                startActivity(toName);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            });
        }
        else {
            user_save_name.setText(saving_as);
            change_name.setVisibility(View.INVISIBLE);
        }


        Button abt_dev = findViewById(R.id.abt_dev);
        abt_dev.setOnClickListener(v -> {
            Vibrator dev_vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            dev_vibe.vibrate(30);
            Toast.makeText(UserInfo.this, R.string.developer,Toast.LENGTH_LONG).show();
        });

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        photo = findViewById(R.id.accphoto);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        signOut = findViewById(R.id.sign_out);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();

        if (account !=null){
            //Google
            String personName = account.getDisplayName();
            username.setText(personName);
            String personEmail = account.getEmail();
            email.setText(personEmail);
            Uri photoUrl = account.getPhotoUrl(); Glide.with(this).load(photoUrl).into(photo);
            signOut.setOnClickListener(v -> {
                Vibrator v2 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v2.vibrate(30);
                Toast.makeText(this, R.string.sign_out_greeting,Toast.LENGTH_SHORT).show();
                Toast.makeText(this,"Goodbye "+personName,Toast.LENGTH_SHORT).show();
                int death_text = 2800;
                new Handler().postDelayed(() -> {
                    ((ActivityManager)this.getSystemService(ACTIVITY_SERVICE))
                            .clearApplicationUserData();
                }, death_text);
            });
        }
        else {
            //Facebook
            String name = mUser.getDisplayName();
            String fbmail = mUser.getEmail();
            String photoURL = mUser.getPhotoUrl().toString();
            Glide.with(this).load(photoURL).into(photo);
            username.setText(name);
            email.setText(fbmail);
            signOut.setOnClickListener(v -> {
                Vibrator v2 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v2.vibrate(30);
                Toast.makeText(this, R.string.sign_out_greeting,Toast.LENGTH_SHORT).show();
                Toast.makeText(this,"Goodbye "+name,Toast.LENGTH_SHORT).show();
                int death_text = 2800;
                new Handler().postDelayed(() -> {
                    ((ActivityManager)this.getSystemService(ACTIVITY_SERVICE))
                            .clearApplicationUserData();
                }, death_text);
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(UserInfo.this,Settings.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
