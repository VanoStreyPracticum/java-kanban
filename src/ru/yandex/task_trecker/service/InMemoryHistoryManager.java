package ru.yandex.task_trecker.service;

import ru.yandex.task_trecker.task_data.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{
    private final ArrayList<Task> history = new ArrayList<>();

    @Override
    public ArrayList<Task> getHistory(){
        return history;
    }

    @Override
    public void add(Task task){
        history.add(task);
        while(history.size() > 10){
            history.removeFirst();
        }
    }
}
