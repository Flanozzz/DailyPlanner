package com.example.dailyplanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;
import android.util.Pair;

import com.example.dailyplanner.Model.DayModel;
import com.example.dailyplanner.Model.TaskModel;

import java.util.ArrayList;
import java.util.Comparator;

public class AppDbHelper {
    private SQLiteDatabase db;

    public AppDbHelper(Context ctx){
        db = new AppSQLiteHelper(ctx).getWritableDatabase();
    }

    @SuppressLint("Recycle")
    public Pair<ArrayList<DayModel>, Integer> findDaysAndGetId(){
        int maxDayId = -1;
        Cursor cursor = db.rawQuery(
                "SELECT * FROM days " +
                    "LEFT JOIN tasks " +
                    "ON day_id = day_id_key " +
                    "ORDER BY day_id", null);
        cursor.moveToFirst();
        ArrayList<DayModel> days = new ArrayList<>();
        //TODO thread
        //TODO db const string
        int prevDayId = -1;
        ArrayList<TaskModel> currDayTasks = new ArrayList<>();
        if (cursor.getCount() > 0){
            DayModel newDay;
            prevDayId = cursor.getInt(cursor.getColumnIndexOrThrow("day_id"));
            maxDayId = Math.max(maxDayId, prevDayId);
            long unixDate = cursor.getLong(cursor.getColumnIndexOrThrow("data"));
            boolean hasTasks = !cursor.isNull(cursor.getColumnIndexOrThrow("day_id_key"));
            if(!hasTasks){
                newDay = new DayModel(prevDayId, unixDate, new ArrayList<>());
                days.add(newDay);
            }
            else{
                TaskModel dayTaskModel = loadTask(cursor, prevDayId);
                currDayTasks.add(dayTaskModel);
                newDay = new DayModel(prevDayId, unixDate, currDayTasks);
                days.add(newDay);
            }
            if(cursor.getCount() == 1){
                cursor.close();
                return new Pair<>(days, maxDayId);
            }
            cursor.moveToNext();
        }
        for (int i = 1; i < cursor.getCount(); i++) {
            int dayId = cursor.getInt(cursor.getColumnIndexOrThrow("day_id"));

            maxDayId = Math.max(maxDayId, dayId);
            long unixDate = cursor.getLong(cursor.getColumnIndexOrThrow("data"));
            boolean hasTasks = !cursor.isNull(cursor.getColumnIndexOrThrow("day_id_key"));
            if(!hasTasks){
                currDayTasks = new ArrayList<>();
                DayModel newDay = new DayModel(dayId, unixDate, new ArrayList<>());
                days.add(newDay);
                cursor.moveToNext();
                continue;
            }
            TaskModel dayTaskModel = loadTask(cursor, dayId);
            if(prevDayId == dayId){
                currDayTasks.add(dayTaskModel);
            }
            else{
                prevDayId = dayId;
                currDayTasks = new ArrayList<>();
                currDayTasks.add(dayTaskModel);
                DayModel newDay = new DayModel(dayId, unixDate, currDayTasks);
                days.add(newDay);
            }
            cursor.moveToNext();
        }
        cursor.close();
        for (DayModel day :
                days) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                day.getTasks().sort((taskModel, t1) ->
                        -Integer.compare(t1.getOrderInList(), taskModel.getOrderInList()));
            }
        }
        return new Pair<>(days, maxDayId);
    }

    @SuppressLint("DefaultLocale")
    public void InsertDay(int dayId, long unixTime){
        Log.e("AAA", "InsertDay");
        db.execSQL(String.format("INSERT INTO days VALUES (%d, %d)", dayId, unixTime));
    }

    @SuppressLint("DefaultLocale")
    public void DeleteDay(long unixTime){
        db.execSQL(String.format("DELETE FROM days WHERE data = %d", unixTime));
    }

    @SuppressLint("DefaultLocale")
    public ArrayList<TaskModel> findTasksByDay(int dayId){
        Cursor cursor = db.rawQuery(String.format
                    ("SELECT * FROM tasks where day_id_key = %d", dayId), null);
        cursor.moveToFirst();
        ArrayList<TaskModel> tasks = new ArrayList<>();
        //TODO thread
        //TODO db const string
        for (int i = 0; i < cursor.getCount(); i++) {
            tasks.add(loadTask(cursor, dayId)); //year
            cursor.moveToNext();
        }

        cursor.close();
        return tasks;
    }

    @SuppressLint("DefaultLocale")
    public ArrayList<TaskModel> findAllTasks(){
        Cursor cursor = db.rawQuery("SELECT * FROM tasks", null);
        cursor.moveToFirst();
        ArrayList<TaskModel> tasks = new ArrayList<>();
        //TODO thread
        //TODO db const string
        for (int i = 0; i < cursor.getCount(); i++) {
            int dayId = cursor.getInt(cursor.getColumnIndexOrThrow("day_id_key"));
            tasks.add(loadTask(cursor, dayId)); //year
            cursor.moveToNext();
        }

        cursor.close();
        return tasks;
    }

    @SuppressLint("DefaultLocale")
    public void InsertTask(int taskId, String task, boolean isDone, int dayId, int orderInList){
        db.execSQL(String.format
                ("INSERT INTO tasks (task_id, task, is_done, day_id_key, order_in_list) " +
                                "VALUES (%d, \"%s\", %d, %d, %d)",
                        taskId, task, isDone ? 1 : 0, dayId, orderInList));
    }

    @SuppressLint("DefaultLocale")
    public void DeleteTask(int taskId){
        Log.w("AAA", "DeleteTask");
        db.execSQL(String.format("DELETE FROM tasks WHERE task_id = %d", taskId));
    }

    @SuppressLint("DefaultLocale")
    public void UpdateTask(int taskId, boolean isDone, String newTask, int orderInList){
        db.execSQL(String.format("UPDATE tasks " +
                        "SET task = \"%s\", is_done = %d, order_in_list = %d" +
                        " WHERE task_id = %d",
                newTask, isDone ? 1 : 0, orderInList, taskId));
    }

    private TaskModel loadTask(Cursor cursor, int dayId){
        int taskId = cursor.getInt(cursor.getColumnIndexOrThrow("task_id"));
        int int_taskIsDone = cursor.getInt(cursor.getColumnIndexOrThrow("is_done"));
        boolean taskIsDone = int_taskIsDone != 0;
        String task = cursor.getString(cursor.getColumnIndexOrThrow("task"));
        int orderInList = cursor.getInt(cursor.getColumnIndexOrThrow("order_in_list"));
        return new TaskModel(taskId, task, taskIsDone, dayId, orderInList);
    }
}
