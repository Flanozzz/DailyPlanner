package com.example.dailyplanner.Interfaces;

import com.example.dailyplanner.Model.DayModel;

public interface Navigator {
     void goToDayFragment(DayModel dayModel);
     void backToDayList();
}
