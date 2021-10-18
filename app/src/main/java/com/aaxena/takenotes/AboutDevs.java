package com.aaxena.takenotes;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class AboutDevs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_devs);
        Toast.makeText(this,"Tap for More",Toast.LENGTH_LONG).show();
        CardView devOne = findViewById(R.id.devOneHolder);
        devOne.setOnClickListener(view -> {
            ImageView spandanTitle = findViewById(R.id.devOne);
            Intent toDetails = new Intent(AboutDevs.this,devOnePopup.class);
            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<View, String>(spandanTitle,"devTitle");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AboutDevs.this,pairs);
            startActivity(toDetails,options.toBundle());
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        CardView devTwo = findViewById(R.id.devTwoHolder);
        devTwo.setOnClickListener(view -> {
            ImageView shrishTitle = findViewById(R.id.devTwo);
            Intent toDetails = new Intent(AboutDevs.this,devTwoPopup.class);
            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<View, String>(shrishTitle,"devTitleTwo");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AboutDevs.this,pairs);
            startActivity(toDetails,options.toBundle());
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }
}
