package com.aaxena.takenotes;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Switch;

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
           else { wifiManager.setWifiEnabled(isChecked);}
        });

    }
}