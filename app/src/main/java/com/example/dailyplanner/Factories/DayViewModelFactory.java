package com.example.dailyplanner.Factories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.dailyplanner.Model.DayModel;
import com.example.dailyplanner.ViewModel.DayViewModel;

public class DayViewModelFactory implements ViewModelProvider.Factory {

    private Context context;
    private DayModel dayModel;

    public DayViewModelFactory(Context ctx, DayModel dayModel){
        context = ctx;
        this.dayModel = dayModel;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T)new DayViewModel(context, dayModel);
    }
}
