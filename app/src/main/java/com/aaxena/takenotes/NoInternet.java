package com.aaxena.takenotes;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NoInternet extends AppCompatActivity {
    private WifiManager wifiManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        wifiManager = (WifiManager)this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        Switch btn = findViewById(R.id.switcher);
        btn.setChecked(wifiManager.isWifiEnabled());
        btn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Intent panelIntent = new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
                startActivityForResult(panelIntent, 545);
            }

           else {
                wifiManager.setWifiEnabled(isChecked);
                int vibrate_like_actual_switch = 100;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(30);
                    }
                }, vibrate_like_actual_switch);
               vibrateDevice();
           }
        });

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
}