package com.example.dailyplanner;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.example.dailyplanner.Interfaces.Navigator;

public class NavigatorInstance {
    private NavigatorInstance(){}
    public static Navigator get(Fragment fragment){
        Activity activity = fragment.requireActivity();
        if(activity instanceof Navigator){
            return (Navigator)activity;
        }
        return null;
    }
}
