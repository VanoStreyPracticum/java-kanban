package ru.yandex.task_trecker.service;

import ru.yandex.task_trecker.task_data.*;

import java.util.*;

public interface TaskManager {
    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getSubtasks();

    void deleteTasks();

    void deleteSubtasks();

    void deleteEpics();

    Task getTaskPerId(int id);

    Epic getEpicPerId(int id);

    Subtask getSubTaskPerId(int id);

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask, int epicId);

    void updateTask(Task updateTask);

    void updateEpic(Epic updateEpic);

    void updateSubtask(Subtask updateSubtask);

    void deleteTaskPerId(int id);

    void deleteEpicPerId(int id);

    void deleteSubtaskPerId(int id);

    ArrayList<Subtask> getSubtasksOfEpic(int epicId);

    void updateEpicStatus(int epicId);

    List<Task> getPrioritizedTasks();
}
