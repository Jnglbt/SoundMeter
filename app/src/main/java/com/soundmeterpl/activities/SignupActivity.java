package com.soundmeterpl.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.soundmeterpl.R;


public class SignupActivity extends AppCompatActivity implements View.OnClickListener
{
    //private SQLiteDatabase sqLiteDatabase;
    private EditText  Email, Password, RePassword;
    private Button btn_signup;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();


        Email = (EditText) findViewById(R.id.Email);
        Password = (EditText) findViewById(R.id.passwordLogin);
        RePassword = (EditText) findViewById(R.id.RePassword);

        btn_signup = (Button) findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);



    }

    private void registerUser(){

        String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();
        String repassword = RePassword.getText().toString().trim();

        //TODO - validation
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter Password", Toast.LENGTH_SHORT).show();
            return;

        }
        if(password.length() < 6){
            Toast.makeText(this,"Please enter more then 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(repassword)){
            Toast.makeText(this,"Please enter rePassword", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!password.equals(repassword)){
            Toast.makeText(this,"Second Password not similar", Toast.LENGTH_SHORT).show();
            return;
        }


        progressDialog.setMessage("Registering User... Please wait.");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignupActivity.this, "Registration complete", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent( SignupActivity.this, LoginActivity.class));

                        }else{
                            if (task.getException() instanceof FirebaseApiNotAvailableException)
                                Toast.makeText(SignupActivity.this, "You are already registered", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(SignupActivity.this, "Could not registered", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.hide();
                    }


        });

    }
    @Override
    public void onClick(View v) {
        if(v == btn_signup){
            registerUser();
        }
    }









}
