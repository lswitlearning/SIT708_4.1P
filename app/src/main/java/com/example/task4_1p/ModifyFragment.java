package com.example.task4_1p;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class ModifyFragment extends Fragment {

    private LinearLayout todoItemsLayout;
    private Button editButton;
    private int selectedTodoId = -1; // 记录选中的待修改记录的ID，默认为-1


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_modify, container, false);

        // 初始化视图
        todoItemsLayout = view.findViewById(R.id.todoItemsLayout);
        editButton = view.findViewById(R.id.editButton);
        editButton.setVisibility(View.GONE); // 初始状态下隐藏编辑按钮
   //     todoRadioGroup = view.findViewById(R.id.todoRadioGroup); // 获取RadioGroup

        // 加载待办事项列表
        loadTodoItems();

        // 设置编辑按钮的点击事件
        editButton.setOnClickListener(v -> navigateToModifyDetails());

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


                // 设置 RadioButton 与 RadioGroup 的关联
                radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        selectedTodoId = todo.getId();
                        editButton.setVisibility(View.VISIBLE); // 当选中RadioButton时显示编辑按钮
                    }
                });

                // 将todoItemView添加到todoItemsLayout
                todoItemsLayout.addView(todoItemView);
            }

        } else {
            showToast("No todo items found.");
        }
    }


    private List<Todo> getAllTodoItems() {
        List<Todo> todoList = new ArrayList<>();
        SQLiteDatabase database = null;
        Cursor cursor = null;

        try {
            // Open database
            database = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().getPath() + "/todo.db", null);
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

    private void navigateToModifyDetails() {
        if (selectedTodoId != -1) {
            // 隐藏ModifyFragment的ScrollView界面
            ScrollView scrollView = requireView().findViewById(R.id.scrollView);
            scrollView.setVisibility(View.GONE);

            // 隐藏ModifyFragment头部的Todos文本和Edit按钮
            TextView todosTextView = requireView().findViewById(R.id.Todos);
            todosTextView.setVisibility(View.GONE);
            editButton.setVisibility(View.GONE);
            // 隱藏 ModifyFragment 的內容
            todoItemsLayout.setVisibility(View.GONE);

            onTodoItemSelected(selectedTodoId);

            // 使用靜態工廠方法實例化 ModifyDetailsFragment
            ModifyDetailsFragment modifyDetailsFragment = ModifyDetailsFragment.newInstance(selectedTodoId);

            // 將 ModifyDetailsFragment 添加到 fragment_container
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, modifyDetailsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            showToast("Please select a todo item to edit.");
        }
    }
        // 定义回调接口
        public interface OnTodoSelectedListener {
            void onTodoSelected(int todoId);
        }

    private OnTodoSelectedListener listener;

    // 当用户选择待办事项时调用此方法
    private void onTodoItemSelected(int todoId) {
        // 检查是否已设置监听器
        if (listener != null) {
            // 调用回调方法，将选定的待办事项ID传递给Activity
            listener.onTodoSelected(todoId);
        }
    }

    // 设置监听器方法
    public void setOnTodoSelectedListener(OnTodoSelectedListener listener) {
        this.listener = listener;
    }
}

