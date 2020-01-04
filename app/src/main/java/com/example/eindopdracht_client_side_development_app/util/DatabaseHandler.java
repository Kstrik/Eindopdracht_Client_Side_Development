package com.example.eindopdracht_client_side_development_app.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.eindopdracht_client_side_development_app.models.McDonalds;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

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
        String mcDonaldsTableCreate = "CREATE TABLE McDonald"
                + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "adress TEXT,"
                + "latitude DECIMAL,"
                + "longitude DECIMAL,"
                + "phonenumber INTEGER,"
                + "favorite BOOLEAN);";
        sqLiteDatabase.execSQL(mcDonaldsTableCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {

    }

    public ArrayList<McDonalds> getAllMcDonalds()
    {
        ArrayList<McDonalds> mcDonalds = new  ArrayList<McDonalds>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor mcDonaldCursor = db.rawQuery("SELECT * FROM McDonald;", null);

        if (mcDonaldCursor.moveToFirst())
        {
            do
            {
                int id = mcDonaldCursor.getInt(mcDonaldCursor.getColumnIndex("id"));
                String adress = mcDonaldCursor.getString(mcDonaldCursor.getColumnIndex("adress"));
                double latitude = mcDonaldCursor.getDouble(mcDonaldCursor.getColumnIndex("latitude"));
                double longitude = mcDonaldCursor.getDouble(mcDonaldCursor.getColumnIndex("longitude"));
                String phonenumber = mcDonaldCursor.getString(mcDonaldCursor.getColumnIndex("phonenumber"));

                LatLng latLng = new LatLng(latitude,longitude);

                McDonalds mcDonald = new McDonalds(id, adress,phonenumber,latLng);

                mcDonalds.add(mcDonald);
            } while (mcDonaldCursor.moveToNext());
        }

        return mcDonalds;
    }


    public boolean addMcDonalds(McDonalds mcDonalds)
    {

        ContentValues values = new ContentValues();
        values.put("adress", mcDonalds.getAddress());
        values.put("latitude", mcDonalds.getLocation().latitude);
        values.put("longitude", mcDonalds.getLocation().longitude);
        values.put("phonenumber", mcDonalds.getPhoneNumber());
        values.put("favorite", mcDonalds.isFavorite());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("McDonald", null, values);


        return true;
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
