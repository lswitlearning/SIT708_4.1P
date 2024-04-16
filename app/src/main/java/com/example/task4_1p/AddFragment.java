package com.example.task4_1p;
import com.example.task4_1p.dao.TodoDAO;
import com.example.task4_1p.dao.TodoDAOImpl;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;


import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class AddFragment extends Fragment {

    private EditText titleEditText, descriptionEditText;
    private DatePicker datePicker;
    private Button addButton;


    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        // Initialize views
        titleEditText = view.findViewById(R.id.title);
        descriptionEditText = view.findViewById(R.id.description);
        datePicker = view.findViewById(R.id.datePicker);
        addButton = view.findViewById(R.id.addButton);

//        // Set default date to current date
//        Calendar calendar = Calendar.getInstance();
//        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);

        // Set button click listener
        addButton.setOnClickListener(v -> addTodo());

        return view;
    }

        private void addTodo() {
            String title = titleEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();
            Calendar calendar = getSelectedDate();
//            if (calendar == null) {
//                showToast("Please select a valid date");
//                return;
//            }

            if (title.isEmpty()) {
                showToast("Please fill in Title");
                return;
            }

            // Format the selected date
            String dueDate = dateFormat.format(calendar.getTime());


            SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().getPath() + "/todo.db", null);
            String createTableQuery = "CREATE TABLE IF NOT EXISTS todos (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT, due_date TEXT)";
            database.execSQL(createTableQuery);
            String insertDataQuery = "INSERT INTO todos (title, description, due_date) VALUES ('" + title + "', '" + description + "', '" + dueDate + "')";
            database.execSQL(insertDataQuery);
            database.close();


            // 当待办事项添加成功后，调用 HomeFragment 中的 loadTodoList() 方法更新列表视图
//            HomeFragment homeFragment = (HomeFragment) getParentFragmentManager().findFragmentById(R.id.todoItemsLayout);
//            if (homeFragment != null) {
//                LayoutInflater inflater = requireActivity().getLayoutInflater();
//                ViewGroup container = requireActivity().findViewById(R.id.frame_layout); // 使用实际的容器 ID
//                View view = getView(); // 获取当前 Fragment 的视图
//                homeFragment.loadTodoList(inflater, container, view);
//            }


            showToast("Todo added successfully");
        }

        private Calendar getSelectedDate() {
            int year = datePicker.getYear();
            int month = datePicker.getMonth();
            int dayOfMonth = datePicker.getDayOfMonth();

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);

            return calendar;
        }

        private void showToast(String message) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }
