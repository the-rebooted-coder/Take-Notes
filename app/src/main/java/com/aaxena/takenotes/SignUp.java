package com.aaxena.takenotes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private int RC_SIGN_IN =1;
    CallbackManager mCallbackManager;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Lottie
        final boolean isAnimated=false;
        loading = findViewById(R.id.sign_up_anim);
        loading.setVisibility(View.INVISIBLE);
        //Facebook SDK Init
        FacebookSdk.sdkInitialize(SignUp.this);
        mAuth = FirebaseAuth.getInstance();

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(v -> {
            Vibrator v2 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v2.vibrate(30);
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
                    loginButton.setVisibility(View.VISIBLE);
                    signInButton.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.INVISIBLE);
                    Toast.makeText(SignUp.this,"User cancelled the Login",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException error) {
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
                loginButton.setVisibility(View.INVISIBLE);
                signInButton.setVisibility(View.INVISIBLE);
                loading.setVisibility(View.VISIBLE);
                loading.playAnimation();
                Vibrator v2 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v2.vibrate(30);
                signIn();
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
            Intent intent = new Intent(SignUp.this,Landing.class);
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
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try {
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
            if (account !=null){
                Intent i=new Intent(SignUp.this,Landing.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
            FirebaseGoogleAuth(acc);
        }
        catch (ApiException e){
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
                    Toast.makeText(SignUp.this,"Failed",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}