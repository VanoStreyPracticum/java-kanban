package ru.yandex.task_trecker.service;

import ru.yandex.task_trecker.task_data.Epic;
import ru.yandex.task_trecker.task_data.SubTask;
import ru.yandex.task_trecker.task_data.Task;

import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {
    private final HistoryManager historyManager ;

    private final ArrayList<Task> tasks = new ArrayList<>();
    private final ArrayList<Epic> epics = new ArrayList<>();
    private final ArrayList<SubTask> subTasks = new ArrayList<>();
    private int idCounter = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return epics;
    }

    @Override
    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteSubTasks() {
        subTasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.clear();
    }

    @Override
    public Task getTaskPerId(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                historyManager.add(task);
                return task;
            }
        }
        return null;
    }

    @Override
    public Epic getEpicPerId(int id) {
        for (Epic epic : epics) {
            if (epic.getId() == id) {
                historyManager.add(epic);
                return epic;
            }
        }
        return null;
    }

    @Override
    public SubTask getSubTaskPerId(int id) {
        for (SubTask subTask : subTasks) {
            if (subTask.getId() == id) {
                historyManager.add(subTask);
                return subTask;
            }
        }
        return null;
    }

    @Override
    public void createTask(Task task) {
        task.setId(++idCounter);
        tasks.add(task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(++idCounter);
        epics.add(epic);
        updateEpicStatus(epic.getId());
    }

    @Override
    public void createSubtask(SubTask subtask, int epicId) {
        if (subtask == null) {
            throw new IllegalArgumentException("Подзадача не может быть null");
        }

        if (subtask.getId() == epicId) {
            throw new IllegalArgumentException("Subtask не может быть своим же эпиком");
        }

        Epic epic = getEpicPerId(epicId);
        if (epic == null) {
            throw new IllegalArgumentException("Epic с ID " + epicId + " не найден");
        }

        subtask.setId(++idCounter);
        subtask.setEpicId(epicId);
        subTasks.add(subtask);
        epic.addSubTaskId(subtask.getId());
        updateEpicStatus(epicId);
    }


    @Override
    public void updateTask(Task updateTask) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == updateTask.getId()) {
                tasks.set(i, updateTask);
                return;
            }
        }
    }

    @Override
    public void updateEpic(Epic updateEpic) {
        for (int i = 0; i < epics.size(); i++) {
            if (epics.get(i).getId() == updateEpic.getId()) {
                epics.set(i, updateEpic);
                return;
            }
        }
    }

    @Override
    public void updateSubTask(SubTask updateSubTask) {
        for (int i = 0; i < subTasks.size(); i++) {
            if (subTasks.get(i).getId() == updateSubTask.getId()) {
                subTasks.set(i, updateSubTask);
                return;
            }
        }
    }

    @Override
    public void deleteTaskPerId(int id) {
        tasks.remove(getTaskPerId(id));
    }

    @Override
    public void deleteEpicPerId(int id) {
        Epic epic = getEpicPerId(id);
        if (epic != null) {
            for (int subTaskId : new ArrayList<>(epic.getSubtaskIds())) {
                deleteSubTaskPerId(subTaskId);
            }
            epics.remove(epic);
        }
    }

    @Override
    public void deleteSubTaskPerId(int id) {
        SubTask subTask = getSubTaskPerId(id);
        if (subTask != null) {
            subTasks.remove(subTask);
            Epic epic = getEpicPerId(subTask.getEpicId());
            if (epic != null) {
                epic.getSubtaskIds().remove(Integer.valueOf(subTask.getId())); // удаление по значению, не индексу
                updateEpicStatus(epic.getId());
            }
        }
    }


    @Override
    public ArrayList<SubTask> getSubtasksOfEpic(int epicId) {
        ArrayList<SubTask> subTaskList = new ArrayList<>();
        Epic epic = getEpicPerId(epicId);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                subTaskList.add(getSubTaskPerId(subtaskId));
            }
        }
        return subTaskList;
    }

    @Override
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
