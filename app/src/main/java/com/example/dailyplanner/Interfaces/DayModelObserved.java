package com.example.dailyplanner.Interfaces;

public interface DayModelObserved {
    void addObserver(DayModelObserver observer);
    void removeObserver(DayModelObserver observer);
    void notifyByRemove();
    void notifyByShow();
}
