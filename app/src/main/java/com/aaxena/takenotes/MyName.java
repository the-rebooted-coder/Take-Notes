 package com.aaxena.takenotes;

 import android.app.AlertDialog;
 import android.content.Context;
 import android.content.Intent;
 import android.content.SharedPreferences;
 import android.os.Build;
 import android.os.Bundle;
 import android.os.VibrationEffect;
 import android.os.Vibrator;
 import android.widget.Button;
 import android.widget.EditText;
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

        Button myName = findViewById(R.id.more_info_on_name);
        myName.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.more_info_title)
                    .setMessage(R.string.more_info_text)
                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton("Yes!", null)
                    .show();
        });

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
        if (saving_as.isEmpty()){
            saveMyName.setText(R.string.save_name);
        }
        else {
            saveMyName.setText(R.string.rename_file_name);
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
         Toast.makeText(this,"Name saved, will be added to upcoming files",Toast.LENGTH_SHORT).show();
         Intent i2=new Intent(MyName.this,Settings.class);
         startActivity(i2);
         overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
         finish();
     }

     @Override
     public void onBackPressed() {
         super.onBackPressed();
         Intent i=new Intent(MyName.this,Settings.class);
         startActivity(i);
         overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
         finish();
     }
}
