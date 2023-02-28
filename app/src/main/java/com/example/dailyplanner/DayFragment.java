package com.example.dailyplanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Paint;
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
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.dailyplanner.Factories.DayViewModelFactory;
import com.example.dailyplanner.Interfaces.Observers.DayObserver;
import com.example.dailyplanner.Interfaces.Observers.TaskModelObserver;
import com.example.dailyplanner.Model.DayModel;
import com.example.dailyplanner.Model.TaskModel;
import com.example.dailyplanner.ViewModel.DayViewModel;
import com.example.dailyplanner.Views.ControlledScrollView;
import com.example.dailyplanner.databinding.FragmentDayBinding;
import com.example.dailyplanner.databinding.PartTaskFieldBinding;

import java.util.ArrayList;


public class DayFragment extends Fragment implements DayObserver, TaskModelObserver {
    private static final String ARG_DAY_PARAM = "dayModel";

    private FragmentDayBinding binding;
    private DayModel dayModel;
    private Button addTaskBtn;
    private View viewAboveAddTaskBtn;
    private View highestLine;
    private Button frontPanel;
    private ControlledScrollView scrollContainer;
    private RelativeLayout tasksContainer;
    private ArrayList<TaskModel> tasks = new ArrayList<TaskModel>();
    private DayViewModel viewModel;
    private TaskListMover taskListMover;
    private int maxTaskViewId = 1;
    private int minOrderInLayer = 999999;

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
                (this, new DayViewModelFactory(getContext(), dayModel)).get(DayViewModel.class);
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
        viewAboveAddTaskBtn = binding.lineAboveAddBtn;
        highestLine = binding.highestLine;
        tasksContainer = binding.tasksContainer;
        scrollContainer = binding.scrollContainer;
        frontPanel = binding.frontPanel;

        binding.mainLayout.setOnTouchListener((view, event) -> {
            clearFocusOnTouch(view, event);
            return true;
        });

        binding.addTaskBtn.setOnClickListener(view -> {
            createAndLoadTaskView();
        });

        loadTasksViews();
        int doneTasksCount = (int)tasks.stream().filter(t -> t.isDone()).count();
        Log.w("AAA", doneTasksCount + "");
        taskListMover = new TaskListMover(tasks, highestLine,
                viewAboveAddTaskBtn, scrollContainer, frontPanel, doneTasksCount);
        return binding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.saveChanges();
    }

    private void clearFocusOnTouch(View view, MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_MOVE){
            View currentFocus = requireActivity().getCurrentFocus();
            if (currentFocus != null) {
                InputMethodManager imm = (InputMethodManager)getActivity()
                        .getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                currentFocus.clearFocus();
            }
        }
    }

    private void createAndLoadTaskView(){
        int prevViewId = tasks.size() == 0 ?
                binding.highestLine.getId() : tasks.get(tasks.size() - 1).getView().getId();
        TaskFieldView newTask = createTaskView(binding.highestLine.getId());

        viewModel.addTask(newTask, dayModel.getId(), tasks.size(), this);
        View followingView = tasks.size() == 1 ? viewAboveAddTaskBtn :
                tasks.get(0).getView();
        putViewBelowViewId(followingView, newTask.getId());
        //putAddTaskButtonAfterLastTask();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            scrollContainer.scrollToDescendant(addTaskBtn);
        }
    }

    private void loadTasksViews(){
        int prevTaskViewId = -1;
        for (TaskModel taskModel: tasks){
            int prevViewId = prevTaskViewId == -1 ?
                    binding.highestLine.getId() : prevTaskViewId;
            TaskFieldView newTaskView = createTaskView(prevViewId);
            prevTaskViewId = newTaskView.getId();
            taskModel.addObserver(this);
            taskModel.setView(newTaskView);
        }
        putAddTaskButtonAfterLastTask();
    }

    @SuppressLint({"ResourceType", "ClickableViewAccessibility"})
    private TaskFieldView createTaskView(int prevViewId){
        TaskFieldView newTask = new TaskFieldView(getContext());
        tasksContainer.addView(newTask);
        putViewBelowViewId(newTask, prevViewId);
        maxTaskViewId++;
        int newTaskId = maxTaskViewId;
        newTask.setId(newTaskId);
        newTask.getBinding().editField.setOnTouchListener((view, event) ->
                taskListMover.dragEndDropTasksEvent(newTask, event));
        newTask.getBinding().editField.setOnLongClickListener(view -> {

            taskListMover.startMove(newTask);
            return true;
        });
        newTask.getBinding().checkbox.setOnClickListener(view -> {
            Log.w("AAA", "check");
        });
        return newTask;
    }

    private void putAddTaskButtonAfterLastTask(){
        if(tasks.size() == 0){
            return;
        }
        putViewBelowViewId(viewAboveAddTaskBtn, tasks.get(tasks.size() - 1).getView().getId());
    }

    private void putViewBelowViewId(View view, int id){
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams)view.getLayoutParams();
        params.addRule(RelativeLayout.BELOW, id);
        view.setLayoutParams(params);
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
//            lastTaskViewId = tasks.get(currTaskModelIndex - 1).getView().getId();
            putViewBelowViewId(viewAboveAddTaskBtn, tasks.get(currTaskModelIndex - 1).getView().getId());
        }
        else if(currTaskModelIndex != 0){
            TaskFieldView prevTaskView = tasks.get(currTaskModelIndex - 1).getView();
            TaskFieldView nextTaskView = tasks.get(currTaskModelIndex + 1).getView();

            putViewBelowViewId(nextTaskView, prevTaskView.getId());
        }
        else { //currTaskModelIndex == 0
            if(tasks.size() > 1){
                TaskFieldView nextTaskView = tasks.get(currTaskModelIndex + 1).getView();
                putViewBelowViewId(nextTaskView, binding.highestLine.getId());
            }
            else{
                putViewBelowViewId(viewAboveAddTaskBtn, binding.highestLine.getId());
            }
        }
        tasksContainer.removeView(currTaskView);
        viewModel.removeTask(taskModel);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void changeTaskStatus(TaskFieldView changedTask, boolean isDone, String task) {
        Drawable background;
        EditText editField = changedTask.getBinding().editField;
        editField.setClickable(!isDone);
        editField.setCursorVisible(!isDone);
        editField.setFocusable(!isDone);
        editField.setFocusableInTouchMode(!isDone);
        if(isDone){
            background = getResources()
                    .getDrawable(R.drawable.rectangle_for_checkbox_active, null);

            editField.setText("");
            editField.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            editField.setText(task);
        }
        else{
            background = getResources()
                    .getDrawable(R.drawable.rectangle_for_checkbox_inactive, null);
            editField.setText("");
            editField.getPaint().setFlags(0);
            editField.setText(task);
        }
        changedTask.getBinding().checkbox.setBackground(background);
        if(taskListMover != null){
            if(isDone){
                taskListMover.moveDown(changedTask);
            }
            else{
                taskListMover.moveUp(changedTask);
            }
        }
    }
}