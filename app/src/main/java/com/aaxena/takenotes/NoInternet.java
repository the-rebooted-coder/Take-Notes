package com.aaxena.takenotes;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.ToggleButton;

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
        btn.setOnCheckedChangeListener((buttonView, isChecked) -> wifiManager.setWifiEnabled(isChecked));

    }
}