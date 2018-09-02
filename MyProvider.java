package com.soundmeterpl;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MyProvider extends ContentProvider
{
    HelperDB mHelperBD;
    private static final String IDENTITY = "com.soundmeterpl.MyProvider";

    public static final Uri URI_CONTENT = Uri.parse("content://"
            + IDENTITY + "/" + HelperDB.TABLE_NAME);

    private static final int TABLE = 1;
    private static final int COLUMN = 2;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static
    {
        sUriMatcher.addURI(IDENTITY, HelperDB.TABLE_NAME, TABLE);
        sUriMatcher.addURI(IDENTITY, HelperDB.TABLE_NAME + "/#", COLUMN);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri)
    {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues)
    {
        int UriType = sUriMatcher.match(uri);
        SQLiteDatabase sqLiteDatabase = mHelperBD.getWritableDatabase();

        long idAdded = 0;
        switch (UriType)
        {
            case TABLE:
                idAdded = sqLiteDatabase.insert(HelperDB.TABLE_NAME, null, contentValues);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(HelperDB.TABLE_NAME + "/" + idAdded);
    }

    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder)
    {
        int UriType = sUriMatcher.match(uri);
        SQLiteDatabase sqLiteDatabase = mHelperBD.getWritableDatabase();
        Cursor cursor = null;
        switch (UriType)
        {
            case TABLE:
                cursor = sqLiteDatabase.query(false, HelperDB.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder, null, null);
                break;

            case COLUMN:
                cursor = sqLiteDatabase.query(false, HelperDB.TABLE_NAME, projection,
                        addToSelection(selection, uri), selectionArgs, null,
                        null, sortOrder, null, null);
                break;
            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public boolean onCreate()
    {
        mHelperBD = new HelperDB(getContext());
        return false;
    }

    private String addToSelection(String selection, Uri uri)
    {
        if (selection != null && !selection.equals(""))
            selection = selection + " and " + HelperDB.ID + "=" + uri.getLastPathSegment();
        else
            selection = HelperDB.ID + "=" + uri.getLastPathSegment();
        return selection;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings)
    {
        int UriType = sUriMatcher.match(uri);
        SQLiteDatabase sqLiteDatabase = mHelperBD.getWritableDatabase();
        int deleted = 0;
        switch (UriType)
        {
            case TABLE:
                deleted = sqLiteDatabase.delete(HelperDB.TABLE_NAME, s, strings);
                break;
            case COLUMN:
                deleted = sqLiteDatabase.delete(HelperDB.TABLE_NAME, addToSelection(s, uri), strings);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return deleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings)
    {
        int UriType = sUriMatcher.match(uri);
        SQLiteDatabase baza = mHelperBD.getWritableDatabase();
        int updated = 0;
        switch (UriType)
        {
            case TABLE:
                updated = baza.update(HelperDB.TABLE_NAME, contentValues, s, strings);
                break;
            case COLUMN:
                updated = baza.update(HelperDB.TABLE_NAME, contentValues, addToSelection(s, uri), strings);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return updated;
    }
}
