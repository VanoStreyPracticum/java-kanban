package ru.yandex.task_trecker.service;

import ru.yandex.task_trecker.task_data.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();

    void remove(int id);
}
