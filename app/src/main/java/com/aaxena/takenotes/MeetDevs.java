package com.aaxena.takenotes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MeetDevs extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet_devs);

        //Load WebView
        loadWeb();
    }

    private void loadWeb() {
        WebView webview = findViewById(R.id.devs_plugin);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setDatabaseEnabled(true);
        webview.setWebViewClient(new WebViewClient());
        registerForContextMenu(webview);
        webview.getSettings().setUseWideViewPort(true);
        webview.setInitialScale((int) 1.0);
        webview.loadUrl("https://the-rebooted-coder.github.io/Take-Notes/devs.html");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(MeetDevs.this,Settings.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
