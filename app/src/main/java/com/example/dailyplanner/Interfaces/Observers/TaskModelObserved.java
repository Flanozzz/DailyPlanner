package com.example.dailyplanner.Interfaces.Observers;

import com.example.dailyplanner.databinding.PartTaskFieldBinding;

public interface TaskModelObserved {
    void addObserver(TaskModelObserver observer);

    void removeObserver(TaskModelObserver observer);

    void notifyByRemove();

    void notifyByCheckboxChanged(PartTaskFieldBinding binding);
}