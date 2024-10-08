package com.example.FSMap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

//This class handles the Forgot Password screen
public class ForgotPassword extends AppCompatActivity {

    private TextInputLayout emailEditText;
    private Button resetPassword;
    private ProgressBar progressbar;
    ImageView backForgot;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        emailEditText =  findViewById(R.id.ForgotPasswordEmail);
        resetPassword = (Button) findViewById(R.id.resetBTN);
        progressbar = (ProgressBar) findViewById(R.id.ProgressBar);
        backForgot = findViewById(R.id.backForgot);

        auth = FirebaseAuth.getInstance();

        //Run the function when reset password is clicked
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetpassword();
            }
        });

        //Run the function when the back button is clicked
        backForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(v.getContext(),MainActivity.class);
               startActivity(intent);
            }
        });

    }
    //Function handles checking and then sending the email to reset the password of the account
    private void resetpassword(){

        String email = emailEditText.getEditText().getText().toString().trim();

        if (email.isEmpty()){
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Please provide a valid email!");
            emailEditText.requestFocus();
            return;
        }

        progressbar.setVisibility(View.VISIBLE);
        //Send the email to reset the password.
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            //Verifying if the email was successfully sent or not.
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ForgotPassword.this,"Check your email to reset your password!",Toast.LENGTH_LONG).show();
                    progressbar.setVisibility(View.GONE);
                }else{
                    Toast.makeText(ForgotPassword.this,"Try again! Something wrong Happened!",Toast.LENGTH_LONG).show();

                    progressbar.setVisibility(View.GONE);

                }

            }
        });
    }
}