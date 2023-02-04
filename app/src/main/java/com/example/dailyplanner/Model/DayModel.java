package com.example.dailyplanner.Model;

import android.annotation.SuppressLint;

import com.example.dailyplanner.DayView;
import com.example.dailyplanner.Interfaces.Observers.DayModelObserved;
import com.example.dailyplanner.Interfaces.Observers.DayModelObserver;
import com.example.dailyplanner.databinding.PartDayBinding;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DayModel implements DayModelObserved, Serializable {
    final private int id;
    private final long unixDate;
    private ArrayList<String> tasks;
    private DayView view;
    private ArrayList<DayModelObserver> observers = new ArrayList<>();

    public DayModel(int id, long unixDate, DayView view){
        this(id, unixDate);
        setView(view);
    }

    public DayModel(int id, long unixDate){
        this.id = id;
        this.unixDate = unixDate;
    }

    public long getUnixDate(){
        return unixDate;
    }

    @SuppressLint("DefaultLocale")
    public String getStringDate(){
        Date date = new Date(unixDate);
        String[] splitDate = DateFormat.getDateInstance(DateFormat.SHORT)
                .format(date).split("\\.");

        return String.format("%s.%s.%s", splitDate[0], splitDate[1], splitDate[2]);
    }


    @SuppressLint("SetTextI18n")
    public void setView(DayView view){
        this.view = view;
        PartDayBinding binding = PartDayBinding.bind(view);
        binding.removeButton.setOnClickListener(v -> notifyByRemove());
        this.view.setOnClickListener(v -> {notifyByShow();});
        binding.date.setText(getStringDate());
    }

    public DayView getView(){
        return view;
    }

    public int getId(){
        return id;
    }

    @Override
    public void addObserver(DayModelObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(DayModelObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyByRemove() {
        for (DayModelObserver observer : observers){
            observer.removeDay(this);
        }
    }

    @Override
    public void notifyByShowInfo() {
        for (DayModelObserver observer : observers){
            observer.showInfo(this);
        }
    }

    @Override
    public void notifyByShow() {
        for (DayModelObserver observer : observers){
            observer.show(this);
        }
    }
}
