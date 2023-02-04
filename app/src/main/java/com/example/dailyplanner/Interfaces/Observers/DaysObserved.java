package com.example.dailyplanner.Interfaces.Observers;

public interface DaysObserved {
    void addObserver(DaysObserver observer);

    void removeObserver(DaysObserver observer);

    void notifyObservers();
}
