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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.soundmeterpl.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    private Button buttonSingIn;
    private EditText editTextEmail, editTextPassword;
    private TextView signUp, forgotPassword;

    private LinearLayout vert;
    private LinearLayout hor;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Log in");

        hor = (LinearLayout) findViewById(R.id.hor_layout);
        vert = (LinearLayout) findViewById(R.id.vert_layout);

        firebaseAuth = firebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.emailLogin);
        editTextPassword = (EditText) findViewById(R.id.passwordLogin);

        buttonSingIn = (Button) findViewById(R.id.btn_enter);

        signUp = (TextView) findViewById(R.id.link_signup);
        forgotPassword = (TextView) findViewById(R.id.fgpText);

        progressDialog = new ProgressDialog(this);

        buttonSingIn.setOnClickListener(this);
        signUp.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);

    }


    @Override
    public void onClick(View v)
    {
        if (v == buttonSingIn)
        {
            userLogin();

        }
        if (v == signUp)
        {
            startActivity(new Intent(this, SignupActivity.class));
        }
        if (v == forgotPassword)
        {
            startActivity(new Intent(this, ReminderActivity.class));
        }
    }

    private void userLogin()
    {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please enter Password", Toast.LENGTH_SHORT).show();
            return;

        }

        progressDialog.setMessage("Login User... Please wait.");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        progressDialog.dismiss();
                        if (task.isSuccessful())
                        {
                            startActivity(new Intent(
                                    LoginActivity.this, MainActivity.class
                            ));
                        } else
                        {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
