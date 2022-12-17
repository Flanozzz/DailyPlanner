package com.example.dailyplanner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class AppSQLiteHelper extends SQLiteOpenHelper {

    Context applicationContext;

    public AppSQLiteHelper(Context applicationContext) {
        super(applicationContext, "database.db", null, 1);
        this.applicationContext = applicationContext;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d("AAA", "onCreate SQLiteOpenHelper");
        String sql = "";
        StringBuilder strBuilder = new StringBuilder();

        try {
            InputStream inputStream = applicationContext.getAssets().open("db_init.sql");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            int b = 0;
            while((b = inputStreamReader.read()) != -1){
                strBuilder.append((char)b);
            }

            sql = strBuilder.toString();
            inputStream.close();
            inputStreamReader.close();
        } catch (IOException e) {
            Log.w("Errors", e.getMessage());
            e.printStackTrace();
        }

        Log.w("AAA", sql);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Arrays.stream(sql.split(";"))
                    .filter((item) -> !item.isEmpty())
                    .forEach(sqLiteDatabase::execSQL);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //миграции
    }
}
