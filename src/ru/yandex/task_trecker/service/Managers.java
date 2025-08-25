package ru.yandex.task_trecker.service;

import java.io.File;

public class Managers {
    private HistoryManager historyManager = new InMemoryHistoryManager();
    private TaskManager taskManager = new FileBackedTaskManager(historyManager, new File("backend.txt"));

    public HistoryManager getDefaultHistory() {
        return historyManager;
    }

    public TaskManager getDefault() {
        return taskManager;
    }

    public void setHistoryManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }
}
