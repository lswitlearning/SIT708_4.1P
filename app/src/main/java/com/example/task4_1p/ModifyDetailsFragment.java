package com.example.task4_1p;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ModifyDetailsFragment extends Fragment {

    private int todoId;

    public ModifyDetailsFragment() {
    }
    // 成員變量用於保存當前選中的 RadioButton

    public static ModifyDetailsFragment newInstance(int todoId) {
        ModifyDetailsFragment fragment = new ModifyDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("todoId", todoId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            todoId = getArguments().getInt("todoId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.modify_details, container, false);

        // Get todo details from database using todoId
        Todo todo = getTodoDetails(todoId);

        if (todo != null) {
            EditText titleEditText = view.findViewById(R.id.title);
            EditText descriptionEditText = view.findViewById(R.id.description);
            DatePicker datePicker = view.findViewById(R.id.datePicker);
            Button confirmButton = view.findViewById(R.id.confirmButton);
            Button cancelButton = view.findViewById(R.id.cancelButton);

            titleEditText.setText(todo.getTitle());
            descriptionEditText.setText(todo.getDescription());
            // Set date picker to todo's date
            setDateOnDatePicker(datePicker, todo.getDate());

// Set click listener for confirm button
            confirmButton.setOnClickListener(v -> {
                // Update database with edited values
                String newTitle = titleEditText.getText().toString();
                String newDescription = descriptionEditText.getText().toString();
                String newDate = getSelectedDateFromDatePicker(datePicker);
                updateTodoInDatabase(todo.getId(), newTitle, newDescription, newDate);

// Get reference to MainActivity and replace fragment with HomeFragment
                if (getActivity() instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.replaceFragment(new HomeFragment());
                    // Update bottom navigation view to show HomeFragment
                    mainActivity.setSelectedNavItem(R.id.Home);

                } else {
                    showToast("Failed to update todo.");
                }
            });

            // Set click listener for cancel button
            cancelButton.setOnClickListener(v -> {
                // Pop fragment from back stack to return to ModifyFragment
                getParentFragmentManager().popBackStack();
                // 重新加载 ModifyFragment 的内容
                loadModifyFragmentContent();
            });
        } else {
            showToast("Failed to load todo details.");
        }

        return view;
    }

    private void loadModifyFragmentContent() {
        // 使用静态工厂方法实例化 ModifyFragment
        ModifyFragment modifyFragment = new ModifyFragment();
        // 将 ModifyFragment 添加到 fragment_container
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, modifyFragment)
                .commit();
    }

    private void setDateOnDatePicker(DatePicker datePicker, String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = dateFormat.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            datePicker.init(year, month, day, null);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String getSelectedDateFromDatePicker(DatePicker datePicker) {
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();
        return String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
    }

    private boolean updateTodoInDatabase(int todoId, String newTitle, String newDescription, String newDate) {
        SQLiteDatabase database = null;
        try {
            database = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().getPath() + "/todo.db", null);
            database.execSQL("UPDATE todos SET title = ?, description = ?, due_date = ? WHERE id = ?",
                    new String[]{newTitle, newDescription, newDate, String.valueOf(todoId)});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error occurred while updating todo.");
            return false;
        } finally {
            if (database != null) {
                database.close();
            }
        }

    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private Todo getTodoDetails(int todoId) {
        SQLiteDatabase database = null;
        Cursor cursor = null;
        Todo todo = null;

        try {
            // 打开数据库
            database = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().getPath() + "/todo.db", null);
            // 查询数据库获取待办事项的详细信息
            cursor = database.rawQuery("SELECT * FROM todos WHERE id = ?", new String[]{String.valueOf(todoId)});

            // 解析游标并将待办事项详细信息设置到Todo对象中
            if (cursor != null && cursor.moveToFirst()) {
                todo = new Todo();
                todo.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                todo.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
                todo.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                todo.setDate(cursor.getString(cursor.getColumnIndexOrThrow("due_date")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error occurred while retrieving todo details.");
        } finally {
            // 关闭游标和数据库连接
            if (cursor != null) {
                cursor.close();
            }
            if (database != null) {
                database.close();
            }
        }

        return todo;
    }

}