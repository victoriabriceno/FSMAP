package com.example.FSMap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;


public class RegisterUser extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private TextView registerUser;
    private EditText editEmail, editPassword,confirmEmail , confirmPassword, editUser;
    private ProgressBar progressBar;
    private Button registerBack;
    ImageView registerBackButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        registerUser = (TextView) findViewById(R.id.RegisterUser);
        registerUser.setOnClickListener(this);

        registerBackButton = findViewById(R.id.backBTN);
        registerBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterUser.this,MainActivity.class));
            }
        });
        editEmail = (EditText) findViewById(R.id.registerEmailAddress);
        editPassword = (EditText)  findViewById(R.id.registerPassword);
        confirmEmail = (EditText) findViewById(R.id.ConfirmEmailAddress) ;
        confirmPassword = (EditText) findViewById(R.id.ConfirmPassword);
        editUser  =(EditText) findViewById(R.id.Username) ;

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

        String userName = editUser.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String confirmE = confirmEmail.getText().toString().trim();
        String confirmP = confirmPassword.getText().toString().trim();


        if (userName.isEmpty()){
            editUser.setError("An user is required!");
            editUser.requestFocus();
            return;
        }
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
                            User user = new User(confirmE,userName);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();
                                                user.sendEmailVerification();
                                                FirebaseAuth.getInstance().signOut();
                                                Toast.makeText(RegisterUser.this,"Check your email box to verify the email!",Toast.LENGTH_LONG).show();
                                                Intent i = new Intent(RegisterUser.this, MainActivity.class);
                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                SharedPreferences preferences = getSharedPreferences("checkBox",MODE_PRIVATE);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString("remember","false");
                                                editor.apply();
                                                startActivity(i);
                                            }else{
                                                Toast.makeText(RegisterUser.this,"Failed to register user! Try again!",Toast.LENGTH_LONG).show();
                                            }
                                            progressBar.setVisibility(View.GONE);

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