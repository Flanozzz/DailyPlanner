package com.example.dailyplanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.dailyplanner.Model.DayModel;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AppDbHelper {
    private SQLiteDatabase db;

    public AppDbHelper(Context ctx){
        db = new AppSQLiteHelper(ctx).getWritableDatabase();
    }

    @SuppressLint("Recycle")
    public ArrayList<DayModel> findDays(){
        Cursor cursor = db.rawQuery("SELECT * FROM days", null);
        cursor.moveToFirst();
        ArrayList<DayModel> days = new ArrayList<>();

        //TODO thread
        //TODO db const string
        for (int i = 0; i < cursor.getCount(); i++) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            long unixDate = cursor.getLong(cursor.getColumnIndexOrThrow("data"));
            days.add(new DayModel(id, unixDate)); //year
            cursor.moveToNext();
        }
        cursor.close();
        return days;
    }

    @SuppressLint("DefaultLocale")
    public void InsertDay(long unixTime){
        Log.w("AAA", "Insert Day");
        db.execSQL(String.format("INSERT INTO days (data) VALUES (%d)", unixTime));
        for (DayModel day : findDays()){
            Log.e("AAA", day.getId() + "  " + day.getUnixDate());
        }
    }

    @SuppressLint("DefaultLocale")
    public void DeleteDay(long unixTime){
        Log.w("AAA", "Delete Day");
        Cursor cursor = db.rawQuery(String.format("SELECT * FROM days WHERE data = %d", unixTime), null);
        cursor.moveToFirst();
        Log.w("AAA", cursor.getCount() + "");
        for (int i = 0; i < cursor.getCount(); i++) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            long unixDate = cursor.getLong(cursor.getColumnIndexOrThrow("data"));
            Log.d("AAA", id + "  " + unixDate);

            cursor.moveToNext();
        }

        db.execSQL(String.format("DELETE FROM days WHERE data = %d", unixTime));
    }
}
