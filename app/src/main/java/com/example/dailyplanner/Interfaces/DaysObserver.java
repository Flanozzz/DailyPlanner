package com.example.dailyplanner.Interfaces;

import com.example.dailyplanner.Model.DayModel;

import java.util.ArrayList;

public interface DaysObserver {
    void handleEvent(ArrayList<DayModel> days);
}
