package com.example.dailyplanner.ViewModel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dailyplanner.AppDbHelper;
import com.example.dailyplanner.Interfaces.Observers.DayObserved;
import com.example.dailyplanner.Interfaces.Observers.DayObserver;
import com.example.dailyplanner.Interfaces.Observers.TaskModelObserver;
import com.example.dailyplanner.Model.TaskModel;
import com.example.dailyplanner.TaskFieldView;

import java.util.ArrayList;
import java.util.Objects;


public class DayViewModel extends ViewModel implements DayObserved {

    private MutableLiveData<ArrayList<TaskModel>> dayTasksLiveData;
    private MutableLiveData<ArrayList<TaskModel>> allTasksLiveData;
    private ArrayList<DayObserver> observers = new ArrayList<>();
    private int dayId;

    private AppDbHelper dbHelper;

    public DayViewModel(Context context, int dayId){
        dayTasksLiveData = new MutableLiveData<>();
        allTasksLiveData = new MutableLiveData<>();
        dayTasksLiveData.setValue(new ArrayList<>());
        allTasksLiveData.setValue(new ArrayList<>());
        this.dayId = dayId;
        dbHelper = new AppDbHelper(context);
    }

    public void loadTasks(){
        dayTasksLiveData.setValue(dbHelper.findTasksByDay(dayId));
        allTasksLiveData.setValue(dbHelper.findAllTasks());
        notifyObservers();
    }

    public void addTask(TaskFieldView view, int dayId, TaskModelObserver observer){
        int taskId;
        ArrayList<TaskModel> allTasks = allTasksLiveData.getValue();
        assert allTasks != null;
        if(allTasks.size() == 0){
            taskId = 0;
        }
        else{
            taskId = allTasks.get(allTasks.size() - 1).getId() + 1;
        }
        TaskModel newTask = new TaskModel(taskId, dayId, view);
        newTask.addObserver(observer);
        allTasks.add(newTask);
        Objects.requireNonNull(dayTasksLiveData.getValue()).add(newTask);
        dbHelper.InsertTask(taskId, "", false, dayId);
    }

    public void saveChanges(){
        for (TaskModel task :
                dayTasksLiveData.getValue()) {
            if(task.isChanged()){
                dbHelper.UpdateTask(task.getId(), task.isDone(), task.getTask());
            }
        }
    }

    public void removeTask(TaskModel taskModel){
        Objects.requireNonNull(dayTasksLiveData.getValue()).remove(taskModel);
        Objects.requireNonNull(allTasksLiveData.getValue()).remove(taskModel);
        dbHelper.DeleteTask(taskModel.getId());
    }

    @Override
    public void addObserver(DayObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(DayObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (DayObserver obs :
                observers) {
            obs.handleEvent(dayTasksLiveData.getValue());
        }
    }
}
