package com.example.dailyplanner.Interfaces.Observers;

import com.example.dailyplanner.Model.TaskModel;
import com.example.dailyplanner.TaskFieldView;
import com.example.dailyplanner.databinding.PartTaskFieldBinding;

public interface TaskModelObserver {
    void removeTask(TaskModel taskModel);

    void changeTaskStatus(TaskFieldView binding, boolean isDone, String task);
}
