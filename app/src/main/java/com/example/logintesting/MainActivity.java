package com.example.logintesting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView register, forgetPassword;
    private EditText editTextEmail, editTextPassword;
    private Button login;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register = (TextView) findViewById(R.id.RegisterBTN);
        register.setOnClickListener(this);

        forgetPassword=(TextView)findViewById(R.id.ForgotPassowrd);
        forgetPassword.setOnClickListener(this);

        login = (Button) findViewById(R.id.LoginBTN);
        login.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.EmailAddress);
        editTextPassword=(EditText) findViewById(R.id.Password);

        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){

            case R.id.RegisterBTN:
                startActivity(new Intent(this,RegisterUser.class));
                break;

            case R.id.LoginBTN:
                userLogin();
                break;
            case R.id.ForgotPassowrd:
                startActivity(new Intent(this,ForgotPassword.class));
        }


    }

    private void userLogin() {
         String email = editTextEmail.getText().toString().trim();
         String password = editTextPassword.getText().toString().trim();

         if (email.isEmpty()){
             editTextEmail.setError("Email is required!");
             editTextEmail.requestFocus();
             return;
         }
         if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

             editTextEmail.setError("Please enter a valid email");
             editTextEmail.requestFocus();
             return;
         }
         if (password.isEmpty()){
             editTextPassword.setError("Password is required!");
             editTextPassword.requestFocus();
             return;
         }
         if (password.length() < 6){
             editTextPassword.setError("Password legnth is 6 characters!");
             editTextPassword.requestFocus();
             return;
         }

         progressBar.setVisibility(View.VISIBLE);
         mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
             @Override
             public void onComplete(@NonNull Task<AuthResult> task) {
                 if (task.isSuccessful()){
                     FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();
                     if (user.isEmailVerified()){
                         startActivity(new Intent(MainActivity.this, MapsActivity.class));


                     }else{
                         user.sendEmailVerification();
                         Toast.makeText(MainActivity.this,"Check your email box to verify the email!",Toast.LENGTH_LONG).show();
                     }
                     progressBar.setVisibility(View.GONE);


                 }else{
                     Toast.makeText(MainActivity.this, "Failed to login check your email and password!", Toast.LENGTH_SHORT).show();
                     progressBar.setVisibility(View.GONE);
                 }

             }
         });
    }
}