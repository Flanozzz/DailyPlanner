package com.example.dailyplanner.Interfaces;

public interface Observed {
    void addObserver(DaysObserver observer);

    void removeObserver(DaysObserver observer);

    void notifyObservers();
}
