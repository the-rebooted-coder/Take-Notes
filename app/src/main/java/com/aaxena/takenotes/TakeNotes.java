package com.aaxena.takenotes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.ExceptionConverter;
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

import static android.app.Activity.RESULT_OK;
import static android.content.Context.ACTIVITY_SERVICE;
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
    private String hello;
    SharedPreferences hasSignedIn = null;

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.activity_take_notes, container, false);

        hasSignedIn = getActivity().getSharedPreferences("hasSignedIn", 0);
        Boolean hasSigned = hasSignedIn.getBoolean("hasSignedIn", false);

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
                webview.setWebViewClient(new WebViewClient());
                registerForContextMenu(webview);
                webview.getSettings().setUseWideViewPort(true);
                webview.setOnLongClickListener(v1 -> true);
                webview.setLongClickable(false);
                webview.setInitialScale((int) 1.0);
                webview.addJavascriptInterface(new WebAppInterface(getActivity()),"Android");
                //TODO Change it
                webview.loadUrl("https://shrish-sharma-codes.github.io/tn-native-v4");
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
                            Toast.makeText(getContext(),"Pick a suitable file",Toast.LENGTH_LONG).show();
                        } catch (ActivityNotFoundException e) {
                            mUMA = null;
                            Toast.makeText(getContext(), "Cannot Open File Picker", Toast.LENGTH_LONG).show();
                            return false;
                        }
                        return true;
                    }
                });
                webview.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        if (url.matches(getString(R.string.take_notes_image_to_be_displayed))) {
                            vibrateDevice();
                            Intent i=new Intent(getContext(),More.class);
                            startActivity(i);
                            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            getActivity().finish();
                        }
                        else if (url.matches(getString(R.string.print))) {
                            vibrateDevice();
                            try {
                                File folderPath = new File(Environment.getExternalStorageDirectory() + "/Documents/TakeNotes");
                                File[] imageList = folderPath.listFiles();
                                ArrayList<File> imagesArrayList = new ArrayList<>();
                                for (File absolutePath : imageList) {
                                    imagesArrayList.add(absolutePath);
                                }
                                new CreatePdfTask(getContext(), imagesArrayList).execute();
                            } catch (Exception e) {
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("No Images Found")
                                        .setMessage(R.string.no_img)
                                        .setCancelable(false)
                                        // A null listener allows the button to dismiss the dialog and take no further action.
                                        .setPositiveButton("I Know", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i=new Intent(getActivity(),BottomHandler.class);
                                                startActivity(i);
                                                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                getActivity().finish();
                                            }
                                        })
                                        .create().show();
                            }
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
                                if(hasSigned){
                                    //when url is base64 encoded data
                                    vibrateDevice();
                                    String path = createAndSaveFileFromBase64Url(url);
                                }
                                else {
                                    Intent toSignUp = new Intent(getActivity(),SignUp.class);
                                    startActivity(toSignUp);
                                    vibrateDevice();
                                    Toast.makeText(getContext(),"Sign In To Fully Experience TakeNotes",Toast.LENGTH_SHORT).show();
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
    private InstallStateUpdatedListener installStateUpdatedListener = new
            InstallStateUpdatedListener() {
                @Override
                public void onStateUpdate(InstallState state) {
                    if (state.installStatus() == InstallStatus.INSTALLED){
                        if (mAppUpdateManager != null){
                            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
                        }

                    } else {
                        //App Is Fully Updated Nothing To Do, Continuing Normal WorkFlow but do not erase the else func
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
    public class CreatePdfTask extends AsyncTask<String, Integer, File> {
        Context context;
        ArrayList<File> files;
        ProgressDialog progressDialog;
        CreatePdfTask(Context context2, ArrayList<File> arrayList) {
            context = context2;
            files = arrayList;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Processing...");
            progressDialog.setMessage(getString(R.string.advice));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "I Know!", (dialog, which) -> ((ActivityManager)context.getSystemService(ACTIVITY_SERVICE))
                    .clearApplicationUserData());
            progressDialog.show();
        }
        @Override
        protected File doInBackground(String... strings) {
            File outputMediaFile = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOCUMENTS + "/" + "Take Notes" + System.currentTimeMillis() + ".pdf");
            Document document = new Document(PageSize.A4, 38.0f, 38.0f, 50.0f, 38.0f);
            try {
                PdfWriter.getInstance(document, new FileOutputStream(outputMediaFile));
            } catch (DocumentException | ExceptionConverter e) {
                e.printStackTrace();
                progressDialog.dismiss();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                progressDialog.dismiss();
                return null;
            }
            document.open();
            try {
                document.add(new Chunk(""));
            } catch (DocumentException e) {
                e.printStackTrace();
            }

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
            progressDialog.dismiss();
        }
        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            Intent intent = new Intent(getContext(),PdfProcessed.class);
            startActivity(intent);
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            progressDialog.dismiss();
            getActivity().finish();
        }
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