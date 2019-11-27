package com.example.android.conde.com.behance.ui;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.conde.com.behance.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AuthActivity";
    private Button mFacebookLogin, mGoogleLogin,mSignUpButton;
    private TextView mLoginTextView;
    private FrameLayout mFragmentContainer;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressBar mProgressBar;
    private ConstraintLayout mAuthLayout;

    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        mFacebookLogin = findViewById(R.id.btn_facebook_log_in);
        mGoogleLogin = findViewById(R.id.btn_google_log_in);
        mSignUpButton = findViewById(R.id.btn_email_sign_up);
        mProgressBar = findViewById(R.id.auth_progress_bar);
        mAuthLayout = findViewById(R.id.auth_layout);
        mLoginTextView = findViewById(R.id.tv_log_in);
        mFragmentContainer = findViewById(R.id.auth_fragment_container);
        mAuth = FirebaseAuth.getInstance();
        mFacebookLogin.setOnClickListener(this);
        mGoogleLogin.setOnClickListener(this);
        mSignUpButton.setOnClickListener(this);
        mLoginTextView.setOnClickListener(this);


        configureGoogleSignIn();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startActivity(new Intent(this, ImageDisplayActivity.class));
            finish();
        }

        //startActivity(new Intent(this, ImageDisplayActivity.class));
        backgroundAnimation();
    }


    private void backgroundAnimation(){
        AnimationDrawable animationDrawable = (AnimationDrawable) mAuthLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_facebook_log_in:
                break;
            case R.id.btn_google_log_in:
                showProgressBar();
                signInWithGoogle();
                break;
            case R.id.btn_email_sign_up:
                SignUpFragment signUpFragment = new SignUpFragment();
                displayFragment(signUpFragment);
                break;
            case R.id.tv_log_in:
                LoginFragment loginFragment = new LoginFragment();
                displayFragment(loginFragment);
                break;
        }
    }


    private void showProgressBar(){
        mProgressBar.setVisibility(View.VISIBLE);
        mGoogleLogin.setVisibility(View.INVISIBLE);
    }

    private void hideProgressBar(){
        mProgressBar.setVisibility(View.INVISIBLE);
        mGoogleLogin.setVisibility(View.VISIBLE);
    }

    private void configureGoogleSignIn(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                hideProgressBar();
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if(account != null)
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);

            }
        }
    }



    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        //showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            startActivity(new Intent(AuthActivity.this, ImageDisplayActivity.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(mGoogleLogin, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        //hideProgressDialog();
                    }
                });
    }


    private void signInWithFacebook(){

    }


    private void signInWithGoogle(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }




    private void displayFragment(Fragment fragment){
        mFacebookLogin.setVisibility(View.INVISIBLE);
        mGoogleLogin.setVisibility(View.INVISIBLE);
        mSignUpButton.setVisibility(View.INVISIBLE);
        mLoginTextView.setVisibility(View.INVISIBLE);
        mFragmentContainer.setVisibility(View.VISIBLE);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction
                .replace(R.id.auth_fragment_container, fragment);
        transaction.addToBackStack("");
        transaction.commit();

    }



    public void hideFragment(){
        if(mFacebookLogin.getVisibility() == View.INVISIBLE &&
        mGoogleLogin.getVisibility() == View.INVISIBLE &&
        mGoogleLogin.getVisibility() == View.INVISIBLE &&
        mLoginTextView.getVisibility() == View.INVISIBLE) {
            mFacebookLogin.setVisibility(View.VISIBLE);
            mGoogleLogin.setVisibility(View.VISIBLE);
            mSignUpButton.setVisibility(View.VISIBLE);
            mLoginTextView.setVisibility(View.VISIBLE);
            mFragmentContainer.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideFragment();
    }
}
