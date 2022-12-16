package com.example.dailyplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.example.dailyplanner.Factories.MainViewModelFactory;
import com.example.dailyplanner.Interfaces.DaysObserver;
import com.example.dailyplanner.Model.DayModel;
import com.example.dailyplanner.ViewModel.MainViewModel;
import com.example.dailyplanner.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements DaysObserver {

    private MainViewModel viewModel;
    private ActivityMainBinding binding;
    private ArrayList<DayModel> days = new ArrayList<DayModel>();
    private Button addDayBtn;
    private ScrollView dayListScrollContainer;
    private RelativeLayout dayListLayout;
    private int lastDayViewId;
    private boolean isFirstStart = true;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //ViewModel Initialize
        viewModel = new ViewModelProvider
                (this, new MainViewModelFactory(this)).get(MainViewModel.class);
        viewModel.addObserver(this);
        //viewModel.loadDays(this);
        viewModel.getLastDayId().observe(this, (id) -> {
            lastDayViewId = id;
        });

        //Views Loading
        addDayBtn = binding.addDayBtn;
        dayListLayout = binding.dayListLayout;
        dayListScrollContainer = binding.dayListScrollContainer;


        addDayBtn.setOnClickListener((view) -> {
            createDay();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                dayListScrollContainer.scrollToDescendant(addDayBtn);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isFirstStart)
        {
            createDay();
            createDay();
            createDay();
            createDay();
            createDay();
            createDay();
            isFirstStart = false;
        }
    }

    private void createDay(){
        DayView newDay = new DayView(this);
        dayListLayout.addView(newDay);

        if(days.size() > 0){
            RelativeLayout.LayoutParams newDayParams = (RelativeLayout.LayoutParams)newDay.getLayoutParams();
            newDayParams.addRule(RelativeLayout.BELOW, lastDayViewId);
            newDay.setLayoutParams(newDayParams);
        }

        viewModel.saveDay(newDay, 13, 12); //saveDay меняет занчение lastDayId
        newDay.setId(lastDayViewId);
        RelativeLayout.LayoutParams addDayBtnParams =
                (RelativeLayout.LayoutParams)addDayBtn.getLayoutParams();
        addDayBtnParams.addRule(RelativeLayout.BELOW, lastDayViewId);
    }

    @Override
    public void handleEvent(ArrayList<DayModel> days) {
        this.days = days;
    }
}