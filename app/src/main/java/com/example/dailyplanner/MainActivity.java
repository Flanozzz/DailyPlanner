package com.example.dailyplanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.dailyplanner.Factories.MainViewModelFactory;
import com.example.dailyplanner.Interfaces.DayModelObserver;
import com.example.dailyplanner.Interfaces.DaysObserver;
import com.example.dailyplanner.Model.DayModel;
import com.example.dailyplanner.ViewModel.MainViewModel;
import com.example.dailyplanner.databinding.ActivityMainBinding;

import java.sql.Time;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements DaysObserver, DayModelObserver {

    private MainViewModel viewModel;
    private ActivityMainBinding binding;
    private ArrayList<DayModel> days = new ArrayList<DayModel>();
    private Button addDayBtn;
    private ScrollView dayListScrollContainer;
    private RelativeLayout dayListLayout;
    private int lastDayViewId = 0;
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
        viewModel.loadDays();

        //Views Loading
        addDayBtn = binding.addDayBtn;
        dayListLayout = binding.dayListLayout;
        dayListScrollContainer = binding.dayListScrollContainer;


        addDayBtn.setOnClickListener((view) -> {
            DayView newDayView = createDayView();
            long unixDate;
            if(days.size() > 0){
                int msInDay = 86400000;
                unixDate = days.get(days.size() - 1).getUnixDate() + msInDay;
            }
            else{
                unixDate = System.currentTimeMillis();
            }
            viewModel.saveDay(unixDate, newDayView, this);  //year
            putAddDayButtonAfterLastDay();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                dayListScrollContainer.scrollToDescendant(addDayBtn);
            }
        });
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onResume() {
        super.onResume();
        if(isFirstStart)
        {
            for (DayModel dayModel: days){
                DayView newDayView = createDayView();
                dayModel.setView(newDayView);
                dayModel.addObserver(this);
            }
            putAddDayButtonAfterLastDay();
            isFirstStart = false;
        }
    }

    private DayView createDayView(){
        DayView newDay = new DayView(this);
        dayListLayout.addView(newDay);

        if(days.size() > 0){
            RelativeLayout.LayoutParams newDayParams = (RelativeLayout.LayoutParams)newDay.getLayoutParams();
            newDayParams.addRule(RelativeLayout.BELOW, lastDayViewId);
            newDay.setLayoutParams(newDayParams);
        }
        lastDayViewId+=1;
        newDay.setId(lastDayViewId);
        return newDay;
    }

    private void putAddDayButtonAfterLastDay(){
        if(days.size() == 0){
            return;
        }
        RelativeLayout.LayoutParams addDayBtnParams =
                (RelativeLayout.LayoutParams)addDayBtn.getLayoutParams();
        addDayBtnParams.addRule(RelativeLayout.BELOW, lastDayViewId);
    }

    @Override
    public void handleEvent(ArrayList<DayModel> days) {
        this.days = days;
    }

    @Override
    public void removeDay(DayModel dayModel) {
        if(days.size() == 0){
            return;
        }
        int currDayModelIndex = days.indexOf(dayModel);
        DayView currDayView = dayModel.getView();
        if (currDayModelIndex == days.size()-1 && days.size() != 1){
            lastDayViewId = days.get(currDayModelIndex - 1).getView().getId();
            putAddDayButtonAfterLastDay();
        }
        else if(currDayModelIndex != 0){
            DayView prevDayView = days.get(currDayModelIndex - 1).getView();
            DayView nextDayView = days.get(currDayModelIndex + 1).getView();

            RelativeLayout.LayoutParams nextDayParams = (RelativeLayout.LayoutParams)nextDayView.getLayoutParams();
            nextDayParams.addRule(RelativeLayout.BELOW, prevDayView.getId());
            nextDayView.setLayoutParams(nextDayParams);
        }
        dayListLayout.removeView(currDayView);
        viewModel.removeDay(dayModel);
    }

    @Override
    public void showInfo(DayModel dayModel) {
        Toast.makeText(this, dayModel.getStringDate(), Toast.LENGTH_SHORT).show();
    }
}