 package com.aaxena.takenotes;

 import android.app.AlertDialog;
 import android.content.Context;
 import android.content.Intent;
 import android.content.SharedPreferences;
 import android.os.Bundle;
 import android.os.Vibrator;
 import android.widget.Button;
 import android.widget.EditText;
 import android.widget.Toast;

 import androidx.appcompat.app.AppCompatActivity;

 public class MyName extends AppCompatActivity {
     public static final String SHARED_PREFS = "sharedPrefs";
     public static final String TEXT = "text";
     EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_name);

        Button myName = findViewById(R.id.more_info_on_name);
        myName.setOnClickListener(v -> {
            Vibrator v2 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v2.vibrate(25);
            new AlertDialog.Builder(this)
                    .setTitle(R.string.more_info_title)
                    .setMessage(R.string.more_info_text)
                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton("Yes!", null)
                    .show();
        });

        Button saveMyName = findViewById(R.id.save_name);
        saveMyName.setOnClickListener(v -> {
            Vibrator v2 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v2.vibrate(26);
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
