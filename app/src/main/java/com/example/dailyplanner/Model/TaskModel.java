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
import java.util.Objects;


public class TaskModel implements TaskModelObserved {
    private final int id;
    private final int dayId;
    private TaskFieldView view;
    private String task;
    private boolean isDone;
    private boolean isChanged = false;
    private int orderInList;
    private ArrayList<TaskModelObserver> observers = new ArrayList<>();

    public TaskModel(int id, int dayId, int orderInList, TaskFieldView view){
        this(id, dayId, orderInList);
        setView(view);
        task = "";
        isDone = false;
    }

    public TaskModel(int id, String task, boolean isDone, int dayId, int orderInList){
        this(id, dayId, orderInList);
        this.task = task;
        this.isDone = isDone;
    }

    private TaskModel(int id, int dayId, int orderInList){
        this.id = id;
        this.dayId = dayId;
        this.orderInList = orderInList;
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
        if(Objects.equals(task, "")){
            return;
        }
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

    public int getOrderInList() {
        return orderInList;
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
            observer.changeTaskStatus(view, isDone, task);
        }
    }
}
