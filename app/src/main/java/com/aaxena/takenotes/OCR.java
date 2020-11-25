package com.aaxena.takenotes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;

public class OCR extends AppCompatActivity {
    ImageView imageView;
    TextView resultTv;
    Button choose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_c_r);
        imageView = findViewById(R.id.imageView);
        resultTv = findViewById(R.id.result);
        choose = findViewById(R.id.button2);

        choose.setOnClickListener(view -> {
            Intent i = new Intent();
            i.setType("image/**");
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(i,"Select an Image to get text from"),121);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==121){
            imageView.setImageURI(data.getData());
            FirebaseVisionImage image;
            try {
                image = FirebaseVisionImage.fromFilePath(getApplicationContext(), data.getData());
                FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                        .getOnDeviceTextRecognizer();

                textRecognizer.processImage(image)
                        .addOnSuccessListener(result -> {
                            // Task completed successfully
                            resultTv.setText(result.getText());
                        })
                        .addOnFailureListener(
                                e -> {
                                    // Task failed with an exception
                                    Toast.makeText(getApplicationContext(),"Oops we ran into trouble",Toast.LENGTH_SHORT).show();
                                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
