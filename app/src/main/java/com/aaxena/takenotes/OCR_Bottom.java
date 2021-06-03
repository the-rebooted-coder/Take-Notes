package com.aaxena.takenotes;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;

import static android.content.Context.CLIPBOARD_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class OCR_Bottom extends Fragment {
    EditText resultTv;
    TextView tap;
    Button choose;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v2 =  inflater.inflate(R.layout.ocr_handler,container,false);
        resultTv = v2.findViewById(R.id.result);
        tap = v2.findViewById(R.id.choose_warning);
        choose = v2.findViewById(R.id.ocr_chooser);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        choose.setOnClickListener(view -> chooseImage());
        tap.setOnClickListener(view -> chooseImage());
        return v2;
    }
    private void chooseImage() {
        tap.setVisibility(View.GONE);
        choose.setVisibility(View.GONE);
        vibrateDevice();
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"Select an Image to get text from"),121);
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            if (requestCode ==121){
                FirebaseVisionImage image;
                try {
                    resultTv.setVisibility(View.VISIBLE);
                    image = FirebaseVisionImage.fromFilePath(getApplicationContext(), data.getData());
                    FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                            .getOnDeviceTextRecognizer();
                    textRecognizer.processImage(image)
                            .addOnSuccessListener(result -> {
                                // Task completed successfully
                                resultTv.setText(result.getText());
                                tap.setVisibility(View.GONE);
                                choose.setVisibility(View.GONE);
                             //   copy.setVisibility(View.VISIBLE);
                                String copied_value = resultTv.getText().toString();
                                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
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
            //Do not remove this function, prevents the app from crashing when user back-presses the chooser without choosing
        }
    }
}