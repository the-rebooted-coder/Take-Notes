package com.aaxena.takenotes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
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
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static android.graphics.Color.BLACK;


public class Landing extends AppCompatActivity {
    private WebView webview;
    private final static int FCR = 1;
    public static final int REQUEST_SELECT_FILE = 100;
    private ValueCallback<Uri> mUM;
    public ValueCallback<Uri[]> mUMA;
    private AppUpdateManager mAppUpdateManager;
    private static final int RC_APP_UPDATE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        //Checking Network
        checkNetwork();

        //Runtime External storage permission for saving download files
        checkPerms();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAppUpdateManager = AppUpdateManagerFactory.create(this);

        mAppUpdateManager.registerListener(installStateUpdatedListener);

        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE /*AppUpdateType.IMMEDIATE*/)){

                try {
                    mAppUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo, AppUpdateType.IMMEDIATE /*AppUpdateType.IMMEDIATE*/, Landing.this, RC_APP_UPDATE);

                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }

            }
            else {
            }
        });
    }

    InstallStateUpdatedListener installStateUpdatedListener = new
            InstallStateUpdatedListener() {
                @Override
                public void onStateUpdate(InstallState state) {
                     if (state.installStatus() == InstallStatus.INSTALLED){
                        if (mAppUpdateManager != null){
                            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
                        }

                    } else {
                      //App Is Fully Updated Nothing To Do Continuing Normal WorkFlow but do not erase the else func
                    }
                }
            };

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
        webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setDatabaseEnabled(true);
        webview.setWebViewClient(new WebViewClient());
        registerForContextMenu(webview);
        webview.getSettings().setUseWideViewPort(true);
        webview.setInitialScale((int) 1.0);
        webview.loadUrl("https://shrish-sharma-codes.github.io/tn-testing-V2/");
        webview.scrollTo(0, 200);
        webview.setWebChromeClient(new WebChromeClient() {
           //File Chooser
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
                    Toast.makeText(Landing.this,"Pick a suitable file",Toast.LENGTH_LONG).show();
                } catch (ActivityNotFoundException e) {
                    mUMA = null;
                    Toast.makeText(Landing.this, "Cannot Open File Picker", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }
        });
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.matches(getString(R.string.take_notes_image_to_be_displayed))) {
                    Vibrator v2 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v2.vibrate(28);
                    Intent i=new Intent(Landing.this,UserInfo.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
                else if (url.matches(getString(R.string.print))) {
                    Vibrator v2 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v2.vibrate(25);
                    try {
                        File folderPath = new File(Environment.getExternalStorageDirectory() + "/Documents/TakeNotes");
                        File[] imageList = folderPath.listFiles();
                        ArrayList<File> imagesArrayList = new ArrayList<>();
                        for (File absolutePath : imageList) {
                            imagesArrayList.add(absolutePath);
                        }
                        new CreatePdfTask(Landing.this, imagesArrayList).execute();
                    } catch (Exception e) {
                       Toast.makeText(Landing.this,"No Images Under Take Notes Folder",Toast.LENGTH_LONG).show();
                    }
                }
                else if (url.matches(getString(R.string.developer_page))) {
                    Vibrator v2 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v2.vibrate(25);
                   Toast.makeText(Landing.this,"Tip: Tap on our PFP's to reveal more!",Toast.LENGTH_LONG).show();
                }
                else if (url.matches(getString(R.string.custom_font))){
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    builder.setToolbarColor(BLACK);
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(Landing.this, Uri.parse(url));
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        //Handles Downloading
        webview.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
            if(Build.VERSION.SDK_INT>=24){
                try{
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);
                    if (url.startsWith("data:")) {
                        //when url is base64 encoded data
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(30);
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

    public void displayExceptionMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void checkPerms() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to WRITE_EXTERNAL_STORAGE - requesting it");
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, 1);
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                if (account !=null) {
                    String personName = account.getDisplayName();
                    Toast.makeText(Landing.this, "Howdy " + personName + " you are in!", Toast.LENGTH_LONG).show();
                    Toast.makeText(Landing.this, "Welcome to Take Notes", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(Landing.this, R.string.not_yet_in,Toast.LENGTH_LONG).show();
                    Vibrator v2 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v2.vibrate(30);
                    Intent taking_out = new Intent(Landing.this, SignUp.class);
                    startActivity(taking_out);
                }
            }
        }
    }



    public String createAndSaveFileFromBase64Url(String url) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS+"/TakeNotes");
        String filetype = url.substring(url.indexOf("/") + 1, url.indexOf(";"));
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        String personName = account.getDisplayName();
        String filename = personName+"'s notes "+System.currentTimeMillis() + "." + filetype;
        Toast.makeText(this, R.string.success_toast,Toast.LENGTH_SHORT).show();
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
    protected void onStop() {
        super.onStop();
        if (mAppUpdateManager != null) {
            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
        }
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

    public class CreatePdfTask extends AsyncTask<String, Integer, File> {
        Context context;
        ArrayList<File> files;
        ProgressDialog progressDialog;

        public CreatePdfTask(Context context2, ArrayList<File> arrayList) {
            context = context2;
            files = arrayList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Hmmmm...");
            progressDialog.setMessage(getString(R.string.advice));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "I Know!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((ActivityManager)context.getSystemService(ACTIVITY_SERVICE))
                            .clearApplicationUserData();
                }
            });
            progressDialog.show();
        }

        @Override
        protected File doInBackground(String... strings) {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
            String username = account.getDisplayName();
            File outputMediaFile =  new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOCUMENTS+"/"+username+ System.currentTimeMillis() + ".pdf");
            Document document = new Document(PageSize.A4, 38.0f, 38.0f, 50.0f, 38.0f);
            try {
                PdfWriter.getInstance(document, new FileOutputStream(outputMediaFile));
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
            document.open();

            int i = 0;
            while (true) {
                if (i < this.files.size()) {
                    try {
                        Image image = Image.getInstance(files.get(i).getAbsolutePath());

                        float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                                - document.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
                        image.scalePercent(scaler);
                        image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
                        image.setAbsolutePosition((document.getPageSize().getWidth() - image.getScaledWidth()) / 2.0f,
                                (document.getPageSize().getHeight() - image.getScaledHeight()) / 2.0f);

                        document.add(image);
                        document.newPage();
                        publishProgress(i);
                        i++;
                    } catch (BadElementException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                } else {
                    document.close();
                    return outputMediaFile;
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            StringBuilder sb = new StringBuilder();
            sb.append("There seems an error!");
            progressDialog.setTitle(sb.toString());
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            progressDialog.dismiss();
            Toast.makeText(context, "PDF Saved at: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        }
    }

}