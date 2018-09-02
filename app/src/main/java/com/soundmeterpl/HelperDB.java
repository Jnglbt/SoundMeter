package com.soundmeterpl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HelperDB extends SQLiteOpenHelper
{
    private Context mKontekst;

    public final static int DB_VERSION = 1;
    public final static String DB_NAME = "UserManager";
    public final static String TABLE_NAME = "user";
    public final static String ID = "_id";

    public static final String COLUMN_LOGIN = "user_login";
    public static final String COLUMN_EMAIL = "user_email";
    public static final String COLUMN_PASSWORD = "user_password";

    public String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_LOGIN + " TEXT,"
            + COLUMN_EMAIL + " TEXT," + COLUMN_PASSWORD + " TEXT" + ")";

    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public HelperDB(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVer, int newVer)
    {
        sqLiteDatabase.execSQL(DROP_USER_TABLE);
        onCreate(sqLiteDatabase);
    }
}

