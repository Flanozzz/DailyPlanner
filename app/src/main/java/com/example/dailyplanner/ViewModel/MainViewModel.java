package com.example.dailyplanner.ViewModel;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dailyplanner.DatabaseHelper;
import com.example.dailyplanner.DayView;
import com.example.dailyplanner.Interfaces.Observed;
import com.example.dailyplanner.Interfaces.DaysObserver;
import com.example.dailyplanner.Model.DayModel;

import java.util.ArrayList;
import java.util.Objects;

public class MainViewModel extends ViewModel implements Observed {
    private ArrayList<DaysObserver> observers = new ArrayList<>();
    private MutableLiveData<ArrayList<DayModel>> daysLiveData;
    private MutableLiveData<Integer> lastDayId;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;

    public LiveData<Integer> getLastDayId(){
        return lastDayId;
    }

    public MainViewModel(Context context){
        daysLiveData = new MutableLiveData<>(new ArrayList<>());
        lastDayId = new MutableLiveData<>();
        lastDayId.setValue(1);
        Log.w("AAA", "view model");
    }

    public void loadDays(Context ctx){
        dbHelper = new DatabaseHelper(ctx);
        db = dbHelper.open();

        cursor = db.rawQuery("select * from " + DatabaseHelper.TABLE, null);
        cursor.moveToFirst();
        daysLiveData.setValue(new ArrayList<>());
        int lineCount = cursor.getCount();
        for(int i = 0; i < lineCount; i++) {
            int dayId = cursor.getInt(0);
            int dayNum = cursor.getInt(1);
            int dayMonth = cursor.getInt(2);
            DayModel day = new DayModel(dayId, dayNum, dayMonth);
            Objects.requireNonNull(daysLiveData.getValue()).add(day);
            cursor.moveToNext();
        }
        cursor.close();
        notifyObservers();
        ArrayList<DayModel> days = daysLiveData.getValue();
        lastDayId.setValue(days.get(days.size() - 1).getId());
    }

    public void saveDay(DayView view, int num, int month){
        lastDayId.setValue(lastDayId.getValue() + 1);
        DayModel day = new DayModel(lastDayId.getValue(), num, month, view);
        daysLiveData.getValue().add(day);
        notifyObservers();
        //add to database...
    }

    @Override
    public void addObserver(DaysObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(DaysObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (DaysObserver obs :
                observers) {
            obs.handleEvent(daysLiveData.getValue());
        }
    }
}
