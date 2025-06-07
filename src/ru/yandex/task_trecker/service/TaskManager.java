package ru.yandex.task_trecker.service;

import ru.yandex.task_trecker.task_data.*;

import java.util.ArrayList;

public class TaskManager {
    private final ArrayList<Task> tasks = new ArrayList<>();
    private final ArrayList<Epic> epics = new ArrayList<>();
    private final ArrayList<SubTask> subTasks = new ArrayList<>();
    private int idCounter = 0;

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public ArrayList<Epic> getEpics() {
        return epics;
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteSubTasks() {
        subTasks.clear();
    }

    public void deleteEpics() {
        epics.clear();
    }

    public Task getTaskPerId(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }
        return null;
    }

    public Epic getEpicPerId(int id) {
        for (Epic epic : epics) {
            if (epic.getId() == id) {
                return epic;
            }
        }
        return null;
    }

    public SubTask getSubTaskPerId(int id) {
        for (SubTask subTask : subTasks) {
            if (subTask.getId() == id) {
                return subTask;
            }
        }
        return null;
    }

    public void createTask(Task task) {
        task.setId(++idCounter);
        tasks.add(task);
    }

    public void createEpic(Epic epic) {
        epic.setId(++idCounter);
        epics.add(epic);
        updateEpicStatus(epic.getId());
    }

    public void createSubtask(SubTask subtask, int epicId) {
        subtask.setId(++idCounter);
        subtask.setEpicId(epicId);
        subTasks.add(subtask);
        getEpicPerId(epicId).addSubTaskId(subtask.getId());
        updateEpicStatus(subtask.getEpicId());
    }

    public void updateTask(Task updateTask) {
        for (Task task : tasks) {
            if (task.equals(updateTask)) {
                task.setId(++idCounter);
                return;
            }
        }
    }

    public void updateEpic(Epic updateEpic) {
        for (Epic epic : epics) {
            if (epic.equals(updateEpic)) {
                epic.setId(++idCounter);
                return;
            }
        }
    }

    public void updateSubTask(SubTask updateSubTask) {
        for (SubTask subTask : subTasks) {
            if (subTask.equals(updateSubTask)) {
                subTask.setId(++idCounter);
            }
        }
    }

    public void deleteTaskPerId(int id) {
        tasks.remove(getTaskPerId(id));
    }

    public void deleteEpicPerId(int id) {
        Epic epic = getEpicPerId(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                deleteSubTaskPerId(id);
            }
        }
    }

    public void deleteSubTaskPerId(int id) {
        SubTask subTask = getSubTaskPerId(id);
        if (subTask != null) {
            subTasks.remove(subTask);
            updateEpicStatus(subTask.getEpicId());
        }
    }

    public ArrayList<SubTask> getSubtasksOfEpic(int epicId) {
        ArrayList<SubTask> subTaskList = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                subTaskList.add(subTasks.get(subtaskId));
            }
        }
        return subTaskList;
    }

    public void updateEpicStatus(int epicId) {
        Epic epic = getEpicPerId(epicId);
        if (epic == null || epic.getSubtaskIds().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allDone = true;
        boolean allNew = true;

        for (int subtaskId : epic.getSubtaskIds()) {
            SubTask subtask = getSubTaskPerId(subtaskId);
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
            if (subtask.getStatus() != Status.NEW) {
                allNew = false;
            }
        }

        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}
