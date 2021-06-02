package com.aaxena.takenotes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

import static android.content.Context.MODE_PRIVATE;
import static com.aaxena.takenotes.MyName.SHARED_PREFS;
import static com.aaxena.takenotes.MyName.TEXT;
import static com.facebook.FacebookSdk.getApplicationContext;

public class More extends Fragment {
    public static final String UI_MODE = "uiMode";
    AlertDialog alertDialog1;
    private TextView loggedInName,savedName;
    private DBHandler dbHandler;
    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v3 = inflater.inflate(R.layout.more, container, false);
        CardView dynamicHolder =  v3.findViewById(R.id.dynamicHolder);
        TextView dynamicText = v3.findViewById(R.id.dynamicText);

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

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String saving_as = sharedPreferences.getString(TEXT, "");
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
            share.setVisibility(View.INVISIBLE);
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
            startActivity(i);
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        Button theme = v3.findViewById(R.id.theme);
        theme.setOnClickListener(view -> CreateAlertDialogWithRadioButtonGroup());
        return v3;
    }
    public void CreateAlertDialogWithRadioButtonGroup() {
        int nightModeFlags =
                this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose Theme for Take Notes");
        builder.setPositiveButton("Light", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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