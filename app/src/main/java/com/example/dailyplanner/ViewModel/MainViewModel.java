package com.example.dailyplanner.ViewModel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dailyplanner.AppDbHelper;
import com.example.dailyplanner.DayView;
import com.example.dailyplanner.Interfaces.DayModelObserver;
import com.example.dailyplanner.Interfaces.DaysObserved;
import com.example.dailyplanner.Interfaces.DaysObserver;
import com.example.dailyplanner.Model.DayModel;

import java.util.ArrayList;
import java.util.Objects;

public class MainViewModel extends ViewModel implements DaysObserved {
    private ArrayList<DaysObserver> observers = new ArrayList<>();
    private MutableLiveData<ArrayList<DayModel>> daysLiveData;

    AppDbHelper dbHelper;

    public MainViewModel(Context context){
        daysLiveData = new MutableLiveData<>(new ArrayList<>());
        dbHelper = new AppDbHelper(context);
        dbHelper.findDays();
    }

    public void loadDays(){
        daysLiveData.setValue(dbHelper.findDays());
        notifyObservers();
    }

    public void saveDay(long unixTimeDate, DayView view, DayModelObserver observer){
        ArrayList<DayModel> days = daysLiveData.getValue();
        assert days != null;
        int dayId;
        if(days.size() > 0){
            dayId = days.get(days.size() - 1).getId() + 1;
        }
        else{
            dayId = 0;
        }
        DayModel day = new DayModel(dayId, unixTimeDate, view);
        day.addObserver(observer);
        daysLiveData.getValue().add(day);
        //notifyObservers();
        //add to database...
        dbHelper.InsertDay(unixTimeDate);
    }

    public void removeDay(DayModel dayModel){
        Objects.requireNonNull(daysLiveData.getValue()).remove(dayModel);
        dbHelper.DeleteDay(dayModel.getUnixDate());
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
