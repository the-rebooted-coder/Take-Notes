package com.aaxena.takenotes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class devOnePopup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dev_one_popup);
        ArrayList<String> urls= new ArrayList<>();

        ImageView devOneGh = findViewById(R.id.devOneGh);
        devOneGh.setOnClickListener(view -> {
            vibrateDevice();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://github.com/the-rebooted-coder"));
            startActivity(intent);
        });
        ImageView devOneIn = findViewById(R.id.devOneLinkedin);
        devOneIn.setOnClickListener(view -> {
            vibrateDevice();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://www.linkedin.com/in/spandn/"));
            startActivity(intent);
        });
        ImageView devOneWeb = findViewById(R.id.devOneWeb);
        new Thread(() -> {
            try {
                // Create a URL for the desired page
                URL url = new URL("https://raw.githubusercontent.com/the-rebooted-coder/Spandan-Saxena-Portfolio/master/porfolioLink.txt");
                //First open the connection
                HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(60000); // timing out in a minute
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String str;
                while ((str = in.readLine()) != null) {
                    urls.add(str);
                }
                in.close();
            } catch (Exception e) {
                Log.d("MyTag",e.toString());
            }
            try {
                this.runOnUiThread(() -> {
                    if (!urls.isEmpty()) {
                    }
                });
            }
            catch (NullPointerException e){
                //Very Important
                //DO NOTE REMOVE THIS
            }
        }).start();
        devOneWeb.setOnClickListener(view -> {
            vibrateDevice();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse(urls.toString().replaceAll("\\[", "").replaceAll("]","")));
            startActivity(intent);
        });
        ImageView devOneMail = findViewById(R.id.devOneMail);
        devOneMail.setOnClickListener(view -> {
            vibrateDevice();
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"connectwithspandan@gmail.com"});
            email.putExtra(Intent.EXTRA_SUBJECT, "TakeNotes Connection");
            email.putExtra(Intent.EXTRA_TEXT, "Type your mail here");
            email.setType("message/rfc822");
            startActivity(Intent.createChooser(email, "Choose an Email client :"));
        });
    }
    private void vibrateDevice() {
        Vibrator v3 = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v3.vibrate(VibrationEffect.createOneShot(28, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v3.vibrate(25);
        }
    }
}