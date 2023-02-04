package com.example.dailyplanner.Interfaces.Observers;

public interface DayObserved {
    void addObserver(DayObserver observer);

    void removeObserver(DayObserver observer);

    void notifyObservers();
}
