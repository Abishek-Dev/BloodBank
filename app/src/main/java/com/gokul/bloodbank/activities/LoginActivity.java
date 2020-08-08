package com.gokul.bloodbank.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gokul.bloodbank.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    Button signin,signup,forgotpass;
    TextInputEditText inputemail,inputpassword;

    FirebaseAuth mAuth;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(getApplicationContext(),Dashboard.class);
            startActivity(intent);
            overridePendingTransition(R.anim.entry,R.anim.exit);
            finish();
        }

        inputemail = findViewById(R.id.input_username);
        inputpassword = findViewById(R.id.input_password);
        signin = findViewById(R.id.button_login);
        signup = findViewById(R.id.button_register);
        forgotpass = findViewById(R.id.button_forgot_password);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = inputemail.getText().toString()+"";
                final String password = inputpassword.getText().toString()+"";

                try {
                    if (password.length() > 0 && email.length() > 0){

                        progressDialog.show();

                        mAuth.signInWithEmailAndPassword(email,password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (!task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(),
                                                    "Authentication Failed",
                                                    Toast.LENGTH_LONG).show();
                                            Log.v("error",task.getException().getMessage());
                                        } else {
                                            Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.entry,R.anim.exit);
                                            finish();
                                        }
                                        progressDialog.dismiss();
                                    }
                                });
                    } else {
                        Toast.makeText(getApplicationContext(), "Please fill all the fields...", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.entry,R.anim.exit);
            }
        });

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RestorePassword.class);
                startActivity(intent);
                overridePendingTransition(R.anim.entry,R.anim.exit);
            }
        });
    }
}
