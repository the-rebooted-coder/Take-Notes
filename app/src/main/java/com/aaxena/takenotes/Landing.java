package com.aaxena.takenotes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.os.Vibrator;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.review.testing.FakeReviewManager;
import com.google.android.play.core.tasks.Task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;


public class Landing extends AppCompatActivity {
    private WebView webview;
    private final static int FCR = 1;
    public static final int REQUEST_SELECT_FILE = 100;
    private ValueCallback<Uri> mUM;
    public ValueCallback<Uri[]> mUMA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        //Checking Network
        checkNetwork();

        //Runtime External storage permission for saving download files
        checkPerms();

        //Asking for Rating
        askRatings();
    }

    void askRatings() {
         ReviewManager manager = new FakeReviewManager(this);
         Task<ReviewInfo> request = manager.requestReviewFlow();
         request.addOnCompleteListener(task -> {
             if (task.isSuccessful()) {
                 // We can get the ReviewInfo object
                 ReviewInfo reviewInfo = task.getResult();
                 Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
                 flow.addOnCompleteListener(task2 -> {
                     // The flow has finished. The API does not indicate whether the user
                     // reviewed or not, or even whether the review dialog was shown. Thus, no
                     // matter the result, we continue our app flow.
                 });
             } else {
                 // There was some problem, continue regardless of the result.
             }
         });
     }

    private void checkNetwork() {
        if(haveNetwork()){
            //Setting Web View Couch for User
            couchSit();
        } else if(!haveNetwork())
        {
            Intent intent = new Intent(Landing.this, NoInternet.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mUMA == null)
            return;

        mUMA.onReceiveValue(new Uri[]{});
        mUMA = null;
    }
    //Network Checking Boolean
    private boolean haveNetwork() {
        boolean have_WIFI = false;
        boolean have_MobileData = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo info : networkInfos) {
            if (info.getTypeName().equalsIgnoreCase("WIFI"))
                if (info.isConnected())
                    have_WIFI = true;
            if (info.getTypeName().equalsIgnoreCase("MOBILE"))
                if (info.isConnected())
                    have_MobileData = true;
        }
        return have_MobileData||have_WIFI;
    }
    @SuppressLint("SetJavaScriptEnabled")
    private void couchSit() {
        webview = findViewById(R.id.takenotes_plugin);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setDatabaseEnabled(true);
        webview.setWebViewClient(new WebViewClient());
        registerForContextMenu(webview);
        webview.getSettings().setUseWideViewPort(true);
        webview.setInitialScale((int) 1.0);
        webview.loadUrl("https://the-rebooted-coder.github.io/Take-Notes/");
        webview.scrollTo(0, 200);

        webview.setWebChromeClient(new WebChromeClient() {
            //For Android 5.0+
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    WebChromeClient.FileChooserParams fileChooserParams) {

                if (mUMA != null) {
                    mUMA.onReceiveValue(null);
                    mUMA = null;
                }
                mUMA = filePathCallback;

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("*/*");
                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                try {
                    startActivityForResult(chooserIntent, REQUEST_SELECT_FILE);
                    Toast.makeText(Landing.this,"Pick a .ttf file",Toast.LENGTH_LONG).show();
                } catch (ActivityNotFoundException e) {
                    mUMA = null;
                    Toast.makeText(Landing.this, "Cannot Open File Picker", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }
        });

        //handle downloading
        webview.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(30);
            if(Build.VERSION.SDK_INT>=24){
                try{
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);
                    if (url.startsWith("data:")) {  //when url is base64 encoded data
                        String path = createAndSaveFileFromBase64Url(url);
                        return;
                    }

                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    dm.enqueue(request);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void checkPerms() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to WRITE_EXTERNAL_STORAGE - requesting it");
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, 1);
            }
        }
    }

    public String createAndSaveFileFromBase64Url(String url) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS+"/Take Notes");
        String filetype = url.substring(url.indexOf("/") + 1, url.indexOf(";"));
        String filename = "Take Notes "+System.currentTimeMillis() + "." + filetype;
        Toast.makeText(this, R.string.success_toast,Toast.LENGTH_LONG).show();
        File file = new File(path, filename);
        try {
            if(!path.exists())
                path.mkdirs();
            if(!file.exists())
                file.createNewFile();

            String base64EncodedString = url.substring(url.indexOf(",") + 1);
            byte[] decodedBytes = Base64.decode(base64EncodedString, Base64.DEFAULT);
            OutputStream os = new FileOutputStream(file);
            os.write(decodedBytes);
            os.close();

            //Tell the media scanner about the new file so that it is immediately available to the user.
            MediaScannerConnection.scanFile(this,
                    new String[]{file.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });

            //Set notification after download complete and add "click to view" action to that

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                final int notificationId = 1;
                String CHANNEL_ID = "SavedReminderService";
                final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                String mimetype = url.substring(url.indexOf(":") + 1, url.indexOf("/"));
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), (mimetype + "/*"));
                PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
                NotificationChannel notificationChannel= new NotificationChannel(CHANNEL_ID,"Notes Saved Notification", NotificationManager.IMPORTANCE_HIGH);
                Notification notification = new Notification.Builder(this,CHANNEL_ID)
                        .setContentText("Tap to Check Now!")
                        .setContentTitle("Your generated note got saved")
                        .setContentIntent(pIntent)
                        .setColor(getResources().getColor(R.color.notification))
                        .setChannelId(CHANNEL_ID)
                        .setSmallIcon(R.drawable.logo_take_notes)
                        .setAutoCancel(true)
                        .build();
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(notificationChannel);
                    notificationManager.notify(notificationId, notification);
                }

            }
            else {
                String mimetype = url.substring(url.indexOf(":") + 1, url.indexOf("/"));
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), (mimetype + "/*"));
                PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
                Notification notification = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo_take_notes)
                        .setColor(getResources().getColor(R.color.notification))
                        .setContentText("Tap to Check Now!")
                        .setContentTitle("Your generated note got saved")
                        .setContentIntent(pIntent)
                        .build();

                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                int notificationId = 85851;
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(notificationId, notification);
            }
        } catch (IOException e) {
            Log.w("ExternalStorage", "Error writing " + file, e);
            Toast.makeText(getApplicationContext(), R.string.error_downloading, Toast.LENGTH_LONG).show();
        }

        return file.toString();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
               if (mUMA == null)
                   return;
                mUMA.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                mUMA = null;
            }
        } else if (requestCode == FCR) {
            if (null == mUM)
               return;
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUM.onReceiveValue(result);
            mUM = null;
        } else
            Toast.makeText(this, R.string.failed_to_load_fnt, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (webview.canGoBack()) {
                    webview.goBack();
                } else {
                    finish();
                }
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}