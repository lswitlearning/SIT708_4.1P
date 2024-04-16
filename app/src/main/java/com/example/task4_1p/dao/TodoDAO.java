package com.example.task4_1p.dao;

import com.example.task4_1p.Todo;

import java.util.List;

public interface TodoDAO {
    // 插入待办事项
    long insert(Todo todo);

    // 更新待办事项
    void update(Todo todo);

    // 删除待办事项
    void delete(Todo todo);

    // 获取所有待办事项
    List<Todo> getAllTodos();
}
