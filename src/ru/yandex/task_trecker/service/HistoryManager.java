package ru.yandex.task_trecker.service;

import ru.yandex.task_trecker.task_data.Task;

import java.util.ArrayList;

public interface HistoryManager {
    void add(Task task);
    ArrayList<Task> getHistory();
}
