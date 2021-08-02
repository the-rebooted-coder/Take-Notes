package com.aaxena.takenotes;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;

import static android.content.Context.CLIPBOARD_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class OCR_Bottom extends Fragment {
    EditText resultTv;
    TextView tap;
    Button copyToClipboard;
    Button choose;
    Button addMore;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v2 =  inflater.inflate(R.layout.ocr_handler,container,false);
        resultTv = v2.findViewById(R.id.result);
        copyToClipboard = v2.findViewById(R.id.copyToClipboard);
        addMore = v2.findViewById(R.id.addMore);
        addMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Choose another image",Toast.LENGTH_SHORT).show();
                addMoreText();
            }
        });
        tap = v2.findViewById(R.id.choose_warning);
        choose = v2.findViewById(R.id.ocr_chooser);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        choose.setVisibility(View.VISIBLE);
        tap.setVisibility(View.VISIBLE);
        choose.setOnClickListener(view -> chooseImage());
        tap.setOnClickListener(view -> chooseImage());
        return v2;
    }
    private void addMoreText(){
        tap.setVisibility(View.GONE);
        choose.setVisibility(View.GONE);
        vibrateDevice();
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"Select another image"),122);
    }
    private void chooseImage() {
        tap.setVisibility(View.GONE);
        choose.setVisibility(View.GONE);
        vibrateDevice();
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        Toast.makeText(getApplicationContext(),"Choose an image to get text from",Toast.LENGTH_SHORT).show();
        startActivityForResult(Intent.createChooser(i,"Select another image"),121);
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
                    copyToClipboard.setVisibility(View.VISIBLE);
                    addMore.setVisibility(View.VISIBLE);
                    copyToClipboard.setOnClickListener(view -> {
                        copyToClipboard.setText("Copied!");
                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                               copyToClipboard.setText("Copy");
                            }
                        }, 1000);
                        vibrateDevice();
                        String copied_value = resultTv.getText().toString();
                        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Take Notes OCR", copied_value);
                        clipboard.setPrimaryClip(clip);
                    });
                    image = FirebaseVisionImage.fromFilePath(getApplicationContext(), data.getData());
                    FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                            .getOnDeviceTextRecognizer();
                    textRecognizer.processImage(image)
                            .addOnSuccessListener(result -> {
                                // Task completed successfully
                                resultTv.setText(result.getText());
                                tap.setVisibility(View.GONE);
                                choose.setVisibility(View.GONE);
                             // copy.setVisibility(View.VISIBLE);
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
            else if (requestCode ==122){
                FirebaseVisionImage image;
                resultTv.setVisibility(View.VISIBLE);
                copyToClipboard.setVisibility(View.VISIBLE);
                addMore.setVisibility(View.VISIBLE);
                try {
                    copyToClipboard.setOnClickListener(view -> {
                        vibrateDevice();
                        copyToClipboard.setText("Copied!");
                        String copied_value = resultTv.getText().toString();
                        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Take Notes OCR", copied_value);
                        clipboard.setPrimaryClip(clip);
                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                copyToClipboard.setText("Copy");
                            }
                        }, 1000);
                    });
                    image = FirebaseVisionImage.fromFilePath(getApplicationContext(), data.getData());
                    FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                            .getOnDeviceTextRecognizer();
                    textRecognizer.processImage(image)
                            .addOnSuccessListener(result -> {
                                // Task completed successfully
                                resultTv.append("\n\n"+result.getText());
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
            tap.setVisibility(View.GONE);
            choose.setVisibility(View.GONE);
        }
    }
}