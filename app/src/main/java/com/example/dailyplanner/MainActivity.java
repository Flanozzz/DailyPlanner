package com.example.dailyplanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.example.dailyplanner.Interfaces.Navigator;
import com.example.dailyplanner.Model.DayModel;
import com.example.dailyplanner.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements Navigator {

    private ActivityMainBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if(savedInstanceState == null){
            DayListFragment fragment = DayListFragment.newInstance();

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void goToDayFragment(DayModel dayModel) {
        DayFragment fragment = DayFragment.newInstance(dayModel);
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void backToDayList() {

    }

    @Override
    public void onBackPressed() {
        DayListFragment fragment = DayListFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}