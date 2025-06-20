package ru.yandex.task_trecker.service;

import ru.yandex.task_trecker.task_data.Epic;
import ru.yandex.task_trecker.task_data.SubTask;
import ru.yandex.task_trecker.task_data.Task;

import java.util.ArrayList;

public interface TaskManager {
    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<SubTask> getSubTasks();

    void deleteTasks();

    void deleteSubTasks();

    void deleteEpics();

    Task getTaskPerId(int id);

    Epic getEpicPerId(int id);

    SubTask getSubTaskPerId(int id);

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(SubTask subtask, int epicId);

    void updateTask(Task updateTask);

    void updateEpic(Epic updateEpic);

    void updateSubTask(SubTask updateSubTask);

    void deleteTaskPerId(int id);

    void deleteEpicPerId(int id);

    void deleteSubTaskPerId(int id);

    ArrayList<SubTask> getSubtasksOfEpic(int epicId);

    void updateEpicStatus(int epicId);
}
