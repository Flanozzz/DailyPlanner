package com.example.dailyplanner.Interfaces.Observers;

import com.example.dailyplanner.Model.DayModel;
import com.example.dailyplanner.Model.TaskModel;

import java.util.ArrayList;

public interface DayObserver {
    void handleEvent(ArrayList<TaskModel> tasks);
}
