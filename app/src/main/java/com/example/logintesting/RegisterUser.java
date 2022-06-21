package com.example.logintesting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private TextView registerUser;
    private EditText editEmail, editPassword,confirmEmail , confirmPassword;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        registerUser = (TextView) findViewById(R.id.RegisterUser);
        registerUser.setOnClickListener(this);

        editEmail = (EditText) findViewById(R.id.registerEmailAddress);
         editPassword = (EditText)  findViewById(R.id.registerPassword);
        confirmEmail = (EditText) findViewById(R.id.ConfirmEmailAddress) ;
        confirmPassword = (EditText) findViewById(R.id.ConfirmPassword);

        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);

    }

    @Override
    public void onClick(View view) {
             switch(view.getId()){
                 case R.id.RegisterUser:
                     registerUser();
                     break;
             }
    }

    private void registerUser() {

        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String confirmE = confirmEmail.getText().toString().trim();
        String confirmP = confirmPassword.getText().toString().trim();

        if(email.isEmpty()){

            editEmail.setError("An email is required!");
            editEmail.requestFocus();
            return;
        }
        if (confirmE.isEmpty()){
            confirmEmail.setError("You need to confirm the email!");
            confirmEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            editEmail.setError("Please provide a valid email!");
            editEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(confirmE).matches()){
            confirmEmail.setError("You need to confirm the email");
            confirmEmail.requestFocus();
            return;
        }
        if (password.isEmpty()){
            editPassword.setError("A password is required!");
            editPassword.requestFocus();
            return;
        }
        if (confirmP.isEmpty()){
            confirmPassword.setError("You need to confirm the password!");
            confirmPassword.requestFocus();
            return;
        }
        if (password.length() < 6 ){
            editPassword.setError("The password should be at least 6 characters!");
            editPassword.requestFocus();
            return;
        }
        if (confirmP.length()<6){
            confirmPassword.setError("The password should be the same amount than the other password!");
            confirmPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            User user = new User(confirmE);
                            FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){
                                                Toast.makeText(RegisterUser.this,"User has been registered successfully!",Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                            }else{
                                                Toast.makeText(RegisterUser.this,"Failed to register user! Try again!",Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                            }

                                        }
                                    });
                        }
                        else{
                            Toast.makeText(RegisterUser.this,"Failed to register user! Try again!",Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }
}