 package com.aaxena.takenotes;

 import android.content.Context;
 import android.content.SharedPreferences;
 import android.os.Build;
 import android.os.Bundle;
 import android.os.VibrationEffect;
 import android.os.Vibrator;
 import android.view.WindowManager;
 import android.widget.Button;
 import android.widget.EditText;
 import android.widget.TextView;
 import android.widget.Toast;

 import androidx.appcompat.app.AppCompatActivity;

 import com.google.android.material.textfield.TextInputEditText;

 public class MyName extends AppCompatActivity {
     public static final String SHARED_PREFS = "sharedPrefs";
     public static final String TEXT = "text";
     TextInputEditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_name);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Button saveMyName = findViewById(R.id.save_name);
        saveMyName.setOnClickListener(v -> {
            vibrateDevice();
            EditText s = findViewById(R.id.my_name_input);
            if (s.getText().toString().isEmpty()){
                Toast.makeText(this,"Please enter a name",Toast.LENGTH_SHORT).show();
            }
            else {
                saveName();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String saving_as = sharedPreferences.getString(TEXT, "");
        if (saving_as != null) {
            if (saving_as.isEmpty()){
                saveMyName.setText(R.string.save_name);
            }
            else {
                TextView savedName = findViewById(R.id.accName);
                savedName.setText(saving_as);
                saveMyName.setText(R.string.rename_file_name);
            }
        }
    }
     private void vibrateDevice() {
         Vibrator v3 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             v3.vibrate(VibrationEffect.createOneShot(28, VibrationEffect.DEFAULT_AMPLITUDE));
         } else {
             //deprecated in API 26
             v3.vibrate(25);
         }
     }

     private void saveName() {
         SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
         SharedPreferences.Editor editor = sharedPreferences.edit();
         name = findViewById(R.id.my_name_input);
         editor.putString(TEXT,name.getText().toString());
         editor.commit();
         Toast.makeText(this,"Default File Name Saved.",Toast.LENGTH_SHORT).show();
         onBackPressed();
     }

     @Override
     public void onBackPressed() {
         super.onBackPressed();
         overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
     }
}
