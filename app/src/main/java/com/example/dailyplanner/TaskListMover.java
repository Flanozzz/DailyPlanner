package com.example.dailyplanner;

import android.util.Log;
import android.view.MotionEvent;

import com.example.dailyplanner.Model.TaskModel;

import java.util.ArrayList;

public class TaskListMover {
    private ArrayList<TaskModel> tasks;

    private float movableTaskBaseY = 0;
    private float movableTaskDownY = 0;
    private TaskModel prevByMovableTask = null;
    private TaskModel nextByMovableTask = null;
    private int movableTaskIndex = -1;
    private float minYpos = 9999999;
    private float maxYpos = -9999999;
    private boolean isMovable = false;
    private long stopedTime = -1;
    private ArrayList<TaskModel> crossedTasks = new ArrayList<>();
    private int direction = 0;
    private int firstCrossedTaskIndex = -1;
    private int lastCrossedTaskIndex = -1;

    public TaskListMover(ArrayList<TaskModel> tasksList){
        tasks = tasksList;
    }

    public boolean dragEndDropEvent1(TaskFieldView movableTask, MotionEvent event){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            float newMinYpos = tasks.get(0).getView().getY();
            float newMaxYpos = tasks.get(tasks.size() - 1).getView().getY();
            if (newMinYpos < minYpos) {
                minYpos = newMinYpos;
            }
            if (newMaxYpos > maxYpos) {
                maxYpos = newMaxYpos;
            }
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    movableTaskDownY = event.getY();
                    movableTaskBaseY = movableTask.getY();
                    TaskModel movableTaskModel =
                            tasks.stream().filter(taskModel ->
                                            taskModel.getView()
                                                    .equals(movableTask))
                                    .findAny()
                                    .get();
                    movableTaskIndex = tasks.indexOf(movableTaskModel);
//                  Log.w("AAA", movableTaskIndex + "");
                    if (movableTaskIndex > 0) {
                        prevByMovableTask = tasks.get(movableTaskIndex - 1);
//                    Log.w("AAA", prevByMovableTask.getView().getY() + "");
                    }
                    if (movableTaskIndex < tasks.size() - 1) {
                        nextByMovableTask = tasks.get(movableTaskIndex + 1);
//                    Log.w("AAA", nextByMovableTask.getView().getY() + "");
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!isMovable) {
                        break;
                    }
                    float movedY = event.getY();
                    float distance = movedY - movableTaskDownY;
                    float nextYpos = movableTask.getY() + distance;
                    float taskHeight = movableTask.getHeight();
                    if (prevByMovableTask != null && nextYpos <= prevByMovableTask.getView().getY()) {
                        float currPrevTaskY = prevByMovableTask.getView().getY();
                        prevByMovableTask.getView().setY(currPrevTaskY + taskHeight);
                        tasks.set(movableTaskIndex - 1, tasks.get(movableTaskIndex));
                        tasks.set(movableTaskIndex, prevByMovableTask);
                        movableTaskIndex--;
                        movableTaskBaseY = currPrevTaskY;
                        if (movableTaskIndex == 0) {
                            prevByMovableTask = null;
                        } else {
                            nextByMovableTask = prevByMovableTask;
                            prevByMovableTask = tasks.get(movableTaskIndex - 1);
                        }
                    }
                    if (nextByMovableTask != null && nextYpos >= nextByMovableTask.getView().getY()) {
                        float currNextTaskY = nextByMovableTask.getView().getY();
                        nextByMovableTask.getView().setY(currNextTaskY - taskHeight);
                        tasks.set(movableTaskIndex + 1, tasks.get(movableTaskIndex));
                        tasks.set(movableTaskIndex, nextByMovableTask);
                        movableTaskIndex++;
                        movableTaskBaseY = currNextTaskY;
                        if (movableTaskIndex == tasks.size() - 1) {
                            nextByMovableTask = null;
                        } else {
                            prevByMovableTask = nextByMovableTask;
                            nextByMovableTask = tasks.get(movableTaskIndex + 1);
                        }
                    }
                    movableTask.setY(nextYpos);

                    break;
                case MotionEvent.ACTION_UP:
                    movableTask.setY(movableTaskBaseY);
                    movableTaskIndex = -1;
                    prevByMovableTask = null;
                    nextByMovableTask = null;
                    isMovable = false;
                    return true;
            }
            return false;
        }
        return true;
    }

    public boolean dragEndDropEvent(TaskFieldView movableTask, MotionEvent event){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            float taskHeight = movableTask.getHeight();
            float newMinYpos = tasks.get(0).getView().getY();
            float newMaxYpos = tasks.get(tasks.size() - 1).getView().getY();
            if (newMinYpos < minYpos) {
                minYpos = newMinYpos;
            }
            if (newMaxYpos > maxYpos) {
                maxYpos = newMaxYpos;
            }
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    movableTaskDownY = event.getY();
                    movableTaskBaseY = movableTask.getY();
                    TaskModel movableTaskModel =
                            tasks.stream().filter(taskModel ->
                                            taskModel.getView()
                                                    .equals(movableTask))
                                    .findAny()
                                    .get();
                    movableTaskIndex = tasks.indexOf(movableTaskModel);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!isMovable) {
                        break;
                    }
                    for (TaskModel t :
                            tasks) {
//                        t.getView().getBinding().mainLayout.setBackgroundColor(movableTask.getResources().getColor(R.color.background, null));
                    }
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
//                        tasks.get(i)
//                                .getView()
//                                .getBinding().mainLayout
//                                .setBackgroundColor(movableTask
//                                        .getResources()
//                                        .getColor(R.color.burntSienna, null));
                    }


                    movableTask.setY(nextYpos);
                    break;
                case MotionEvent.ACTION_UP:
                    float finalPos;
                    if (crossedTasks.size() > 0){
                        finalPos = crossedTasks.get(crossedTasks.size() - 1).getView().getY();
                    }
                    else{
                        finalPos = movableTaskBaseY;
                    }
                    TaskModel currMovableTaskModel = tasks.get(movableTaskIndex);
                    for(int i = firstCrossedTaskIndex;
                        i <= lastCrossedTaskIndex && direction > 0 || i >= lastCrossedTaskIndex && direction < 0;
                        i += direction){
                        TaskModel crossedTask = tasks.get(i);
                        float currPos = crossedTask.getView().getY();
                        float nextPos = currPos - taskHeight * direction;
                        crossedTask.getView().setY(nextPos);
                        tasks.set(i-direction, crossedTask);
//                        tasks.get(i)
//                                .getView()
//                                .getBinding().mainLayout
//                                .setBackgroundColor(movableTask
//                                        .getResources()
//                                        .getColor(R.color.background, null));
                    }
                    if(isMovable){
                        tasks.set(lastCrossedTaskIndex, currMovableTaskModel);
                    }
                    movableTask.setY(finalPos);
                    movableTaskIndex = -1;
                    isMovable = false;
                    return true;
            }
            return false;
        }
        return true;
    }

    private ArrayList<TaskModel> getCrossedTasks(float startYPoint, float endYPoint){
        return null;
    }

    public void makeMovable(){
        isMovable = true;
    }
}
