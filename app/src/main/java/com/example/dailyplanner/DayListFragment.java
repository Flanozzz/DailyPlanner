package com.example.dailyplanner;

import static androidx.databinding.DataBindingUtil.setContentView;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.dailyplanner.Factories.DayListViewModelFactory;
import com.example.dailyplanner.Interfaces.Observers.DayModelObserver;
import com.example.dailyplanner.Interfaces.Observers.DaysObserver;
import com.example.dailyplanner.Model.DayModel;
import com.example.dailyplanner.ViewModel.DayListViewModel;
import com.example.dailyplanner.databinding.FragmentDayListBinding;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DayListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DayListFragment extends Fragment implements DaysObserver, DayModelObserver {

    private DayListViewModel viewModel;
    private FragmentDayListBinding binding;
    private ArrayList<DayModel> days = new ArrayList<DayModel>();
    private Button addDayBtn;
    private ScrollView dayListScrollContainer;
    private RelativeLayout dayListLayout;
    private int lastDayViewId = 0;
    private boolean isFirstStart = true;

    public DayListFragment() {
        // Required empty public constructor
    }

    public static DayListFragment newInstance() {
        DayListFragment fragment = new DayListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ViewModel Initialize
        viewModel = new ViewModelProvider
                (this, new DayListViewModelFactory(getContext())).get(DayListViewModel.class);
        viewModel.addObserver(this);
        viewModel.loadDays();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDayListBinding.inflate(inflater, container, false);
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

        for (DayModel dayModel: days){
            DayView newDayView = createDayView();
            dayModel.setView(newDayView);
            dayModel.addObserver(this);
        }
        putAddDayButtonAfterLastDay();
        isFirstStart = false;

        return binding.getRoot();
    }

    private DayView createDayView(){
        DayView newDay = new DayView(getContext());
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
        Toast.makeText(getContext(), dayModel.getStringDate(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void show(DayModel dayModel) {
        Objects.requireNonNull(NavigatorInstance.get(this)).goToDayFragment(dayModel);
    }
}