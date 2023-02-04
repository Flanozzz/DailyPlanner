package com.example.dailyplanner.Model;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.dailyplanner.Interfaces.Observers.TaskModelObserved;
import com.example.dailyplanner.Interfaces.Observers.TaskModelObserver;
import com.example.dailyplanner.R;
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
    private ArrayList<TaskModelObserver> observers = new ArrayList<>();

    public TaskModel(int taskId, int dayId, TaskFieldView view){
        setView(view);
        this.id = taskId;
        this.dayId = dayId;
        task = "";
        isDone = false;
    }

    public TaskModel(int id, String task, boolean isDone, int dayId){
        this.id = id;
        this.dayId = dayId;
        this.task = task;
        this.isDone = isDone;
    }

    public void setView(TaskFieldView view) {
        this.view = view;
        PartTaskFieldBinding binding = PartTaskFieldBinding.bind(view);
        binding.editField.setText(task);
        binding.removeButton.setOnClickListener(v -> notifyByRemove());
        binding.editField.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String newTask = binding.editField.getText().toString();
                if (!newTask.equals(task)){
                    task = newTask;
                    isChanged = true;
                }
            }
        });
        notifyByCheckboxChanged(binding);
        binding.checkbox.setOnClickListener(v -> {
            isChanged = true;
            isDone = !isDone;
            notifyByCheckboxChanged(binding);
        });
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
            Log.w("AAA", "notify");
            observer.changeCheckbox(binding, isDone);
        }
    }
}
