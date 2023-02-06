package com.example.dailyplanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintSet;
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
    private TaskListMover taskListMover;
    private int lastTaskViewId = 0;
    private int createdTasksCount = 0;
    private float taskHeight = 0;

//    private float movableTaskBaseY = 0;
//    private float movableTaskDownY = 0;
//    private TaskModel prevByMovableTask = null;
//    private TaskModel nextByMovableTask = null;
//    private int movableTaskIndex = -1;
//    private float minYpos = 9999999;
//    private float maxYpos = -9999999;
//    private boolean isMovable = false;

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
        taskListMover = new TaskListMover(tasks);
        taskHeight = new TaskFieldView(getContext()).getHeight();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDayBinding.inflate(inflater, container, false);
        binding.infoText.setText(dayModel.getStringDate());
        addTaskBtn = binding.addTaskBtn;
        tasksContainer = binding.tasksContainer;

        binding.mainLayout.setOnTouchListener((view, event) -> {
            clearFocusOnTouch(view, event);
            return true;
        });

        binding.addTaskBtn.setOnClickListener(view -> {
            createAndLoadTaskView();
        });

        loadTasksViews();
        putAddTaskButtonAfterLastTask();
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
        TaskFieldView newTask = createTaskView();

        viewModel.addTask(newTask, dayModel.getId(), this);
        putAddTaskButtonAfterLastTask();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.scrollContainer.scrollToDescendant(addTaskBtn);
        }
    }

    private void loadTasksViews(){
        for (TaskModel taskModel: tasks){
            TaskFieldView newTaskView = createTaskView();
            taskModel.addObserver(this);
            taskModel.setView(newTaskView);
        }
    }

    @SuppressLint({"ResourceType", "ClickableViewAccessibility"})
    private TaskFieldView createTaskView(){
        TaskFieldView newTask = new TaskFieldView(getContext());
        tasksContainer.addView(newTask);
//        newTask.setY(newTask.getY() + 183 * createdTasksCount);
//        tasksContainer.setMinimumHeight(183 * createdTasksCount + 183);
//        Log.w("AAA", newTask.getY() + " " + newTask.getHeight() + " " + createdTasksCount);
//        createdTasksCount++;
        if(lastTaskViewId > 0 && tasks.size() > 0){
            putViewBelowViewId(newTask, lastTaskViewId);
        }
        else{
            putViewBelowViewId(newTask, binding.highestLine.getId());
        }
        lastTaskViewId+=1;
        newTask.setId(lastTaskViewId);
        newTask.getBinding().editField.setOnTouchListener((view, event) ->
                taskListMover.dragEndDropEvent(newTask, event));
        newTask.getBinding().editField.setOnLongClickListener(view -> {
            newTask.bringToFront();
            taskListMover.makeMovable();
            return true;
        });
        return newTask;
    }

//     private boolean dragEndDropEvent(TaskFieldView movableTask, MotionEvent event){
//        float newMinYpos = tasks.get(0).getView().getY();
//        float newMaxYpos = tasks.get(tasks.size() - 1).getView().getY();
//        if(newMinYpos < minYpos){
//            minYpos = newMinYpos;
//        }
//        if(newMaxYpos > maxYpos){
//            maxYpos = newMaxYpos;
//        }
//        switch (event.getActionMasked()){
//            case MotionEvent.ACTION_DOWN:
//                movableTaskDownY = event.getY();
//                movableTaskBaseY = movableTask.getY();
//                TaskModel movableTaskModel = null;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                    movableTaskModel =
//                            tasks.stream().filter(taskModel ->
//                                    taskModel.getView()
//                                    .equals(movableTask))
//                                    .findAny()
//                                    .get();
//                }
//                assert movableTaskModel != null;
//                movableTaskIndex = tasks.indexOf(movableTaskModel);
////                Log.w("AAA", movableTaskIndex + "");
//                if(movableTaskIndex > 0){
//                    prevByMovableTask = tasks.get(movableTaskIndex - 1);
////                    Log.w("AAA", prevByMovableTask.getView().getY() + "");
//                }
//                if(movableTaskIndex < tasks.size()-1){
//                    nextByMovableTask = tasks.get(movableTaskIndex + 1);
////                    Log.w("AAA", nextByMovableTask.getView().getY() + "");
//                }
//                break;
//            case MotionEvent.ACTION_MOVE:
//                if(!isMovable){
//                    break;
//                }
//                float movedY = event.getY();
//                float distance = movedY - movableTaskDownY;
//                float nextYpos = movableTask.getY() + distance;
//                float taskHeight = movableTask.getHeight();
//                if(nextYpos < minYpos){
//                    nextYpos = minYpos;
//                }
//                if(nextYpos > maxYpos){
//                    nextYpos = maxYpos;
//                }
//                if(prevByMovableTask != null && nextYpos <= prevByMovableTask.getView().getY()){
//                    float currPrevTaskY = prevByMovableTask.getView().getY();
//                    prevByMovableTask.getView().setY(currPrevTaskY + taskHeight);
//                    tasks.set(movableTaskIndex - 1, tasks.get(movableTaskIndex));
//                    tasks.set(movableTaskIndex, prevByMovableTask);
//                    movableTaskIndex--;
//                    movableTaskBaseY = currPrevTaskY;
//                    if(movableTaskIndex == 0){
//                        prevByMovableTask = null;
//                    }
//                    else{
//                        nextByMovableTask = prevByMovableTask;
//                        prevByMovableTask = tasks.get(movableTaskIndex - 1);
//                    }
//                    Log.w("AAA", "prevTask");
//                }
//                if(nextByMovableTask != null && nextYpos >= nextByMovableTask.getView().getY()){
//                    float currNextTaskY = nextByMovableTask.getView().getY();
//                    nextByMovableTask.getView().setY(currNextTaskY - taskHeight);
//                    tasks.set(movableTaskIndex + 1, tasks.get(movableTaskIndex));
//                    tasks.set(movableTaskIndex, nextByMovableTask);
//                    movableTaskIndex++;
//                    movableTaskBaseY = currNextTaskY;
//                    if(movableTaskIndex == tasks.size() - 1){
//                        nextByMovableTask = null;
//                    }
//                    else{
//                        prevByMovableTask = nextByMovableTask;
//                        nextByMovableTask = tasks.get(movableTaskIndex + 1);
//                    }
//                    Log.w("AAA", "nextTask");
//                }
//                movableTask.setY(nextYpos);
//
//                break;
//            case MotionEvent.ACTION_UP:
//                movableTask.setY(movableTaskBaseY);
//                movableTaskIndex = -1;
//                prevByMovableTask = null;
//                nextByMovableTask = null;
//                return true;
//        }
//        return false;
//    }

    private void putAddTaskButtonAfterLastTask(){
        if(tasks.size() == 0){
            return;
        }
        putViewBelowViewId(addTaskBtn, lastTaskViewId);
    }

    private void putViewBelowViewId(View view, int id){
        RelativeLayout.LayoutParams addTaskBtnParams =
                (RelativeLayout.LayoutParams)view.getLayoutParams();
        addTaskBtnParams.addRule(RelativeLayout.BELOW, id);
        view.setLayoutParams(addTaskBtnParams);
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

            putViewBelowViewId(nextTaskView, prevTaskView.getId());
        }
        else { //currTaskModelIndex == 0
            if(tasks.size() > 1){
                TaskFieldView nextTaskView = tasks.get(currTaskModelIndex + 1).getView();
                putViewBelowViewId(nextTaskView, binding.highestLine.getId());
            }
            else{
                putViewBelowViewId(addTaskBtn, binding.highestLine.getId());
            }
        }
        tasksContainer.removeView(currTaskView);
        viewModel.removeTask(taskModel);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void changeTaskStatus(PartTaskFieldBinding taskBinding, boolean isDone, String task) {
        Drawable background;
        EditText editField = taskBinding.editField;
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
        taskBinding.checkbox.setBackground(background);
    }
}