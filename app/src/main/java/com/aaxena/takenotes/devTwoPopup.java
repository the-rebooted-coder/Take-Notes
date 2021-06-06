package com.aaxena.takenotes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class devTwoPopup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dev_two_popup);

        ImageView devOneGh = findViewById(R.id.devTwoGh);
        devOneGh.setOnClickListener(view -> {
            vibrateDevice();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://github.com/shrish-sharma-git"));
            startActivity(intent);
        });
        ImageView devOneIn = findViewById(R.id.devTwoLinkedin);
        devOneIn.setOnClickListener(view -> {
            vibrateDevice();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://www.linkedin.com/in/shrish-sharma/"));
            startActivity(intent);
        });
        ImageView devOneWeb = findViewById(R.id.devTwoWeb);
        devOneWeb.setOnClickListener(view -> {
            vibrateDevice();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://shrish-sharma-git.github.io/My-Portfolio"));
            startActivity(intent);
        });
        ImageView devOneMail = findViewById(R.id.devTwoMail);
        devOneMail.setOnClickListener(view -> {
            vibrateDevice();
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{"connectwithshrish@gmail.com"});
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