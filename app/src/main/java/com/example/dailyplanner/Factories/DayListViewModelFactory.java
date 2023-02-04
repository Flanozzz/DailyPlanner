package com.example.dailyplanner.Factories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.dailyplanner.ViewModel.DayListViewModel;

public class DayListViewModelFactory implements ViewModelProvider.Factory {

    private Context context;

    public DayListViewModelFactory(Context ctx){
        context = ctx;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T)new DayListViewModel(context);
    }
}
