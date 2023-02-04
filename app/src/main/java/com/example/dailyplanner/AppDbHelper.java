package com.example.dailyplanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.dailyplanner.Model.DayModel;
import com.example.dailyplanner.Model.TaskModel;

import java.util.ArrayList;

public class AppDbHelper {
    private SQLiteDatabase db;

    public AppDbHelper(Context ctx){
        db = new AppSQLiteHelper(ctx).getWritableDatabase();
    }

    @SuppressLint("Recycle")
    public ArrayList<DayModel> findDays(){
        Cursor cursor = db.rawQuery("SELECT * FROM days", null);
        cursor.moveToFirst();
        ArrayList<DayModel> days = new ArrayList<>();

        //TODO thread
        //TODO db const string
        for (int i = 0; i < cursor.getCount(); i++) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            long unixDate = cursor.getLong(cursor.getColumnIndexOrThrow("data"));
            days.add(new DayModel(id, unixDate)); //year
            cursor.moveToNext();
        }
        cursor.close();
        return days;
    }

    @SuppressLint("DefaultLocale")
    public void InsertDay(int dayId, long unixTime){
        db.execSQL(String.format("INSERT INTO days VALUES (%d, %d)", dayId, unixTime));
    }

    @SuppressLint("DefaultLocale")
    public void DeleteDay(long unixTime){
        db.execSQL(String.format("DELETE FROM days WHERE data = %d", unixTime));
    }

    @SuppressLint("DefaultLocale")
    public ArrayList<TaskModel> findTasksByDay(int dayId){
        Cursor cursor = db.rawQuery(String.format
                    ("SELECT * FROM tasks where day_id = %d", dayId), null);
        cursor.moveToFirst();
        ArrayList<TaskModel> tasks = new ArrayList<>();
        //TODO thread
        //TODO db const string
        for (int i = 0; i < cursor.getCount(); i++) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
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
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String task = cursor.getString(cursor.getColumnIndexOrThrow("task"));
            int int_IsDone = cursor.getInt(cursor.getColumnIndexOrThrow("is_done"));
            int dayId = cursor.getInt(cursor.getColumnIndexOrThrow("day_id"));
            boolean isDone = int_IsDone != 0;
            Log.w("AAA", id + "  " + int_IsDone);
            tasks.add(new TaskModel(id, task, isDone, dayId)); //year
            cursor.moveToNext();
        }

        cursor.close();
        return tasks;
    }

    @SuppressLint("DefaultLocale")
    public void InsertTask(int taskId, String task, boolean isDone, int dayId){
        db.execSQL(String.format
                ("INSERT INTO tasks (id, task, is_done, day_id) VALUES (%d, \"%s\", %d, %d)",
                        taskId, task, isDone ? 1 : 0, dayId));
    }

    @SuppressLint("DefaultLocale")
    public void DeleteTask(int taskId){
        db.execSQL(String.format("DELETE FROM tasks WHERE id = %d", taskId));
    }

    @SuppressLint("DefaultLocale")
    public void UpdateTask(int taskId, boolean isDone, String newTask){
        db.execSQL(String.format("UPDATE tasks SET task = \"%s\", is_done = %d WHERE id = %d",
                newTask, isDone ? 1 : 0, taskId));
    }
}
