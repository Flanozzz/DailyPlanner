package com.example.dailyplanner.Model;

import android.annotation.SuppressLint;
import android.view.View;

import com.example.dailyplanner.DayView;

import java.util.ArrayList;

public class DayModel {
    final private int id;
    private int day;
    private int month;
    private ArrayList<String> tasks;
    private DayView view;

    public DayModel(int id, int day, int month, DayView view){
        this(id, day, month);
        this.view = view;
    }

    public DayModel(int id, int day, int month){
        this.day = day;
        this.month = month;
        this.id  = id;
    }

    @SuppressLint("DefaultLocale")
    public String ToString(){
        return String.format("%d: %d.%d", id, day, month);
    }


    public void setView(DayView view){
        this.view = view;
    }

    public int getDay() {
        return day;
    }

    public int getMonth(){
        return month;
    }

    public int getId(){
        return id;
    }

    public DayView getView() { return view; }
}
