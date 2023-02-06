package com.example.dailyplanner.Model;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.dailyplanner.DayView;
import com.example.dailyplanner.Interfaces.Observers.DayModelObserved;
import com.example.dailyplanner.Interfaces.Observers.DayModelObserver;
import com.example.dailyplanner.R;
import com.example.dailyplanner.databinding.PartDayBinding;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


public class DayModel implements DayModelObserved, Serializable {
    final private int id;
    private final long unixDate;
    private ArrayList<TaskModel> tasks;
    private DayView view;
    private ArrayList<DayModelObserver> observers = new ArrayList<>();

    public DayModel(int id, long unixDate, ArrayList<TaskModel> tasks, DayView view){
        this(id, unixDate, tasks);
        setView(view);
    }

    public DayModel(int id, long unixDate, ArrayList<TaskModel> tasks){
        this.id = id;
        this.unixDate = unixDate;
        this.tasks = tasks;
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
        setProgressText(binding);
    }

    private void setProgressText(PartDayBinding binding){
        int completedTasksCount = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            completedTasksCount = (int)tasks.stream().filter(TaskModel::isDone).count();
        }
        String viewText;
        if(completedTasksCount == tasks.size()){
            if(completedTasksCount == 0){
                viewText = view.getContext()
                        .getResources()
                        .getString(R.string.no_tasks);
            }
            else {
                viewText = "Всё сделано";
            }
        }
        else{
            viewText = tasks.size() > 0 ?
                    completedTasksCount + "/" + tasks.size() :
                    view.getContext().getResources().getString(R.string.no_tasks);
        }
        binding.text.setText(viewText);
    }

    public DayView getView(){
        return view;
    }

    public int getId(){
        return id;
    }

    public ArrayList<TaskModel> getTasks(){
        return tasks;
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
