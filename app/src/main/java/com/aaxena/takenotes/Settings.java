package com.aaxena.takenotes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
    }

    private void init() {
        Button share=findViewById(R.id.share);
        share.setOnClickListener(v -> {
            Vibrator v2 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v2.vibrate(22);
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

        Button tn_journey = findViewById(R.id.take_notes_journey);
        tn_journey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Vibrator v2 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v2.vibrate(25);
                Uri uri = Uri.parse("https://the-rebooted-coder.github.io/Take-Notes/take_notes_journey");
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        Button profile = findViewById(R.id.myacc);
        profile.setOnClickListener(v -> {
            Vibrator v2 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v2.vibrate(25);
            Intent i=new Intent(Settings.this,UserInfo.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        Button request = findViewById(R.id.request);
        request.setOnClickListener(v -> {
            Vibrator v2 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v2.vibrate(25);
            Intent i=new Intent(Settings.this,feature.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        Button devs = findViewById(R.id.devs);
        devs.setOnClickListener(v -> {
            Vibrator v2 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v2.vibrate(25);
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
            Vibrator v2 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v2.vibrate(25);
            Intent i=new Intent(Settings.this,PrivacyPolicy.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
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
