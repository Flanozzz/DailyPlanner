package com.example.dailyplanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
            long unixDate = cursor.getInt(cursor.getColumnIndexOrThrow("data"));

            Date date = new Date(unixDate * 1000);
            String[] splitDate = DateFormat.getDateInstance(DateFormat.SHORT)
                    .format(date).split("\\.");

            days.add(new DayModel(id,
                    Integer.parseInt(splitDate[0]),
                    Integer.parseInt(splitDate[1]),
                    Integer.parseInt(splitDate[2])));

            cursor.moveToNext();
        }
        cursor.close();
        return days;
    }


}
