package ru.yandex.task_trecker;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private int idCounter = 1;

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public void deleteTasks() {
        tasks = new HashMap<>();
    }

    public void deleteSubTasks() {
        subTasks = new HashMap<>();
    }

    public void deleteEpics() {
        epics = new HashMap<>();
    }

    public Task getTaskPerId(int id) {
        return tasks.get(id);
    }

    public Epic getEpicPerId(int id) {
        return epics.get(id);
    }

    public SubTask getSubTaskPerId(int id) {
        return subTasks.get(id);
    }

    public void createTask(Task task) {
        task.id = idCounter++;
        tasks.put(task.id, task);
    }

    public void createEpic(Epic epic) {
        epic.id = idCounter++;
        epics.put(epic.id, epic);
    }

    public void createSubtask(SubTask subtask) {
        subtask.id = idCounter++;
        subTasks.put(subtask.id, subtask);
        epics.get(subtask.getEpicId()).getSubtaskIds().add(subtask.id);
        updateEpicStatus(subtask.getEpicId());
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.id)) {
            tasks.put(task.id, task);
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.id)) {
            epics.put(epic.id, epic);
        }
    }

    public void updateSubTask(SubTask subtask) {
        if (subTasks.containsKey(subtask.id)) {
            subTasks.put(subtask.id, subtask);
            updateEpicStatus(subtask.getEpicId());
        }
    }

    public void deleteTaskPerId(int id) {
        tasks.remove(id);
    }

    public void deleteEpicPerId(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                subTasks.remove(subtaskId);
            }
        }
    }

    public void deleteSubTaskPerId(int id) {
        SubTask subtask = subTasks.remove(id);
        if (subtask != null) {
            updateEpicStatus(subtask.getEpicId());
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

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null || epic.getSubtaskIds().isEmpty()) {
            epic.status = Status.NEW;
            return;
        }

        boolean allDone = true;
        boolean allNew = true;

        for (int subtaskId : epic.getSubtaskIds()) {
            SubTask subtask = subTasks.get(subtaskId);
            if (subtask.status != Status.DONE) {
                allDone = false;
            }
            if (subtask.status != Status.NEW) {
                allNew = false;
            }
        }

        if (allNew) {
            epic.status = Status.NEW;
        } else if (allDone) {
            epic.status = Status.DONE;
        } else {
            epic.status = Status.IN_PROGRESS;
        }
    }
}
