package com.example.android.conde.com.behance.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.conde.com.behance.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private EditText mPassword, mEmail;
    private FirebaseAuth mAuth;
    private static final String TAG = "LoginFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mPassword = view.findViewById(R.id.et_password);
        mEmail = view.findViewById(R.id.et_email);
        mAuth = FirebaseAuth.getInstance();
        Button loginButton = view.findViewById(R.id.btn_log_in);
        loginButton.setOnClickListener(this);
        return view;
    }



    @Override
    public void onClick(View view) {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        if(!email.trim().equals("") && !password.trim().equals("")){
            signIn(email, password);
        }
    }


    private void signIn(String email, String password){
        if(getActivity() != null) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (getActivity() != null) {
                                    getActivity().startActivity(new Intent(getActivity(), ImageDisplayActivity.class));
                                    getActivity().finish();
                                }
                            } else {
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(getContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
        }
    }




}
