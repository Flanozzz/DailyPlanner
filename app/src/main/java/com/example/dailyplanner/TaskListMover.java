package com.example.dailyplanner;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.example.dailyplanner.Model.TaskModel;
import com.example.dailyplanner.Views.ControlledScrollView;
import com.example.dailyplanner.databinding.FragmentDayBinding;

import java.util.ArrayList;

public class TaskListMover {
    private ArrayList<TaskModel> tasks;
    private float movableTaskBaseY = 0;
    private float movableTaskDownY = 0;
    private int movableTaskIndex = -1;
    private float minYpos = 9999999;
    private float maxYpos = 9999999;
    private boolean isMovable = false;
    private ArrayList<TaskModel> crossedTasks = new ArrayList<>();
    private int direction = 0;
    private int firstCrossedTaskIndex = -1;
    private int lastCrossedTaskIndex = -1;
    private boolean isFirstMove = false;
    private int prevTasksCount = -1;
    private View highestView;
    private View viewByLastTask;
    private ControlledScrollView scrollViewContainer;
    private Button disablesClickPanel;
    private int movedDownCount;

    private final int onMoveTaskAnimation = 300;
    private final int onMoveTasksAnimation = 80;

    public TaskListMover(ArrayList<TaskModel> tasksList,
                         View highestView, View viewByLastTask,
                         ControlledScrollView scrollViewContainer,
                         Button disablesClickPanel, int downedTasks){
        movedDownCount = downedTasks;
        tasks = tasksList;
        this.highestView = highestView;
        this.viewByLastTask = viewByLastTask;
        this.scrollViewContainer = scrollViewContainer;
        this.disablesClickPanel = disablesClickPanel;
    }

    public void moveDown(TaskFieldView movableTask){
        movedDownCount++;
        float movableTaskBaseY = movableTask.getY();
        float downY = tasks.get(tasks.size() - 1).getView().getY();
        int movableTaskIndex = getMovableTaskIndex(movableTask);
        float taskFieldHeight = tasks.get(0).getView().getHeight();
        TaskModel movableTaskModel = tasks.get(movableTaskIndex);
        movableTask.bringToFront();
        movableTask.animate()
                .setDuration(onMoveTaskAnimation)
                .y(downY)
                .withStartAction(() -> {
                    if(movableTaskIndex != tasks.size() - 1){
                        disablesClickPanel.setClickable(true);
                    }
                })
                .withEndAction(() -> {
                    for (int i = movableTaskIndex + 1; i < tasks.size(); i++){
                        int currI = i;
                        TaskModel currTask = tasks.get(i);
                        float baseY = currTask.getView().getY();
                        tasks.set(i - 1, currTask);
                        currTask.getView().animate()
                                .setDuration(onMoveTasksAnimation)
                                .translationYBy(-taskFieldHeight)
                                .withStartAction(() -> {
                                    if(currI == tasks.size() - 1){
                                        disablesClickPanel.setClickable(true);
                                    }
                                })
                                .withEndAction(() -> {
                                    currTask.getView().setY(baseY);
                                    if(currI == movableTaskIndex + 1){
                                        disablesClickPanel.setClickable(false);
                                        movableTask.setY(movableTaskBaseY);
                                        int highestTaskIndex = currI - 1;
                                        int highestViewId = highestTaskIndex == 0 ?
                                                        highestView.getId() : tasks.get(highestTaskIndex - 1).getView().getId();
                                        putViewBelowViewId
                                                (tasks.get(highestTaskIndex).getView(), highestViewId);
                                        putViewBelowViewId(movableTask, tasks.get(tasks.size() - 2).getView().getId());
                                        putViewBelowViewId(viewByLastTask, movableTask.getId());
                                    }
                                });
                    }
                    tasks.set(tasks.size() - 1, movableTaskModel);
                });
    }

    public void moveUp(TaskFieldView movableTask){
        float movableTaskBaseY = movableTask.getY();
        int highestDownedTaskIndex = tasks.size() - movedDownCount;
        float finalY = tasks.get(highestDownedTaskIndex).getView().getY();
        float taskFieldHeight = tasks.get(0).getView().getHeight();
        int movableTaskIndex = getMovableTaskIndex(movableTask);
        TaskModel movableTaskModel = tasks.get(movableTaskIndex);
        movableTask.bringToFront();
        movableTask.animate()
                .setDuration(onMoveTaskAnimation)
                .y(finalY)
                .withStartAction(() -> {
                    if(movableTaskIndex != highestDownedTaskIndex){
                        disablesClickPanel.setClickable(true);
                    }
                })
                .withEndAction(() -> {
                    for (int i = movableTaskIndex - 1; i >= highestDownedTaskIndex; i--){
                        int currI = i;
                        TaskModel currTask = tasks.get(i);
                        float baseY = currTask.getView().getY();
                        tasks.set(i + 1, currTask);
                        currTask.getView().animate()
                                .setDuration(onMoveTasksAnimation)
                                .translationYBy(taskFieldHeight)
                                .withStartAction(() -> {
                                    if(currI == movableTaskIndex - 1){
                                        disablesClickPanel.setClickable(true);
                                    }
                                })
                                .withEndAction(() -> {
                                    currTask.getView().setY(baseY);
                                    if(currI == movableTaskIndex - 1){
                                        disablesClickPanel.setClickable(false);
                                        movableTask.setY(movableTaskBaseY);

                                        int highestViewId = highestDownedTaskIndex == 0 ?
                                                highestView.getId() :
                                                tasks.get(highestDownedTaskIndex - 1)
                                                .getView()
                                                .getId();

                                        View lowestView = movableTaskIndex + 1 < tasks.size() ?
                                                tasks.get(movableTaskIndex + 1).getView() :
                                                viewByLastTask;

                                        putViewBelowViewId(movableTask, highestViewId);
                                        putViewBelowViewId
                                                (tasks.get(highestDownedTaskIndex + 1).getView(),
                                                        movableTask.getId());
                                        putViewBelowViewId(lowestView,
                                                tasks.get(movableTaskIndex).getView().getId());
                                    }
                                });
                    }
                    tasks.set(highestDownedTaskIndex, movableTaskModel);
                });
        movedDownCount--;
    }


    public void startMove(TaskFieldView movableTask){
        movableTask.bringToFront();
        movableTask.getBinding().mainLayout.setBackgroundColor(
                movableTask.getResources().getColor(R.color.backgroundLight0_5, null));
        setIsMovable(true);
    }

    public boolean dragEndDropTasksEvent(TaskFieldView movableTask, MotionEvent event){
        float taskHeight = movableTask.getHeight();
        float newMinYpos = tasks.get(0).getView().getY();
        float newMaxYpos = tasks.get(tasks.size() - 1).getView().getY();
        if(prevTasksCount != tasks.size()){
            minYpos = newMinYpos;
            maxYpos = newMaxYpos;
        }
        prevTasksCount = tasks.size();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                movableTaskDownY = event.getY();
                movableTaskBaseY = movableTask.getY();
                movableTaskIndex = getMovableTaskIndex(movableTask);
                if(movableTaskIndex == -1){
                    setIsMovable(false);
                    return false;
                }
                isFirstMove = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if(!isMovable || isFirstMove && Math.abs(movableTaskDownY - event.getY()) > (taskHeight / 2))
                {
                    movableTaskDownY = event.getY();
                    isFirstMove = false;
                    setIsMovable(false);
                    return false;
                }
                moveTask(event, movableTask, taskHeight);
                isFirstMove = false;
                break;
            case MotionEvent.ACTION_UP:
//                movableTask.setY(movableTaskBaseY);
                movableTask.getBinding().mainLayout.setBackgroundColor(
                        movableTask.getResources().getColor(R.color.background));
                if (!isMovable){
                    return false;
                }
                if(crossedTasks.size() == 0){
                    movableTask.setY(movableTaskBaseY);
                    setIsMovable(false);
                    return true;
                }
                movableTask.setY(tasks.get(lastCrossedTaskIndex).getView().getY());
                for(int i = firstCrossedTaskIndex;
                    i <= lastCrossedTaskIndex && direction > 0 || i >= lastCrossedTaskIndex && direction < 0;
                    i += direction){
                    float baseYpos = tasks.get(i).getView().getY();
                    TaskFieldView currView = tasks.get(i).getView();
                    int currI = i;
                    int animationDuration = 100;
                    currView.animate()
                            .setDuration(animationDuration)
                            .translationYBy(-taskHeight*direction)
                            .withStartAction(() -> {
                                if(currI == firstCrossedTaskIndex){
                                    disablesClickPanel.setClickable(true);
                                }
                            })
                            .withEndAction(() -> {
                                currView.setY(baseYpos);
                                if(currI == lastCrossedTaskIndex && direction > 0 ||
                                        currI == lastCrossedTaskIndex && direction < 0){
                                    movableTask.setY(movableTaskBaseY);
                                    setNewRelativePositionForTasks(movableTask);
                                    changeTasksOrderInArray();
                                    disablesClickPanel.setClickable(false);
                                    movableTaskIndex = -1;
                                }
                            });
                }
                setIsMovable(false);
                return true;
        }
        return false;
    }

    private int getMovableTaskIndex(TaskFieldView movableTask){
        TaskModel movableTaskModel = tasks.stream().filter(taskModel ->
                        taskModel.getView()
                                .equals(movableTask))
                .findAny()
                .get();
        return tasks.indexOf(movableTaskModel);
    }

    private void moveTask(MotionEvent event, TaskFieldView movableTask, float taskHeight){
        float movedY = event.getY();
        float distance = movedY - movableTaskDownY;
        float currYpos = movableTask.getY();
        float nextYpos = currYpos + distance;
        if(nextYpos < minYpos){
            nextYpos = minYpos;
        }
        if(nextYpos > maxYpos){
            nextYpos = maxYpos;
        }
        int crossedTasksCount = (int)Math.abs((nextYpos - movableTaskBaseY) /  taskHeight);
        direction = (int)Math.signum(nextYpos - movableTaskBaseY);
        lastCrossedTaskIndex = movableTaskIndex + crossedTasksCount*direction;
        firstCrossedTaskIndex = movableTaskIndex + direction;
        crossedTasks = new ArrayList<>();
        for(int i = firstCrossedTaskIndex;
            i <= lastCrossedTaskIndex && direction > 0 || i >= lastCrossedTaskIndex && direction < 0;
            i += direction){
            crossedTasks.add(tasks.get(i));
        }
        movableTask.setY(nextYpos);
    }

    private void changeTasksOrderInArray(){
        TaskModel currMovableTaskModel = tasks.get(movableTaskIndex);
        for(int i = firstCrossedTaskIndex;
            i <= lastCrossedTaskIndex && direction > 0 || i >= lastCrossedTaskIndex && direction < 0;
            i += direction){
            TaskModel crossedTask = tasks.get(i);
            tasks.set(i-direction, crossedTask);
        }
        tasks.set(lastCrossedTaskIndex, currMovableTaskModel);
    }

    private void setNewRelativePositionForTasks(TaskFieldView movableTask){
        int movableTaskId = movableTask.getId();
        int highestLineId = highestView.getId();
        TaskFieldView lastCrossedTaskView =
                tasks.get(lastCrossedTaskIndex).getView();
        TaskFieldView firstCrossedTaskView =
                tasks.get(firstCrossedTaskIndex).getView();
        if(direction > 0){
            if(firstCrossedTaskIndex == 1){
                putViewBelowViewId(firstCrossedTaskView, highestLineId);
            }
            else{
                TaskFieldView prevByFirstView =
                        tasks.get(firstCrossedTaskIndex - 2).getView();
                putViewBelowViewId(firstCrossedTaskView, prevByFirstView.getId());
            }
            if(lastCrossedTaskIndex != tasks.size()-1){
                TaskFieldView nextByLastView =
                        tasks.get(lastCrossedTaskIndex + 1).getView();
                putViewBelowViewId(nextByLastView, movableTaskId);
            }
            else{
                putViewBelowViewId(viewByLastTask, movableTaskId);
            }
            putViewBelowViewId(movableTask, lastCrossedTaskView.getId());
        }
        else if(direction < 0){
            if(lastCrossedTaskIndex == 0){
                putViewBelowViewId(movableTask, highestLineId);
            }
            else{
                TaskFieldView nextByLastView =
                        tasks.get(lastCrossedTaskIndex - 1).getView();
                putViewBelowViewId(movableTask, nextByLastView.getId());
            }
            if(firstCrossedTaskIndex != tasks.size() - 2){
                TaskFieldView prevByFirsView =
                        tasks.get(firstCrossedTaskIndex + 2).getView();
                putViewBelowViewId(prevByFirsView, firstCrossedTaskView.getId());
            }
            else{
                putViewBelowViewId(viewByLastTask, firstCrossedTaskView.getId());
            }
            putViewBelowViewId(lastCrossedTaskView, movableTaskId);
        }
    }

    private void putViewBelowViewId(View view, int id){
        RelativeLayout.LayoutParams addTaskBtnParams =
                (RelativeLayout.LayoutParams)view.getLayoutParams();
        addTaskBtnParams.addRule(RelativeLayout.BELOW, id);
        view.setLayoutParams(addTaskBtnParams);
    }

    private void setIsMovable(boolean value){
        isMovable = value;
        scrollViewContainer.setEnableScrolling(!isMovable);
    }
}
