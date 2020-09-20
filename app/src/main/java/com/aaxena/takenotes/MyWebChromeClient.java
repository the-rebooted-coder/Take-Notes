package com.aaxena.takenotes;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

class MyWebChromeClient extends WebChromeClient {
    public ValueCallback<Uri[]> uploadMessage;
    private ValueCallback<Uri> mUploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;

    // For Lollipop 5.0+ Devices
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        if (uploadMessage != null) {
            uploadMessage.onReceiveValue(null);
            uploadMessage = null;
        }

        uploadMessage = filePathCallback;

        try {
            Intent intent = fileChooserParams.createIntent();
            startActivityForResult(intent, REQUEST_SELECT_FILE);
        } catch (ActivityNotFoundException e) {
            uploadMessage = null;
            return false;
        }
        return true;
    }

    protected void openFileChooser(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
       startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
    }
}
