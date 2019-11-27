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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SignUpFragment extends Fragment implements View.OnClickListener {

    private EditText mSignUpEmail, mSignUpPassword, mConfirmPassword;
    private FirebaseAuth mAuth;
    private static final String TAG = "SignUpFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        mSignUpEmail = view.findViewById(R.id.et_sign_up_email);
        mSignUpPassword = view.findViewById(R.id.et_sign_up_password);
        mConfirmPassword = view.findViewById(R.id.et_password_confirm);
        Button signUpButton = view.findViewById(R.id.btn_sign_up);
        mAuth = FirebaseAuth.getInstance();
        signUpButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        final String email = mSignUpEmail.getText().toString();
        final String password = mSignUpPassword.getText().toString();
        final String confirmedPassword = mConfirmPassword.getText().toString();

        if (!email.trim().equals("") && !password.trim().equals("")
                && !confirmedPassword.trim().equals("")) {
            if (password.equals(confirmedPassword)) {
                signUp(email, password);
            } else {
                Toast.makeText(getActivity(),
                        "The passwords aren't the same", Toast.LENGTH_SHORT).show();
            }
        }else
        {
            Toast.makeText(getActivity(),
                    "Please don't leave any field empty", Toast.LENGTH_SHORT).show();
        }


    }


    private void signUp(String email, String password) {
        if (getActivity() != null) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmail:success");
                                startActivity(new Intent(getActivity(), ImageDisplayActivity.class));
                            } else {
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getActivity(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
    }


}
