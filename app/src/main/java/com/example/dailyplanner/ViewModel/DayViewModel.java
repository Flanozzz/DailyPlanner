package com.example.dailyplanner.ViewModel;

import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dailyplanner.AppDbHelper;
import com.example.dailyplanner.Interfaces.Observers.DayObserved;
import com.example.dailyplanner.Interfaces.Observers.DayObserver;
import com.example.dailyplanner.Interfaces.Observers.TaskModelObserver;
import com.example.dailyplanner.Model.DayModel;
import com.example.dailyplanner.Model.TaskModel;
import com.example.dailyplanner.TaskFieldView;

import java.util.ArrayList;
import java.util.Objects;


public class DayViewModel extends ViewModel implements DayObserved {
    private MutableLiveData<ArrayList<TaskModel>> allTasksLiveData;
    private ArrayList<DayObserver> observers = new ArrayList<>();
    private MutableLiveData<DayModel> dayModelLiveData;

    private AppDbHelper dbHelper;

    public DayViewModel(Context context, DayModel dayModel){
        allTasksLiveData = new MutableLiveData<>();
        dayModelLiveData = new MutableLiveData<>();
        allTasksLiveData.setValue(new ArrayList<>());
        dayModelLiveData.setValue(dayModel);
        dbHelper = new AppDbHelper(context);
    }

    public void loadTasks(){
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
        Objects.requireNonNull(dayModelLiveData.getValue()).getTasks().add(newTask);
        dbHelper.InsertTask(taskId, taskId+"", false, dayId);
    }

    public void saveChanges(){
        for (TaskModel task :
                Objects.requireNonNull(dayModelLiveData.getValue()).getTasks()) {
            dbHelper.UpdateTask(task.getId(), task.isDone(), task.getTask());
        }
    }

    public void removeTask(TaskModel taskModel){
        Objects.requireNonNull(dayModelLiveData.getValue()).getTasks().remove(taskModel);
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
            obs.handleEvent(Objects.requireNonNull(dayModelLiveData.getValue()).getTasks());
        }
    }
}
