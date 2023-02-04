package com.example.dailyplanner.Interfaces.Observers;

import com.example.dailyplanner.Model.DayModel;

public interface DayModelObserver {
    void removeDay(DayModel dayModel);
    void showInfo(DayModel dayModel);
    void show(DayModel dayModel);
}
