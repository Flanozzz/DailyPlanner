package com.example.dailyplanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.dailyplanner.Model.DayModel;
import com.example.dailyplanner.Model.TaskModel;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AppDbHelper {
    private SQLiteDatabase db;

    public AppDbHelper(Context ctx){
        db = new AppSQLiteHelper(ctx).getWritableDatabase();
    }

    @SuppressLint("Recycle")
    public ArrayList<DayModel> findDays(){
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
            long unixDate = cursor.getLong(cursor.getColumnIndexOrThrow("data"));
            boolean hasTasks = !cursor.isNull(cursor.getColumnIndexOrThrow("day_id_key"));
            if(!hasTasks){
                newDay = new DayModel(prevDayId, unixDate, new ArrayList<>());
                days.add(newDay);
            }
            else{
                int taskId = cursor.getInt(cursor.getColumnIndexOrThrow("task_id"));
                int int_taskIsDone = cursor.getInt(cursor.getColumnIndexOrThrow("is_done"));
                boolean taskIsDone = int_taskIsDone != 0;
                String task = cursor.getString(cursor.getColumnIndexOrThrow("task"));
                TaskModel dayTaskModel = new TaskModel(taskId, task, taskIsDone, prevDayId);
                currDayTasks.add(dayTaskModel);
            }
            newDay = new DayModel(prevDayId, unixDate, currDayTasks);
            days.add(newDay);
            if(cursor.getCount() == 1){
                cursor.close();
                return days;
            }
            cursor.moveToNext();
        }
        for (int i = 1; i < cursor.getCount(); i++) {
            int dayId = cursor.getInt(cursor.getColumnIndexOrThrow("day_id"));
            long unixDate = cursor.getLong(cursor.getColumnIndexOrThrow("data"));
            boolean hasTasks = !cursor.isNull(cursor.getColumnIndexOrThrow("day_id_key"));
            if(!hasTasks){
                currDayTasks = new ArrayList<>();
                DayModel newDay = new DayModel(dayId, unixDate, new ArrayList<>());
                days.add(newDay);
                cursor.moveToNext();
                continue;
            }

            int taskId = cursor.getInt(cursor.getColumnIndexOrThrow("task_id"));
            int int_taskIsDone = cursor.getInt(cursor.getColumnIndexOrThrow("is_done"));
            boolean taskIsDone = int_taskIsDone != 0;
            String task = cursor.getString(cursor.getColumnIndexOrThrow("task"));
            TaskModel dayTaskModel = new TaskModel(taskId, task, taskIsDone, dayId);
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
//        for (DayModel day :
//                days) {
//            Log.e("AAA", day.getId() + "");
//            for (TaskModel t :
//                    day.getTasks()) {
//                Log.d("AAA", t.getId() + "");
//            }
//        }
        return days;
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
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("task_id"));
            String task = cursor.getString(cursor.getColumnIndexOrThrow("task"));
            int int_IsDone = cursor.getInt(cursor.getColumnIndexOrThrow("is_done"));
            boolean isDone = int_IsDone != 0;
            tasks.add(new TaskModel(id, task, isDone, dayId)); //year
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
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("task_id"));
            String task = cursor.getString(cursor.getColumnIndexOrThrow("task"));
            int int_IsDone = cursor.getInt(cursor.getColumnIndexOrThrow("is_done"));
            int dayId = cursor.getInt(cursor.getColumnIndexOrThrow("day_id_key"));
            boolean isDone = int_IsDone != 0;
            tasks.add(new TaskModel(id, task, isDone, dayId)); //year
            cursor.moveToNext();
        }

        cursor.close();
        return tasks;
    }

    @SuppressLint("DefaultLocale")
    public void InsertTask(int taskId, String task, boolean isDone, int dayId){
        db.execSQL(String.format
                ("INSERT INTO tasks (task_id, task, is_done, day_id_key) VALUES (%d, \"%s\", %d, %d)",
                        taskId, task, isDone ? 1 : 0, dayId));
    }

    @SuppressLint("DefaultLocale")
    public void DeleteTask(int taskId){
        Log.w("AAA", "DeleteTask");
        db.execSQL(String.format("DELETE FROM tasks WHERE task_id = %d", taskId));
    }

    @SuppressLint("DefaultLocale")
    public void UpdateTask(int taskId, boolean isDone, String newTask){
        db.execSQL(String.format("UPDATE tasks SET task = \"%s\", is_done = %d WHERE task_id = %d",
                newTask, isDone ? 1 : 0, taskId));
    }
}
