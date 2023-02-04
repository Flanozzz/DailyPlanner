package com.example.dailyplanner.Interfaces.Observers;

public interface DayModelObserved {
    void addObserver(DayModelObserver observer);
    void removeObserver(DayModelObserver observer);
    void notifyByRemove();
    void notifyByShowInfo();
    void notifyByShow();
}
