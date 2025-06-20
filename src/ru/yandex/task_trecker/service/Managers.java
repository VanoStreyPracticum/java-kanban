package ru.yandex.task_trecker.service;

public class Managers {
    private final HistoryManager historyManager = new InMemoryHistoryManager();
    private final TaskManager taskManager = new InMemoryTaskManager(historyManager);

    public HistoryManager getDefaultHistory(){
        return historyManager;
    }
    public TaskManager getDefault(){
        return taskManager;
    }
}
