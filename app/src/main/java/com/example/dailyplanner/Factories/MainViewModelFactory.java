package com.example.dailyplanner.Factories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.dailyplanner.ViewModel.MainViewModel;


public class MainViewModelFactory implements ViewModelProvider.Factory {

    private Context context;

    public MainViewModelFactory(Context ctx){
        context = ctx;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T)new MainViewModel(context);
    }
}
