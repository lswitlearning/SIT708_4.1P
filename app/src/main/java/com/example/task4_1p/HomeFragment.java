package com.example.task4_1p;
import com.example.task4_1p.Todo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.task4_1p.dao.TodoDAO;
import com.example.task4_1p.dao.TodoDAOImpl;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

//    private TodoDAO todoDAO;
    private LinearLayout todoItemsLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        todoItemsLayout = view.findViewById(R.id.todoItemsLayout);

        // Load todo items
        loadTodoItems();

        return view;
    }

    private void loadTodoItems() {
        // Clear existing views
        todoItemsLayout.removeAllViews();

        // Get todo items from database
        List<Todo> todoList = getAllTodoItems();



        // Populate todo items in layout
        if (todoList != null && !todoList.isEmpty()) {
            for (Todo todo : todoList) {
                View todoItemView = LayoutInflater.from(requireContext()).inflate(R.layout.todo_item_layout, null);

                TextView idTextView = todoItemView.findViewById(R.id.idTextView);
                TextView titleTextView = todoItemView.findViewById(R.id.titleTextView);
                TextView descriptionTextView = todoItemView.findViewById(R.id.descriptionTextView);
                TextView dateTextView = todoItemView.findViewById(R.id.dateTextView);
                RadioButton radioButton = todoItemView.findViewById(R.id.todoRadioButton);

                idTextView.setText(String.valueOf(todo.getId()));
                titleTextView.setText(todo.getTitle());
                descriptionTextView.setText(todo.getDescription());
                dateTextView.setText(todo.getDate());

                todoItemsLayout.addView(todoItemView);

                // 在 RadioButton 的點擊事件中設置點擊後的操作
                radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 設置延遲操作
                        v.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 在延遲後執行刪除操作
                                deleteTodoItem(todo.getId());
                                loadTodoItems(); // 重新加載 todo 項目
                            }
                        }, 3000); // 3 秒延遲
                    }
                });
            }
        } else {
            showToast("No todo items found.");
        }
    }

    private void deleteTodoItem(int todoId) {
        // 創建或打開數據庫連接
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().getPath() + "/todo.db", null);

        try {
            // 執行刪除語句
            database.delete("todos", "id = ?", new String[]{String.valueOf(todoId)});

            // 在這裡添加其他需要更新的操作，例如刷新列表等

            // 顯示刪除成功的提示消息
            showToast("Todo item deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error occurred while deleting todo item.");
        } finally {
            // 關閉數據庫連接
            if (database != null) {
                database.close();
            }
        }
    }



    private List<Todo> getAllTodoItems() {
        List<Todo> todoList = new ArrayList<>();
        SQLiteDatabase database = null;
        Cursor cursor = null;

        try {
            // Open database
            database = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().getPath() + "/todo.db", null);

//            database.delete("todos",null,null);
//            cursor = database.rawQuery("Delete FROM todos", null);
            // Query todo items
            cursor = database.rawQuery("SELECT * FROM todos order by due_date desc", null);

            // Parse cursor and add todo items to list
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Todo todo = new Todo();
                    todo.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    todo.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
                    todo.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                    todo.setDate(cursor.getString(cursor.getColumnIndexOrThrow("due_date")));

                    todoList.add(todo);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error occurred while retrieving todo items.");
        } finally {
            // Close cursor and database
            if (cursor != null) {
                cursor.close();
            }
            if (database != null) {
                database.close();
            }
        }

        return todoList;
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
