package com.example.dailyplanner.ViewModel;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dailyplanner.AppDbHelper;
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

    AppDbHelper dbHelper;

    public LiveData<Integer> getLastDayId(){
        return lastDayId;
    }

    public MainViewModel(Context context){
        daysLiveData = new MutableLiveData<>(new ArrayList<>());
        lastDayId = new MutableLiveData<>();
        lastDayId.setValue(1);
        dbHelper = new AppDbHelper(context);
        dbHelper.findDays();
    }

    public void loadDays(){
        daysLiveData.setValue(dbHelper.findDays());
    }

    public void saveDay(DayView view, int num, int month, int year){
        lastDayId.setValue(lastDayId.getValue() + 1);
        DayModel day = new DayModel(lastDayId.getValue(), num, month, year, view);
        daysLiveData.getValue().add(day);
        notifyObservers();
        view.setId(lastDayId.getValue());
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
