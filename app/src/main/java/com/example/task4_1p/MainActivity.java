package com.example.task4_1p;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.task4_1p.dao.TodoDAO;
import com.example.task4_1p.dao.TodoDAOImpl;
import com.example.task4_1p.databinding.ActivityMainBinding;
import com.example.task4_1p.Todo;


import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomeFragment()); // 默认加载 HomeFragment
        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.Home) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.Add) {
                replaceFragment(new AddFragment());
            }
            else if (item.getItemId() == R.id.Modify) {
                replaceFragment(new ModifyFragment());
            }
            return true;
        });

    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public void setSelectedNavItem(int navItemId) {
        binding.bottomNavigationView.setSelectedItemId(navItemId);
    }

}