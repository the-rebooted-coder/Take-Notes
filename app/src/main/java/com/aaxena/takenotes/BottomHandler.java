package com.aaxena.takenotes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;
import dev.shreyaspatil.MaterialDialog.interfaces.OnDismissListener;
import nl.joery.animatedbottombar.AnimatedBottomBar;

public class BottomHandler extends AppCompatActivity {
    AnimatedBottomBar animatedBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_handler);
        SharedPreferences opening = getSharedPreferences("OPENING_TIME", 0);
        int opening_time = opening.getInt("OPENING_TIME", 0);
        opening_time++;
        SharedPreferences.Editor editor = opening.edit();
        editor.putInt("OPENING_TIME",opening_time);
        editor.apply();
        if(opening_time == 5){
            //User Opened TakeNotes 5 Times, Easter Can be Show
            BottomSheetMaterialDialog mDialog = new BottomSheetMaterialDialog.Builder(this)
                    .setTitle("You like Games Right?")
                    .setMessage("There's one hidden somewhere in TakeNotes ;)")
                    .setAnimation("easteregg.json")
                    .setCancelable(true)
                    .build();
            mDialog.show();
            mDialog.setOnDismissListener(dialogInterface -> Toast.makeText(getApplicationContext(),"Hint: Somewhere inside the 'Cockpit' buttons",Toast.LENGTH_LONG).show());
        }
        else if(opening_time == 10){
            //User Opened TakeNotes 10 Times, Review Can be Asked
            BottomSheetMaterialDialog mDialog = new BottomSheetMaterialDialog.Builder(this)
                    .setTitle("We Hope You Like TakeNotes")
                    .setMessage("Show your joy by reviewing or rating it on the Play Store.")
                    .setAnimation("review.json")
                    .setPositiveButton("Rate", R.drawable.star, new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            vibrateDevice();
                            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                dialogInterface.dismiss();
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                dialogInterface.dismiss();
                            }
                        }
                    })
                    .setNegativeButton("Nope", (dialogInterface, which) -> {
                        dialogInterface.dismiss();
                        vibrateDevice();
                        SharedPreferences.Editor editor1 = opening.edit();
                        editor1.putInt("OPENING_TIME",6);
                        editor1.apply();
                    })
                    .setCancelable(false)
                    .build();
            mDialog.show();
        }
        animatedBottomBar = findViewById(R.id.bottomNavigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new TakeNotes()).commit();
        animatedBottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int lastIndex, @Nullable AnimatedBottomBar.Tab lastTab, int newIndex, @NotNull AnimatedBottomBar.Tab newTab) {
                Fragment fragment = null;
                switch (newTab.getId()) {
                    case R.id.takenotes:
                        vibrateDevice();
                        fragment = new TakeNotes();
                        break;
                    case R.id.ocr:
                        vibrateDevice();
                        fragment = new OCR_Bottom();
                        break;
                    case R.id.history:
                        vibrateDevice();
                        fragment = new History();
                        break;
                    case R.id.more:
                        vibrateDevice();
                        fragment = new More();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
            }

            @Override
            public void onTabReselected(int i, @NotNull AnimatedBottomBar.Tab tab) {
                //DO NOTHING
            }
        });
    }
    private void vibrateDevice() {
        Vibrator v3 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v3.vibrate(VibrationEffect.createOneShot(28, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v3.vibrate(20);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
