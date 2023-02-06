package com.example.dailyplanner;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.dailyplanner.databinding.PartTaskFieldBinding;

public class TaskFieldView extends RelativeLayout {

    private PartTaskFieldBinding binding;

    public TaskFieldView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public TaskFieldView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr, 0);
        init();
    }

    public TaskFieldView(Context context, AttributeSet attrs){
        super(context, attrs, 0);
        init();
    }

    public TaskFieldView(Context context){
        super(context, null);
        init();
    }

    private void init(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.part_task_field, this, true);
        LayoutParams layoutParams = new LayoutParams
                (LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParams);
        binding = PartTaskFieldBinding.bind(this);
    }

    public PartTaskFieldBinding getBinding() {
        return binding;
    }
}
