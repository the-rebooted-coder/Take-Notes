package com.aaxena.takenotes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.aaxena.takenotes.MyName.SHARED_PREFS;
import static com.aaxena.takenotes.MyName.TEXT;

public class TakeNotes extends Fragment {
    private WebView webview;
    private final static int FCR = 1;
    public static final int REQUEST_SELECT_FILE = 100;
    private ValueCallback<Uri> mUM;
    public ValueCallback<Uri[]> mUMA;
    private AppUpdateManager mAppUpdateManager;
    private static final int RC_APP_UPDATE = 11;
    private String Namaste;
    SharedPreferences hasSignedIn = null;

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.activity_take_notes, container, false);

        hasSignedIn = getActivity().getSharedPreferences("hasSignedIn", 0);
        boolean hasSigned = hasSignedIn.getBoolean("hasSignedIn", false);
        ImageView paper = v.findViewById(R.id.pagePaper);
        //Load Data
        loadData();
        //Checking Network
        if(haveNetwork()) {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation != Configuration.ORIENTATION_LANDSCAPE) {
                //Setting Web View Couch for User
                webview = v.findViewById(R.id.takenotes_plugin);
                webview.getSettings().setJavaScriptEnabled(true);
                webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
                webview.getSettings().setDomStorageEnabled(true);
                webview.getSettings().setDatabaseEnabled(true);
                webview.setWebViewClient(new WebViewClient(){
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        paper.setVisibility(View.GONE);
                        webview.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        paper.setVisibility(View.VISIBLE);
                        webview.setVisibility(View.INVISIBLE);
                    }
                });
                registerForContextMenu(webview);
                webview.getSettings().setUseWideViewPort(true);
                webview.setInitialScale((int) 1.0);
                webview.addJavascriptInterface(new WebAppInterface(getActivity()),"Android");
                webview.addJavascriptInterface(new WebAppInterfaceDraft(getActivity()),"Draft");
                webview.loadUrl("https://the-rebooted-coder.github.io/Take-Notes/");
                webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                webview.setWebChromeClient(new WebChromeClient() {
                    //File Chooser
                    public boolean onShowFileChooser(
                            WebView webView, ValueCallback<Uri[]> filePathCallback,
                            FileChooserParams fileChooserParams) {

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
                            Toast.makeText(getContext(),"Pick a .ttf font file",Toast.LENGTH_LONG).show();
                        } catch (ActivityNotFoundException e) {
                            mUMA = null;
                            Toast.makeText(getContext(), "Cannot Open File Picker", Toast.LENGTH_LONG).show();
                            return false;
                        }
                        return true;
                    }
                });
                        //Handles Downloading
                webview.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
                    if(Build.VERSION.SDK_INT>=24){
                        try{
                            Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                            m.invoke(null);
                            if (url.startsWith("data:")) {
                                if(hasSigned){
                                    //when url is base64 encoded data
                                    vibrateDevice();
                                    SharedPreferences usedTakeNotes = getActivity().getSharedPreferences("TimeUsedTakeNotes", 0);
                                    int takeNotesOpening = usedTakeNotes.getInt("TimeUsedTakeNotes", 0);
                                    takeNotesOpening++;
                                    SharedPreferences.Editor editor = usedTakeNotes.edit();
                                    editor.putInt("TimeUsedTakeNotes",takeNotesOpening);
                                    editor.apply();
                                    if(takeNotesOpening == 10){
                                        BottomSheetMaterialDialog mDialog = new BottomSheetMaterialDialog.Builder(getActivity())
                                                .setTitle("Wohooooooo!")
                                                .setMessage("You just used TakeNotes for 10 times, way to go!")
                                                .setAnimation("confetti.json")
                                                .setPositiveButton("Share and Feel Proud",R.drawable.share_now, new MaterialDialog.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int which) {
                                                        vibrateDeviceParty();
                                                        Intent intent = new Intent(Intent.ACTION_SEND);
                                                        String shareBody = "Just Scored a 10 in TakeNotes. How Many Have You Created?\nCreate Awesome Handwritten Notes now!\n\nDownload here: https://play.google.com/store/apps/details?id=com.aaxena.takenotes";
                                                        intent.setType("text/plain");
                                                        intent.putExtra(Intent.EXTRA_SUBJECT,"I Created 10 Notes using TakeNotes");
                                                        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                                                        startActivity(Intent.createChooser(intent, "Choose app to share achievement"));
                                                    }
                                                })
                                                .setCancelable(true)
                                                .build();
                                        mDialog.show();
                                    }
                                    else if(takeNotesOpening == 20){
                                        BottomSheetMaterialDialog mDialog = new BottomSheetMaterialDialog.Builder(getActivity())
                                                .setTitle("Nice Going!")
                                                .setMessage("You just used TakeNotes for 20 times, great going!")
                                                .setAnimation("confetti.json")
                                                .setPositiveButton("Share and Feel Proud",R.drawable.share_now, new MaterialDialog.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int which) {
                                                        vibrateDeviceParty();
                                                        Intent intent = new Intent(Intent.ACTION_SEND);
                                                        String shareBody = "Just Scored a 20 in TakeNotes. How Many Have You Created?\nCreate Awesome Handwritten Notes now!\n\nDownload here: https://play.google.com/store/apps/details?id=com.aaxena.takenotes";
                                                        intent.setType("text/plain");
                                                        intent.putExtra(Intent.EXTRA_SUBJECT,"I Created 20 Notes using TakeNotes");
                                                        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                                                        startActivity(Intent.createChooser(intent, "Choose app to share achievement"));
                                                    }
                                                })
                                                .setCancelable(true)
                                                .build();
                                        mDialog.show();
                                    }
                                    else if(takeNotesOpening == 50){
                                        BottomSheetMaterialDialog mDialog = new BottomSheetMaterialDialog.Builder(getActivity())
                                                .setTitle("You're Unstoppable")
                                                .setMessage("You just used TakeNotes for the 50th, great creation!")
                                                .setAnimation("confetti.json")
                                                .setPositiveButton("Share and Feel Proud",R.drawable.share_now, new MaterialDialog.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int which) {
                                                        vibrateDeviceParty();
                                                        Intent intent = new Intent(Intent.ACTION_SEND);
                                                        String shareBody = "Just Scored a 50 in TakeNotes. How Many Have You Created?\nCreate Awesome Handwritten Notes now!\n\nDownload here: https://play.google.com/store/apps/details?id=com.aaxena.takenotes";
                                                        intent.setType("text/plain");
                                                        intent.putExtra(Intent.EXTRA_SUBJECT,"I Created 50 Notes using TakeNotes");
                                                        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                                                        startActivity(Intent.createChooser(intent, "Choose app to share achievement")); }
                                                })
                                                .setCancelable(true)
                                                .build();
                                        mDialog.show();
                                    }
                                    else if(takeNotesOpening == 100){
                                        BottomSheetMaterialDialog mDialog = new BottomSheetMaterialDialog.Builder(getActivity())
                                                .setTitle("Now That's a Milestone")
                                                .setMessage("Congratulations!!")
                                                .setMessage("100th Note yeah, you just crossed one ultimate milestone.")
                                                .setAnimation("confetti.json")
                                                .setPositiveButton("Share and Feel Proud",R.drawable.share_now, new MaterialDialog.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int which) {
                                                        vibrateDeviceParty();
                                                        Intent intent = new Intent(Intent.ACTION_SEND);
                                                        String shareBody = "Just Scored a 100 in TakeNotes. How Many Have You Created?\nCreate Awesome Handwritten Notes now!\n\nDownload here: https://play.google.com/store/apps/details?id=com.aaxena.takenotes";
                                                        intent.setType("text/plain");
                                                        intent.putExtra(Intent.EXTRA_SUBJECT,"I Created 100 Notes using TakeNotes");
                                                        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                                                        startActivity(Intent.createChooser(intent, "Choose app to share achievement")); }
                                                })
                                                .setCancelable(true)
                                                .build();
                                        mDialog.show();
                                    }
                                    String path = createAndSaveFileFromBase64Url(url);
                                }
                                else {
                                    Intent toSignUp = new Intent(getActivity(),SignUp.class);
                                    startActivity(toSignUp);
                                    vibrateDevice();
                                    Toast.makeText(getContext(),"Sign In To Fully Dedicated TakeNotes",Toast.LENGTH_SHORT).show();
                                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    getActivity().finish();
                                }
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        } else if(!haveNetwork())
        {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            getActivity().finish();
        }
        //Runtime External storage permission for saving download files
        checkPerms();

        return v;
    }
    private void vibrateDeviceParty() {
        Vibrator v3 = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0,25,30,35,40,45,100};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v3.vibrate(VibrationEffect.createWaveform(pattern,-1));
        } else {
            v3.vibrate(pattern,-1);
        }
    }
    private InstallStateUpdatedListener installStateUpdatedListener = new
            InstallStateUpdatedListener() {
                @Override
                public void onStateUpdate(InstallState state) {
                    if (state.installStatus() == InstallStatus.INSTALLED){
                        if (mAppUpdateManager != null){
                            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
                        }
                    }
                }
            };
    private void loadData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        hello = sharedPreferences.getString(TEXT, "");
    }
    @Override
    public void onStart() {
        super.onStart();
        mAppUpdateManager = AppUpdateManagerFactory.create(getContext());
        mAppUpdateManager.registerListener(installStateUpdatedListener);
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE /*AppUpdateType.IMMEDIATE*/)){

                try {
                    mAppUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo, AppUpdateType.IMMEDIATE /*AppUpdateType.IMMEDIATE*/,getActivity(), RC_APP_UPDATE);

                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
            else {
            }
        });
    }
    @SuppressLint("SetJavaScriptEnabled")
    private String createAndSaveFileFromBase64Url(String url) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS+"/TakeNotes");
        String filetype = url.substring(url.indexOf("/") + 1, url.indexOf(";"));
        if (hello.isEmpty()){
            Toast.makeText(getContext(),"Provide a Default File Name",Toast.LENGTH_SHORT).show();
            Intent i=new Intent(getActivity(),MyName.class);
            startActivity(i);
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        String filename =hello+" Notes"+System.currentTimeMillis() + "." + filetype;
        Toast.makeText(getContext(), R.string.success_toast,Toast.LENGTH_SHORT).show();
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
            MediaScannerConnection.scanFile(getContext(),
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
                final NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                String mimetype = url.substring(url.indexOf(":") + 1, url.indexOf("/"));
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), (mimetype + "/*"));
                PendingIntent pIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);
                NotificationChannel notificationChannel= new NotificationChannel(CHANNEL_ID,"Notes Saved Notification", NotificationManager.IMPORTANCE_HIGH);
                Notification notification = new Notification.Builder(getContext(),CHANNEL_ID)
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
                PendingIntent pIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);
                Notification notification = new NotificationCompat.Builder(getContext())
                        .setSmallIcon(R.drawable.logo_take_notes)
                        .setColor(getResources().getColor(R.color.notification))
                        .setContentText("Tap to Check Now!")
                        .setContentTitle("Your generated note got saved")
                        .setContentIntent(pIntent)
                        .build();

                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                int notificationId = 85851;
                NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(notificationId, notification);
            }
        } catch (IOException e) {
            Log.w("ExternalStorage", "Error writing " + file, e);
            Toast.makeText(getContext(), R.string.error_downloading, Toast.LENGTH_LONG).show();
        }

        return file.toString();
    }
    @Override
    public void onResume() {
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

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
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
    private void checkPerms() {
        if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(getContext(),"Storage Permissions Denied",Toast.LENGTH_LONG).show();
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissions, 1);
        }
    }
    @Override
    public void onStop() {
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
            Toast.makeText(getContext(), R.string.failed_to_load_fnt, Toast.LENGTH_LONG).show();
    }
    private void vibrateDevice() {
        Vibrator v3 = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v3.vibrate(VibrationEffect.createOneShot(28, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v3.vibrate(25);
        }
    }
}
