package com.aaxena.takenotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class feature extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature);

        //Load WebView
        loadWeb();
    }

    private void loadWeb() {
        WebView webview = findViewById(R.id.request_plugin);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setDatabaseEnabled(true);
        webview.setWebViewClient(new WebViewClient());
        registerForContextMenu(webview);
        webview.getSettings().setUseWideViewPort(true);
        webview.setInitialScale((int) 1.0);
        webview.loadUrl("https://key-a-byte.typeform.com/to/gvGml3FW/");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(feature.this,Settings.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}

