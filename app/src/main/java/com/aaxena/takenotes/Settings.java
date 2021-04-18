package com.aaxena.takenotes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.browser.customtabs.CustomTabsIntent;

import com.airbnb.lottie.LottieAnimationView;

public class Settings extends AppCompatActivity {
    LottieAnimationView loading;
    public static final String UI_MODE = "uiMode";
    AlertDialog alertDialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        loading = findViewById(R.id.sign_up_anim);
        loading.setVisibility(View.INVISIBLE);
        init();
    }

    private void init() {

        String product = Build.PRODUCT;
        String model = Build.MODEL;
        TextView model_display = findViewById(R.id.unique_id_model);
        model_display.setText(getString(R.string.device_id)+ product+"-TN-"+ model);
        Button share=findViewById(R.id.share);
        share.setOnClickListener(v -> {
            vibrateDevice();
            share.setVisibility(View.INVISIBLE);
            loading.setVisibility(View.VISIBLE);
            loading.playAnimation();
                int splash_screen_time_out = 2000;
                new Handler().postDelayed(() -> {
                    loading.setVisibility(View.GONE);
                    share.setVisibility(View.VISIBLE);
                }, splash_screen_time_out);
            /*Create an ACTION_SEND Intent*/
            Intent intent = new Intent(Intent.ACTION_SEND);
            /*This will be the actual content you wish you share.*/
            String shareBody = "Take Notes is an awesome app for writing handwritten notes, I am using it and believe it will help you too!\n\nDownload here: https://play.google.com/store/apps/details?id=com.aaxena.takenotes";
            /*The type of the content is text, obviously.*/
            intent.setType("text/plain");
            /*Applying information Subject and Body.*/
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
            intent.putExtra(Intent.EXTRA_TEXT, shareBody);
            /*Fire!*/
            startActivity(Intent.createChooser(intent, getString(R.string.share_using)));
        });

        Button profile = findViewById(R.id.myacc);
        profile.setOnClickListener(v -> {
            vibrateDevice();
            Intent i=new Intent(Settings.this,UserInfo.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        Button my_name = findViewById(R.id.my_name);
        my_name.setOnClickListener(v -> {
            vibrateDevice();
            Intent i=new Intent(Settings.this,MyName.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        Button devs = findViewById(R.id.devs);
        devs.setOnClickListener(v -> {
            vibrateDevice();
            Toast.makeText(Settings.this,"Tip: Tap on our PFP's to reveal more!",Toast.LENGTH_SHORT).show();
            String url = "https://the-rebooted-coder.github.io/Take-Notes/devs";
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setToolbarColor(Color.parseColor("#A2C994"));
            CustomTabsIntent customTabsIntent = builder.build();
            builder.setShowTitle(true);
            customTabsIntent.launchUrl(this, Uri.parse(url));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        Button privacy = findViewById(R.id.privacy);
        privacy.setOnClickListener(v -> {
            vibrateDevice();
            Intent i=new Intent(Settings.this,PrivacyPolicy.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        Button theme = findViewById(R.id.theme);
        theme.setOnClickListener(view -> CreateAlertDialogWithRadioButtonGroup());
    }
    private void vibrateDevice() {
        Vibrator v3 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v3.vibrate(VibrationEffect.createOneShot(28, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v3.vibrate(25);
        }
    }

    public void CreateAlertDialogWithRadioButtonGroup() {
        int nightModeFlags =
                this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
        builder.setTitle("Choose Overall Theme for Take Notes");
        builder.setMessage("There would still be a toggle on the home, to easily switch!");
        builder.setPositiveButton("Light", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (nightModeFlags) {
                    case Configuration.UI_MODE_NIGHT_YES:
                        vibrateDevice();
                        alertDialog1.dismiss();
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        SharedPreferences.Editor editor = getSharedPreferences(UI_MODE, MODE_PRIVATE).edit();
                        editor.putString("uiMode","Light");
                        editor.apply();
                        break;
                    case Configuration.UI_MODE_NIGHT_NO:
                        Toast.makeText(getApplicationContext(),"Already in Light Mode ☀️",Toast.LENGTH_SHORT).show();
                        alertDialog1.dismiss();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(),"Choose a theme",Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Dark", (dialog, which) -> {
            switch (nightModeFlags) {
                case Configuration.UI_MODE_NIGHT_YES:
                    Toast.makeText(getApplicationContext(),"Already in Dark Mode \uD83C\uDF19",Toast.LENGTH_SHORT).show();
                    alertDialog1.dismiss();
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                    vibrateDevice();
                    alertDialog1.dismiss();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    SharedPreferences.Editor editor = getSharedPreferences(UI_MODE, MODE_PRIVATE).edit();
                    editor.putString("uiMode","Dark");
                    editor.apply();
                    break;
                default:
                    Toast.makeText(getApplicationContext(),"Choose a theme",Toast.LENGTH_SHORT).show();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            builder.setNeutralButton("System Default", (dialog, which) -> {
                vibrateDevice();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                SharedPreferences.Editor editor = getSharedPreferences(UI_MODE, MODE_PRIVATE).edit();
                editor.putString("uiMode","System");
                editor.apply();
                alertDialog1.dismiss();
            });
        }
        alertDialog1 = builder.create();
        alertDialog1.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(Settings.this,Landing.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
