package com.example.dailyplanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.dailyplanner.Factories.DayViewModelFactory;
import com.example.dailyplanner.Interfaces.Observers.DayObserver;
import com.example.dailyplanner.Interfaces.Observers.TaskModelObserver;
import com.example.dailyplanner.Model.DayModel;
import com.example.dailyplanner.Model.TaskModel;
import com.example.dailyplanner.ViewModel.DayViewModel;
import com.example.dailyplanner.databinding.FragmentDayBinding;
import com.example.dailyplanner.databinding.PartTaskFieldBinding;

import java.util.ArrayList;


public class DayFragment extends Fragment implements DayObserver, TaskModelObserver {
    private static final String ARG_DAY_PARAM = "dayModel";

    private FragmentDayBinding binding;
    private DayModel dayModel;
    private Button addTaskBtn;
    private RelativeLayout tasksContainer;
    private ArrayList<TaskModel> tasks = new ArrayList<TaskModel>();
    private DayViewModel viewModel;
    private int lastTaskViewId = 0;

    public DayFragment() {
        // Required empty public constructor
    }

    public static DayFragment newInstance(DayModel dayModel) {
        DayFragment fragment = new DayFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DAY_PARAM, dayModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dayModel = (DayModel)getArguments().getSerializable(ARG_DAY_PARAM);
        }
        viewModel = new ViewModelProvider
                (this, new DayViewModelFactory(getContext(), dayModel.getId())).get(DayViewModel.class);
        viewModel.addObserver(this);
        viewModel.loadTasks();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDayBinding.inflate(inflater, container, false);
        binding.infoText.setText(dayModel.getStringDate());
        addTaskBtn = binding.addTaskBtn;
        tasksContainer = binding.tasksContainer;

        binding.mainLayout.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    View view = getActivity().getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        view.clearFocus();
                    }
                }
                return true;
            }
        });

        binding.addTaskBtn.setOnClickListener(view -> {
            TaskFieldView newTask = createTaskView();
            viewModel.addTask(newTask, dayModel.getId(), this);
            putAddTaskButtonAfterLastTask();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                binding.scrollContainer.scrollToDescendant(addTaskBtn);
            }
        });


        for (TaskModel taskModel: tasks){
            TaskFieldView newTaskView = createTaskView();
            taskModel.addObserver(this);
            taskModel.setView(newTaskView);
        }
        putAddTaskButtonAfterLastTask();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.saveChanges();
    }

    @SuppressLint("ResourceType")
    private TaskFieldView createTaskView(){
        TaskFieldView newTask = new TaskFieldView(getContext());
        tasksContainer.addView(newTask);

        if(lastTaskViewId > 0){
            RelativeLayout.LayoutParams newTaskParams = (RelativeLayout.LayoutParams)newTask.getLayoutParams();
            newTaskParams.addRule(RelativeLayout.BELOW, lastTaskViewId);
            newTask.setLayoutParams(newTaskParams);
        }
        else{
            RelativeLayout.LayoutParams newTaskParams = (RelativeLayout.LayoutParams)newTask.getLayoutParams();
            newTaskParams.addRule(RelativeLayout.BELOW, binding.highestLine.getId());
            newTask.setLayoutParams(newTaskParams);
        }
        lastTaskViewId+=1;
        newTask.setId(lastTaskViewId);
        return newTask;
    }

    private void putAddTaskButtonAfterLastTask(){
        if(tasks.size() == 0){
            return;
        }
        int lastTaskId = tasks.get(tasks.size() - 1)
                .getView()
                .getId();
        RelativeLayout.LayoutParams addTaskBtnParams =
                (RelativeLayout.LayoutParams)addTaskBtn.getLayoutParams();
        addTaskBtnParams.addRule(RelativeLayout.BELOW, lastTaskViewId);
    }

    @Override
    public void handleEvent(ArrayList<TaskModel> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void removeTask(TaskModel taskModel) {
        if(tasks.size() == 0){
            return;
        }
        int currTaskModelIndex = tasks.indexOf(taskModel);
        TaskFieldView currTaskView = taskModel.getView();
        if (currTaskModelIndex == tasks.size()-1 && tasks.size() != 1){
            lastTaskViewId = tasks.get(currTaskModelIndex - 1).getView().getId();
            putAddTaskButtonAfterLastTask();
        }
        else if(currTaskModelIndex != 0){
            TaskFieldView prevTaskView = tasks.get(currTaskModelIndex - 1).getView();
            TaskFieldView nextTaskView = tasks.get(currTaskModelIndex + 1).getView();

            RelativeLayout.LayoutParams nextTaskParams =
                    (RelativeLayout.LayoutParams)nextTaskView.getLayoutParams();
            nextTaskParams.addRule(RelativeLayout.BELOW, prevTaskView.getId());
            nextTaskView.setLayoutParams(nextTaskParams);
        }
        else { //currTaskModelIndex == 0
            if(tasks.size() > 1){
                TaskFieldView nextTaskView = tasks.get(currTaskModelIndex + 1).getView();
                RelativeLayout.LayoutParams nextTaskParams =
                        (RelativeLayout.LayoutParams)nextTaskView.getLayoutParams();
                nextTaskParams.addRule(RelativeLayout.BELOW, binding.highestLine.getId());
                nextTaskView.setLayoutParams(nextTaskParams);
            }
            else{
                RelativeLayout.LayoutParams addTaskBtnParams =
                        (RelativeLayout.LayoutParams)addTaskBtn.getLayoutParams();
                addTaskBtnParams.addRule(RelativeLayout.BELOW, binding.highestLine.getId());
            }
        }
        tasksContainer.removeView(currTaskView);
        viewModel.removeTask(taskModel);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void changeCheckbox(PartTaskFieldBinding taskBinding, boolean isDone) {
        Log.w("AAA", "changeCheckbox");
        Drawable background;
        if(isDone){
            background = getResources()
                    .getDrawable(R.drawable.rectangle_for_checkbox_active, null);
        }
        else{
            background = getResources()
                    .getDrawable(R.drawable.rectangle_for_checkbox_inactive, null);
        }
        taskBinding.checkbox.setBackground(background);
    }
}