package com.example.eindopdracht_client_side_development_app.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

public class DatabaseHandler extends SQLiteOpenHelper
{
    private static DatabaseHandler instance;

    public DatabaseHandler(@Nullable Context context, @Nullable String name)
    {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        String mcDonaldsTableCreate = "CREATE TABLE mcDonald"
                + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "adress TEXT,"
                + "latitude DECIMAL,"
                + "longitude DECIMAL,"
                + "favorite BOOLEAN);";
        sqLiteDatabase.execSQL(mcDonaldsTableCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {

    }


    public static DatabaseHandler getInstance(@Nullable Context context, @Nullable String name)
    {
        if(instance == null)
            instance = new DatabaseHandler(context, name);
        return instance;
    }

    public static DatabaseHandler getInstance()
    {
        return instance;
    }
}
