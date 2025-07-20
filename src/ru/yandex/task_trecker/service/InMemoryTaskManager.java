package ru.yandex.task_trecker.service;

import ru.yandex.task_trecker.task_data.Epic;
import ru.yandex.task_trecker.task_data.Subtask;
import ru.yandex.task_trecker.task_data.Task;

import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {
    private final HistoryManager historyManager;

    private final ArrayList<Task> tasks = new ArrayList<>();
    private final ArrayList<Epic> epics = new ArrayList<>();
    private final ArrayList<Subtask> subtasks = new ArrayList<>();
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
    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public void deleteTasks() {
        for (Task task : tasks) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Subtask subtask : subtasks) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
    }

    @Override
    public void deleteEpics() {
        for (Epic epic : epics) {
            historyManager.remove(epic.getId());
            for (Integer subtaskId : epic.getSubtaskIds()) {
                historyManager.remove(subtaskId);
            }
        }
        epics.clear();
        subtasks.clear();
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
    public Subtask getSubTaskPerId(int id) {
        for (Subtask subTask : subtasks) {
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
    public void createSubtask(Subtask subtask, int epicId) {
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
        subtasks.add(subtask);
        epic.addSubtaskId(subtask.getId());
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
    public void updateSubtask(Subtask subtask) {
        for (int i = 0; i < subtasks.size(); i++) {
            if (subtasks.get(i).getId() == subtask.getId()) {
                subtasks.set(i, subtask);
                updateEpicStatus(subtask.getEpicId());
                return;
            }
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        for (int i = 0; i < epics.size(); i++) {
            if (epics.get(i).getId() == epic.getId()) {
                epics.set(i, epic);
                updateEpicStatus(epic.getId());
                return;
            }
        }
    }

    @Override
    public ArrayList<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = getEpicPerId(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }

        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = getSubTaskPerId(subtaskId);
            if (subtask != null) {
                subtasksOfEpic.add(subtask);
            }
        }

        return subtasksOfEpic;
    }


    public void updateEpicStatus(int epicId) {
        Epic epic = getEpicPerId(epicId);
        if (epic == null) return;

        int done = 0;
        int newCount = 0;
        for (Integer subId : epic.getSubtaskIds()) {
            Subtask sub = findSubTask(subId);
            if (sub != null) {
                switch (sub.getStatus()) {
                    case DONE -> done++;
                    case NEW -> newCount++;
                }
            }
        }

        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else if (done == epic.getSubtaskIds().size()) {
            epic.setStatus(Status.DONE);
        } else if (newCount == epic.getSubtaskIds().size()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private Subtask findSubTask(int id) {
        for (Subtask sub : subtasks) {
            if (sub.getId() == id) return sub;
        }
        return null;
    }

    @Override
    public void deleteTaskPerId(int id) {
        tasks.removeIf(task -> task.getId() == id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicPerId(int id) {
        Epic epicToRemove = getEpicPerId(id);
        if (epicToRemove != null) {
            for (Integer subId : epicToRemove.getSubtaskIds()) {
                subtasks.removeIf(st -> st.getId() == subId);
                historyManager.remove(subId);
            }
            epics.removeIf(epic -> epic.getId() == id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtaskPerId(int id) {
        Subtask sub = findSubTask(id);
        if (sub != null) {
            Epic epic = getEpicPerId(sub.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic.getId());
            }
            subtasks.removeIf(s -> s.getId() == id);
            historyManager.remove(id);
        }
    }
}
