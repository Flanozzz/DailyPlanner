package com.example.dailyplanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.dailyplanner.R;

import java.net.ConnectException;

@SuppressLint("ViewConstructor")
public class DayView extends LinearLayout {

    public DayView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public DayView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr, 0);
        init();
    }

    public DayView(Context context, AttributeSet attrs){
        super(context, attrs, 0);
        init();
    }

    public DayView(Context context){
        super(context, null);
        init();
    }

    private void init(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.part_day, this, true);
        LayoutParams layoutParams = new LayoutParams
                (LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParams);
    }
}
