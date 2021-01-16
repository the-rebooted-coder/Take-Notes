 package com.aaxena.takenotes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Button;

 public class MyName extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_name);

        Button myName = findViewById(R.id.more_info_on_name);
        myName.setOnClickListener(v -> {
            Vibrator v2 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v2.vibrate(25);
            new AlertDialog.Builder(this)
                    .setTitle(R.string.more_info_title)
                    .setMessage(R.string.more_info_text)
                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton("Yes!", null)
                    .show();
        });

        Button saveMyName = findViewById(R.id.save_name);
        saveMyName.setOnClickListener(v -> {
            Vibrator v2 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v2.vibrate(26);
            
        });
    }
     @Override
     public void onBackPressed() {
         super.onBackPressed();
         Intent i=new Intent(MyName.this,Settings.class);
         startActivity(i);
         overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
         finish();
     }
}
