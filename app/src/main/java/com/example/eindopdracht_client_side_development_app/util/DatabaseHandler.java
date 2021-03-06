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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.FileHandler;

public class DatabaseHandler extends SQLiteOpenHelper
{
    private static DatabaseHandler instance;
    private Context context;

    public DatabaseHandler(@Nullable Context context, @Nullable String name)
    {
        super(context, name, null, 1);
        this.context = context;
        readJsonFile();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        String mcDonaldsTableCreate = "CREATE TABLE McDonalds"
                + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "address TEXT,"
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

        Cursor mcDonaldCursor = db.rawQuery("SELECT * FROM McDonalds;", null);

        if (mcDonaldCursor.moveToFirst())
        {
            do
            {
                int id = mcDonaldCursor.getInt(mcDonaldCursor.getColumnIndex("id"));
                String address = mcDonaldCursor.getString(mcDonaldCursor.getColumnIndex("address"));
                double latitude = mcDonaldCursor.getDouble(mcDonaldCursor.getColumnIndex("latitude"));
                double longitude = mcDonaldCursor.getDouble(mcDonaldCursor.getColumnIndex("longitude"));
                String phonenumber = mcDonaldCursor.getString(mcDonaldCursor.getColumnIndex("phonenumber"));
                boolean isFavorite = (mcDonaldCursor.getInt(mcDonaldCursor.getColumnIndex("favorite")) == 0) ? false : true;

                LatLng latLng = new LatLng(latitude,longitude);

                McDonalds mcDonald = new McDonalds(id, address, phonenumber, latLng, isFavorite);

                mcDonalds.add(mcDonald);
            } while (mcDonaldCursor.moveToNext());
        }

        return mcDonalds;
    }

    public boolean addMcDonalds(McDonalds mcDonalds)
    {
        if(mcDonalds == null || mcDonaldsExists(mcDonalds))
            return false;

        ContentValues values = new ContentValues();
        values.put("address", mcDonalds.getAddress());
        values.put("latitude", mcDonalds.getLocation().latitude);
        values.put("longitude", mcDonalds.getLocation().longitude);
        values.put("phonenumber", mcDonalds.getPhoneNumber());
        values.put("favorite", mcDonalds.isFavorite());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("McDonalds", null, values);

        return true;
    }
    public void readJsonFile()
    {
        try
        {
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset("McDonaldsList.json"));
            JSONArray jsonArray = jsonObject.getJSONArray("restaurants");
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject mcDonaldsObject = jsonArray.getJSONObject(i);

                String address = mcDonaldsObject.getString("address");
                double latitude = mcDonaldsObject.getDouble("latitude");
                double longitude = mcDonaldsObject.getDouble("longitude");
                String phonenumber = mcDonaldsObject.getString("phonenumber");

                LatLng latLng = new LatLng(latitude,longitude);

                McDonalds mcDonalds = new McDonalds(address,phonenumber,latLng);

                addMcDonalds(mcDonalds);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset(String file)
    {
        String json = null;
        try
        {
            InputStream is = context.getAssets().open(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

        return json;
    }

    public boolean updateMcDonalds(McDonalds mcDonalds)
    {
        if(mcDonalds == null || mcDonalds.getId() == -1 || !mcDonaldsExists(mcDonalds))
            return false;

        ContentValues values = new ContentValues();
        values.put("phonenumber", mcDonalds.getPhoneNumber());
        values.put("favorite", mcDonalds.isFavorite());

        SQLiteDatabase db = this.getWritableDatabase();
        db.update("McDonalds", values, "id = ?", new String[] { String.valueOf(mcDonalds.getId()) });

        return true;
    }

    public boolean mcDonaldsExists(McDonalds mcDonalds)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, "McDonalds", "address = ?", new String[] { mcDonalds.getAddress() });
        //long count = DatabaseUtils.queryNumEntries(db, "McDonalds", "id = ?", new String[] { String.valueOf(mcDonalds.getId()) });

        if (count != 0)
            return true;
        return false;
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
