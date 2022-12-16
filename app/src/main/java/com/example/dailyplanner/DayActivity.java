package com.example.dailyplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class DayActivity extends AppCompatActivity {

    TextView dayView;
    TextView monthView;
    TextView idView;

    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    Cursor cursor;
    long dayId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        dayView = findViewById(R.id.day);
        monthView = findViewById(R.id.month);
        idView = findViewById(R.id.num);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.open();
    }

    @Override
    protected void onStart(){
        super.onStart();
        //Load();
        //Toast.makeText(this,"start", Toast.LENGTH_LONG).show();
    }

    @SuppressLint("SetTextI18n")
    private void Load(){
        String sDayId = getIntent().getExtras().getString("DayId");
        char chDayId = sDayId.charAt(sDayId.length() - 1);
        dayId = Character.getNumericValue(chDayId);
        idView.setText(String.valueOf(dayId));
        if(dayId > 0){
            cursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                    DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(dayId)});
            cursor.moveToFirst();
            dayView.setText(cursor.getString(1) + ".");
            monthView.setText(cursor.getString(2));
            idView.setText(cursor.getString(0));
            cursor.close();
        }
    }

    public void backToDays(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}