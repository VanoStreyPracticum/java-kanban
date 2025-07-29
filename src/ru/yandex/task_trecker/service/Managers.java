package ru.yandex.task_trecker.service;

import java.io.File;

public class Managers {
    private final HistoryManager historyManager = new InMemoryHistoryManager();
    private final TaskManager taskManager = new FileBackedTaskManager(historyManager, new File("backend.txt"));

    public HistoryManager getDefaultHistory() {
        return historyManager;
    }

    public TaskManager getDefault() {
        return taskManager;
    }
}
