package com.example.dailyplanner.Model;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.dailyplanner.Interfaces.Observers.TaskModelObserved;
import com.example.dailyplanner.Interfaces.Observers.TaskModelObserver;
import com.example.dailyplanner.TaskFieldView;
import com.example.dailyplanner.databinding.PartTaskFieldBinding;

import java.util.ArrayList;


public class TaskModel implements TaskModelObserved {
    private final int id;
    private final int dayId;
    private TaskFieldView view;
    private String task;
    private boolean isDone;
    private boolean isChanged = false;
    private int order;
    private ArrayList<TaskModelObserver> observers = new ArrayList<>();

    public TaskModel(int id, int dayId, TaskFieldView view){
        this(id, dayId, 0);
        setView(view);
        task = "";
        isDone = false;
    }

    public TaskModel(int id, String task, boolean isDone, int dayId){
        this(id, dayId, 0);
        this.task = task;
        this.isDone = isDone;
    }

    private TaskModel(int id, int dayId, int order){
        this.id = id;
        this.dayId = dayId;
        this.order = order;
    }

    public void setView(TaskFieldView taskView) {
        view = taskView;
        PartTaskFieldBinding binding = PartTaskFieldBinding.bind(view);
        setListeners(binding);
        notifyByCheckboxChanged(binding);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners(PartTaskFieldBinding binding){
        binding.editField.setText(task);
        binding.removeButton.setOnClickListener(view -> notifyByRemove());
        binding.editField.setOnFocusChangeListener((view, hasFocus) ->
                saveEnteredTaskAfterLosingFocus(hasFocus, binding));
        binding.checkbox.setOnClickListener(view -> changeState(binding));
    }

    private void saveEnteredTaskAfterLosingFocus
            (boolean hasFocus, PartTaskFieldBinding binding){
        if (!hasFocus) {
            String newTask = binding.editField.getText().toString();
            if (!newTask.equals(task)){
                task = newTask;
                isChanged = true;
            }
        }
    }

    private void changeState(PartTaskFieldBinding binding){
        isChanged = true;
        isDone = !isDone;
        notifyByCheckboxChanged(binding);
    }

    public TaskFieldView getView(){
        return view;
    }

    public int getId(){
        return id;
    }

    public String getTask() {
        return task;
    }

    public boolean isDone() {
        return isDone;
    }

    @Override
    public void addObserver(TaskModelObserver observer) {
        observers.add(observer);
    }

    public boolean isChanged() {
        return isChanged;
    }

    @Override
    public void removeObserver(TaskModelObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyByRemove() {
        for (TaskModelObserver observer:
             observers) {
            observer.removeTask(this);
        }
    }

    @Override
    public void notifyByCheckboxChanged(PartTaskFieldBinding binding) {
        for (TaskModelObserver observer:
                observers) {
            observer.changeTaskStatus(binding, isDone, task);
        }
    }
}
