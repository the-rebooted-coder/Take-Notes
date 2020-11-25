package com.aaxena.takenotes;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
    EditText resultTv;
    Button choose;
    Button copy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_c_r);
        resultTv = findViewById(R.id.result);
        choose = findViewById(R.id.button2);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        choose.setOnClickListener(view -> {
            Vibrator v2 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v2.vibrate(26);
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(i,"Select an Image to get text from"),121);
        });
        copy = findViewById(R.id.copy_button);
        copy.setVisibility(View.INVISIBLE);
        copy.setOnClickListener(view -> {
            Vibrator v2 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v2.vibrate(20);
            String copied_value = resultTv.getText().toString();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Take Notes OCR", copied_value);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), R.string.text_copied_to_clipboard_message,Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
        if (requestCode ==121){
            FirebaseVisionImage image;
            try {
                image = FirebaseVisionImage.fromFilePath(getApplicationContext(), data.getData());
                FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                        .getOnDeviceTextRecognizer();
                textRecognizer.processImage(image)
                        .addOnSuccessListener(result -> {
                            // Task completed successfully
                            resultTv.setText(result.getText());
                            copy.setVisibility(View.VISIBLE);
                            String copied_value = resultTv.getText().toString();
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Take Notes OCR", copied_value);
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(getApplicationContext(),R.string.text_copied_to_clipboard_message,Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(
                                e -> {
                                    // Task failed with an exception
                                    Toast.makeText(getApplicationContext(),"Oops, we ran into trouble",Toast.LENGTH_SHORT).show();
                                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        }
        else {
            //Do not remove this function, prevents the app from  crashing when user back-presses the chooser without choosing
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(OCR.this,Landing.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
