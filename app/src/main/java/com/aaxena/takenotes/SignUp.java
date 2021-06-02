package com.aaxena.takenotes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class SignUp extends AppCompatActivity {
    private Button signInButton;
    LottieAnimationView loading;
    private GoogleSignInClient mGoogleSignInClient;
    private String TAG = "Login";
    private FirebaseAuth mAuth;
    public static final String STATUS = "acc_status";
    private int RC_SIGN_IN =1;
    String acc_status;
    CallbackManager mCallbackManager;
    Button loginButton;
    TextView skip;
    View linePlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        SharedPreferences prefs = getSharedPreferences(STATUS, MODE_PRIVATE);
        acc_status = prefs.getString("acc_status", "okay");
        linePlace.findViewById(R.id.linePlace);
        skip = findViewById(R.id.skipSign);
        skip.setOnClickListener(view -> {
            vibrateDevice();
            SharedPreferences settings = getSharedPreferences("hasSignedIn", 0);
            SharedPreferences.Editor edit = settings.edit();
            edit.putBoolean("hasSignedIn", false);
            edit.apply();
            Intent toLanding = new Intent(SignUp.this,BottomHandler.class);
            startActivity(toLanding);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
        loading = findViewById(R.id.sign_up_anim);
        loading.setVisibility(View.INVISIBLE);
        //Facebook SDK Init
        FacebookSdk.sdkInitialize(SignUp.this);
        mAuth = FirebaseAuth.getInstance();

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(v -> {
                vibrateDevice();
                linePlace.setVisibility(View.INVISIBLE);
                skip.setVisibility(View.INVISIBLE);
                loginButton.setVisibility(View.INVISIBLE);
                signInButton.setVisibility(View.INVISIBLE);
                loading.setVisibility(View.VISIBLE);
                loading.playAnimation();
            LoginManager.getInstance().logInWithReadPermissions(SignUp.this, Arrays.asList("email","public_profile"));
            LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Toast.makeText(SignUp.this,"Logged In Successfully, Please wait!",Toast.LENGTH_SHORT).show();
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    linePlace.setVisibility(View.VISIBLE);
                    skip.setVisibility(View.VISIBLE);
                    loginButton.setVisibility(View.VISIBLE);
                    signInButton.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.INVISIBLE);
                    Toast.makeText(SignUp.this,"User cancelled the Login",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException error) {
                    linePlace.setVisibility(View.VISIBLE);
                    skip.setVisibility(View.VISIBLE);
                    loginButton.setVisibility(View.VISIBLE);
                    signInButton.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.INVISIBLE);
                    Toast.makeText(SignUp.this,"Oops something went wrong",Toast.LENGTH_SHORT).show();
                }
            });
        });
        signInButton = findViewById(R.id.sign_in_button);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (acc_status.equals("okay")) {
                    linePlace.setVisibility(View.INVISIBLE);
                    skip.setVisibility(View.INVISIBLE);
                    loginButton.setVisibility(View.INVISIBLE);
                    signInButton.setVisibility(View.INVISIBLE);
                    loading.setVisibility(View.VISIBLE);
                    loading.playAnimation();
                    vibrateDevice();
                    signIn();
                }
                else {
                    Toast.makeText(SignUp.this,"Your account is temporarily suspended due to proactive use, contact the developer",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                        finish();
                    } else {
                        linePlace.setVisibility(View.VISIBLE);
                        loginButton.setVisibility(View.VISIBLE);
                        signInButton.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.INVISIBLE);
                        // If sign in fails, display a message to the user.
                        Toast.makeText(SignUp.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    private void updateUI(FirebaseUser user) {
        if (user!= null){
            SharedPreferences settings = getSharedPreferences("hasSignedIn", 0);
            SharedPreferences.Editor edit = settings.edit();
            edit.putBoolean("hasSignedIn", true);
            edit.apply();
            Intent intent = new Intent(SignUp.this,BottomHandler.class);
            startActivity(intent);
            finish();
        }
    }

    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void vibrateDevice() {
        Vibrator v3 = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v3.vibrate(VibrationEffect.createOneShot(28, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v3.vibrate(25);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try {
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
            if (account !=null){
                SharedPreferences settings = getSharedPreferences("hasSignedIn", 0);
                SharedPreferences.Editor edit = settings.edit();
                edit.putBoolean("hasSignedIn", true);
                edit.apply();
                Intent i=new Intent(SignUp.this,BottomHandler.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
            FirebaseGoogleAuth(acc);
        }
        catch (ApiException e){
            linePlace.setVisibility(View.VISIBLE);
            skip.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.VISIBLE);
            loading.setVisibility(View.INVISIBLE);
            Toast.makeText(SignUp.this,"Something went wrong",Toast.LENGTH_LONG).show();
        }
    }
    private void FirebaseGoogleAuth(GoogleSignInAccount acct){
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                }
                else {
                    recreate();
                    SharedPreferences.Editor editor = getSharedPreferences(STATUS, MODE_PRIVATE).edit();
                    editor.putString("acc_status","suspended");
                    editor.apply();
                    Toast.makeText(SignUp.this,"Your account is temporarily suspended due to proactive use, contact the developer",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}