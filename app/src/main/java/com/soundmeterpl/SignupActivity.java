package com.soundmeterpl;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class SignupActivity extends AppCompatActivity
{
    private SQLiteDatabase sqLiteDatabase;
    private EditText Login, Email, Password, RePassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        HelperDB helperDB = new HelperDB(this);
        sqLiteDatabase = helperDB.getWritableDatabase();

        Button _enterBtn = (Button) findViewById(R.id.btn_enter);

        _enterBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                switch (view.getId())
                {
                    case R.id.btn_enter:
                        if (!checkField())
                        {
                            Toast.makeText(getApplicationContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(HelperDB.COLUMN_LOGIN, Login.getText().toString());
                            contentValues.put(HelperDB.COLUMN_EMAIL, Email.getText().toString());
                            contentValues.put(HelperDB.COLUMN_PASSWORD, Password.getText().toString());

                            Bundle extras = getIntent().getExtras();
                            if (extras != null)
                            {
                                if (extras.containsKey("id"));
                                {
                                    long id = extras.getLong("id");
                                    getContentResolver().update(ContentUris.withAppendedId(MyProvider.URI_CONTENT,
                                            id), contentValues, null, null);
                                }
                            }
                            else
                            {
                                getContentResolver().insert(MyProvider.URI_CONTENT, contentValues);
                            }
                            finish();
                        }
                        break;
                }
            }
        });

        Login = (EditText) findViewById(R.id.input_login);
        Email = (EditText) findViewById(R.id.input_email);
        Password = (EditText) findViewById(R.id.input_password);
        RePassword = (EditText) findViewById(R.id.input_rePassword);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            if (extras.containsKey("id"))
            {
                long id = extras.getLong("id");

                Cursor cursor = getContentResolver().query(ContentUris.withAppendedId(MyProvider.URI_CONTENT,
                        id), null, null, null, null);

                cursor.moveToFirst();

                int indexLogin = cursor.getColumnIndexOrThrow(HelperDB.COLUMN_LOGIN);
                int indexEmail = cursor.getColumnIndexOrThrow(HelperDB.COLUMN_EMAIL);
                int indexPassword = cursor.getColumnIndexOrThrow(HelperDB.COLUMN_PASSWORD);

                String login = cursor.getString(indexLogin);
                String email = cursor.getString(indexEmail);
                String password = cursor.getString(indexPassword);

                Login.setText(login);
                Email.setText(email);
                Password.setText(password);
            }
        }
    }

    private boolean checkField()
    {
        if (Login.getText().toString().equals("") ||
                Email.getText().toString().equals("") ||
                Password.getText().toString().equals("") ||
                RePassword.getText().toString().equals("") ||
                Password.getText().toString().equals(RePassword.getText().toString()))
        {
            return false;
        }
        try
        {
            Integer.parseInt(Login.getText().toString());
            Integer.parseInt(Email.getText().toString());
        }
        catch (NumberFormatException e)
        {
            return false;
        }
        return true;
    }
}
