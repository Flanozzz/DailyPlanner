package com.example.dailyplanner.Interfaces;

public interface DaysObserved {
    void addObserver(DaysObserver observer);

    void removeObserver(DaysObserver observer);

    void notifyObservers();
}
