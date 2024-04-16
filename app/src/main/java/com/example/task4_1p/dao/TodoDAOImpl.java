package com.example.task4_1p.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.task4_1p.DatabaseHelper;
import com.example.task4_1p.Todo;

import java.util.ArrayList;
import java.util.List;

public class TodoDAOImpl implements TodoDAO {

    private DatabaseHelper dbHelper;

    public TodoDAOImpl(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public long insert(Todo todo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITLE, todo.getTitle());
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, todo.getDescription());
        values.put(DatabaseHelper.COLUMN_DATE, todo.getDate());
        long id = db.insert(DatabaseHelper.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    @Override
    public void update(Todo todo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITLE, todo.getTitle());
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, todo.getDescription());
        values.put(DatabaseHelper.COLUMN_DATE, todo.getDate());
        db.update(DatabaseHelper.TABLE_NAME, values, DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(todo.getId())});
        db.close();
    }

    @Override
    public void delete(Todo todo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(todo.getId())});
        db.close();
    }

    @Override
    public List<Todo> getAllTodos() {
        List<Todo> todoList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                Todo todo = new Todo();
                todo.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
                todo.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE)));
                todo.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION)));
                todo.setDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE)));
                todoList.add(todo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return todoList;
    }
}
