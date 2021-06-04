package com.aaxena.takenotes;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;
import dev.shreyaspatil.MaterialDialog.interfaces.OnDismissListener;

import static android.content.Context.MODE_PRIVATE;
import static com.aaxena.takenotes.MyName.SHARED_PREFS;
import static com.aaxena.takenotes.MyName.TEXT;
import static com.facebook.FacebookSdk.getApplicationContext;

public class More extends Fragment {
    public static final String UI_MODE = "uiMode";
    AlertDialog alertDialog1;
    private TextView loggedInName,savedName;
    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v3 = inflater.inflate(R.layout.more, container, false);
        CardView dynamicHolder =  v3.findViewById(R.id.dynamicHolder);
        TextView dynamicText = v3.findViewById(R.id.dynamicText);
        SharedPreferences seenPDFCheck = getActivity().getSharedPreferences("seenPDF", 0);
        boolean seenPDF = seenPDFCheck.getBoolean("seenPDF", false);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String saving_as = sharedPreferences.getString(TEXT, "");
        new Thread(() -> {
            ArrayList<String> urls= new ArrayList<>();
            try {
                // Create a URL for the desired page
                URL url = new URL("https://the-rebooted-coder.github.io/Take-Notes/dynamicText.txt"); //My text file location
                //First open the connection
                HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(60000); // timing out in a minute
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String str;
                while ((str = in.readLine()) != null) {
                    urls.add(str);
                }
                in.close();
            } catch (Exception e) {
                Log.d("MyTag",e.toString());
            }
            try {
                getActivity().runOnUiThread(() -> {
                    if (!urls.isEmpty()) {
                        dynamicHolder.setVisibility(View.VISIBLE);
                        dynamicText.setText(urls.get(0));
                    }
                });
            }
            catch (NullPointerException e){
               //Very Important
                //DO NOTE REMOVE THIS
            }

        }).start();

        savedName = v3.findViewById(R.id.savingName);
        if (saving_as.isEmpty()){
            savedName.setText("File Name");
        }
        else {
            savedName.setText(saving_as);
        }

        loggedInName = v3.findViewById(R.id.loggedInGuy);
        if (account != null) {
            //Google
            loggedInName.setText(account.getDisplayName());
        } else {
            loggedInName.setText("Sign in");
        }

        CardView createPDF = v3.findViewById(R.id.createPDF);
        createPDF.setOnClickListener(view -> {
            vibrateDevice();
            if (seenPDF){
                try {
                    File folderPath = new File(Environment.getExternalStorageDirectory() + "/Documents/TakeNotes");
                    File[] imageList = folderPath.listFiles();
                    ArrayList<File> imagesArrayList = new ArrayList<>();
                    for (File absolutePath : imageList) {
                        imagesArrayList.add(absolutePath);
                    }
                    new CreatePdfTask(getContext(), imagesArrayList).execute();
                    Toast.makeText(getContext(),"PDF Created & Saved in Documents.",Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getContext(),"No Images In TakeNotes Directory.\nCreate Notes First!",Toast.LENGTH_LONG).show();
                }
            }
            else {
            BottomSheetMaterialDialog mDialog = new BottomSheetMaterialDialog.Builder(getActivity())
                    .setTitle("Tip")
                    .setMessage("Using this you can convert all the Images in your TakeNotes Folder into a Single PDF.")
                    .setCancelable(true)
                    .build();
            mDialog.show();
            mDialog.setOnDismissListener(dialogInterface -> {
                SharedPreferences.Editor editPDF = seenPDFCheck.edit();
                editPDF.putBoolean("seenPDF", true);
                editPDF.apply();
                try {
                    File folderPath = new File(Environment.getExternalStorageDirectory() + "/Documents/TakeNotes");
                    File[] imageList = folderPath.listFiles();
                    ArrayList<File> imagesArrayList = new ArrayList<>();
                    for (File absolutePath : imageList) {
                        imagesArrayList.add(absolutePath);
                    }
                    new CreatePdfTask(getContext(), imagesArrayList).execute();
                    Toast.makeText(getContext(),"PDF Created & Saved in Documents.",Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getContext(),"No Images In TakeNotes Directory.\nCreate Notes First!",Toast.LENGTH_LONG).show();
                }
            });
            }
        });
        CardView deleteHistory = v3.findViewById(R.id.idBtnDelete);
        LottieAnimationView deleteAnimation = v3.findViewById(R.id.lottieFileDelete);
        deleteHistory.setOnClickListener(v -> {
           vibrateDevice();
           reset();
           deleteAnimation.playAnimation();
            int deleteAnimTimeout = 2000;
            new Handler().postDelayed(() -> {
              deleteAnimation.cancelAnimation();
              Toast.makeText(getApplicationContext(),"My Notes Cleared",Toast.LENGTH_SHORT).show();
            }, deleteAnimTimeout);
        });
        Button tnWeb = v3.findViewById(R.id.tnWeb);
        tnWeb.setOnClickListener(view -> {
            vibrateDevice();
            BottomSheetMaterialDialog mDialog = new BottomSheetMaterialDialog.Builder(getActivity())
                    .setTitle("To use TakeNotes Desktop Visit")
                    .setMessage(Html.fromHtml("<a href=\"bit.ly/TakeNotesDesktop\">bit.ly/TakeNotesDesktop</a>"))
                    .setAnimation("linked.json")
                    .setCancelable(true)
                    .build();
            mDialog.show();
        });

        Button share = v3.findViewById(R.id.shareApp);
        share.setOnClickListener(v -> {
            vibrateDevice();
            /*Create an ACTION_SEND Intent*/
            Intent intent = new Intent(Intent.ACTION_SEND);
            /*This will be the actual content you wish you share.*/
            String shareBody = "Take Notes is an awesome app for writing handwritten notes, I am using it and believe it will help you too!\n\nDownload here: https://play.google.com/store/apps/details?id=com.aaxena.takenotes";
            /*The type of the content is text, obviously.*/
            intent.setType("text/plain");
            /*Applying information Subject and Body.*/
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
            intent.putExtra(Intent.EXTRA_TEXT, shareBody);
            /*Fire!*/
            startActivity(Intent.createChooser(intent, getString(R.string.share_using)));
        });

        CardView profile = v3.findViewById(R.id.profile);
        profile.setOnClickListener(v -> {
            vibrateDevice();
            Intent i=new Intent(getContext(),UserInfo.class);
            startActivity(i);
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        CardView my_name = v3.findViewById(R.id.savedName);
        my_name.setOnClickListener(v -> {
            vibrateDevice();
            Intent i=new Intent(getContext(),MyName.class);
            startActivity(i);
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        CardView devs = v3.findViewById(R.id.developers);
        devs.setOnClickListener(v -> {
            vibrateDevice();
            Toast.makeText(getContext(),"Tip: Tap on our PFP's to reveal more!",Toast.LENGTH_SHORT).show();
            String url = "https://the-rebooted-coder.github.io/Take-Notes/devs";
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setToolbarColor(Color.parseColor("#19112E"));
            CustomTabsIntent customTabsIntent = builder.build();
            builder.setShowTitle(true);
            customTabsIntent.launchUrl(getContext(), Uri.parse(url));
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        Button privacy = v3.findViewById(R.id.privacyPolicy);
        privacy.setOnClickListener(v -> {
            vibrateDevice();
            Intent i=new Intent(getContext(),PrivacyPolicy.class);
            Pair [] pairs = new Pair[1];
            pairs[0] = new Pair<View, String>(privacy,"imageTransition");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(),pairs);
            startActivity(i,options.toBundle());
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        Button theme = v3.findViewById(R.id.theme);
        theme.setOnClickListener(view -> CreateAlertDialogWithRadioButtonGroup());
        return v3;
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
        protected File doInBackground(String... strings) {
            String pdfNomenclature;
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            String saving_as = sharedPreferences.getString(TEXT, "");
            if(saving_as.isEmpty()){
                pdfNomenclature = "";
            }
            else{
                pdfNomenclature=saving_as;
            }
            File outputMediaFile = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOCUMENTS + "/" + pdfNomenclature.trim() + " TakeNotes" + System.currentTimeMillis() + ".pdf");
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
                document.add(new Chunk("Created by TakeNotes"));
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
    }
    public void CreateAlertDialogWithRadioButtonGroup() {
        int nightModeFlags =
                this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose Theme for Take Notes");
        builder.setPositiveButton("Light", (dialog, which) -> {
            switch (nightModeFlags) {
                case Configuration.UI_MODE_NIGHT_YES:
                    vibrateDevice();
                    alertDialog1.dismiss();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(UI_MODE, MODE_PRIVATE).edit();
                    editor.putString("uiMode","Light");
                    editor.apply();
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                    Toast.makeText(getApplicationContext(),"Already in Light Mode ☀️",Toast.LENGTH_SHORT).show();
                    alertDialog1.dismiss();
                    break;
                default:
                    Toast.makeText(getApplicationContext(),"Choose a theme",Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Dark", (dialog, which) -> {
            switch (nightModeFlags) {
                case Configuration.UI_MODE_NIGHT_YES:
                    Toast.makeText(getApplicationContext(),"Already in Dark Mode \uD83C\uDF19",Toast.LENGTH_SHORT).show();
                    alertDialog1.dismiss();
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                    vibrateDevice();
                    alertDialog1.dismiss();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(UI_MODE, MODE_PRIVATE).edit();
                    editor.putString("uiMode","Dark");
                    editor.apply();
                    break;
                default:
                    Toast.makeText(getApplicationContext(),"Choose a theme",Toast.LENGTH_SHORT).show();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            builder.setNeutralButton("System Default", (dialog, which) -> {
                vibrateDevice();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                SharedPreferences.Editor editor = getActivity().getSharedPreferences(UI_MODE, MODE_PRIVATE).edit();
                editor.putString("uiMode","System");
                editor.apply();
                alertDialog1.dismiss();
            });
        }
        alertDialog1 = builder.create();
        alertDialog1.show();
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
    public void reset(){
        DBHandler database = new DBHandler(getContext());
        try {
            database.delete();
        }
        catch (NullPointerException e){
            Log.d("DATABASE", "ERROR!");
            e.printStackTrace();
        }
    }
}