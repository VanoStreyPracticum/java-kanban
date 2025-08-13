package ru.yandex.task_trecker.service;

import ru.yandex.task_trecker.task_data.*;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final HistoryManager historyManager;

    private final ArrayList<Task> tasks = new ArrayList<>();
    private final ArrayList<Epic> epics = new ArrayList<>();
    private final ArrayList<Subtask> subtasks = new ArrayList<>();
    private int idCounter = 0;

    private final Comparator<Task> taskComparator = Comparator
            .comparing(Task::getStartTime)
            .thenComparingInt(Task::getId);

    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(taskComparator);

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public int getIdCounter() {
        return idCounter;
    }

    public void setIdCounter(int idCounter) {
        this.idCounter = idCounter;
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
        tasks.forEach(task -> historyManager.remove(task.getId()));
        tasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        subtasks.forEach(subtask -> historyManager.remove(subtask.getId()));
        subtasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.forEach(epic -> {
            historyManager.remove(epic.getId());
            epic.getSubtaskIds().forEach(historyManager::remove);
        });
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Task getTaskPerId(int id) {
        return tasks.stream()
                .filter(task -> task.getId() == id)
                .peek(historyManager::add)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Epic getEpicPerId(int id) {
        return epics.stream()
                .filter(epic -> epic.getId() == id)
                .peek(historyManager::add)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Subtask getSubTaskPerId(int id) {
        return subtasks.stream()
                .filter(subtask -> subtask.getId() == id)
                .peek(historyManager::add)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void createTask(Task task) {
        if (checkOverlaps(task)) {
            throw new IllegalArgumentException("Task пересекается по времени с существующей");
        }
        task.setId(++idCounter);
        tasks.add(task);
        addTaskToPriorityMap(task);
    }

    @Override
    public void createEpic(Epic epic) {
        if (checkOverlaps(epic)) {
            throw new IllegalArgumentException("Epic пересекается по времени с существующей");
        }
        epic.setId(++idCounter);
        epics.add(epic);
        updateEpicStatus(epic.getId());
        addTaskToPriorityMap(epic);
    }

    @Override
    public void createSubtask(Subtask subtask, int epicId) {
        if (subtask == null) {
            throw new IllegalArgumentException("Subtask не может быть null");
        }

        if (subtask.getId() == epicId) {
            throw new IllegalArgumentException("Subtask не может быть своим же эпиком");
        }

        Epic epic = getEpicPerId(epicId);
        if (epic == null) {
            throw new IllegalArgumentException("Epic с ID " + epicId + " не найден");
        }

        if (checkOverlaps(subtask)) {
            throw new IllegalArgumentException("Subtask пересекается по времени с существующей");
        }

        subtask.setId(++idCounter);
        subtask.setEpicId(epicId);
        subtasks.add(subtask);
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epicId);
        addTaskToPriorityMap(subtask);
    }

    @Override
    public void updateTask(Task updateTask) {
        tasks.stream()
                .filter(task -> task.getId() == updateTask.getId())
                .findFirst()
                .ifPresent(existingTask -> {
                    if (checkOverlapsExcludingSelf(updateTask, existingTask)) {
                        throw new IllegalArgumentException("Новый Task пересекается по времени с другой задачей");
                    }
                    removeTaskFromPriorityMap(existingTask);
                    tasks.set(tasks.indexOf(existingTask), updateTask);
                    addTaskToPriorityMap(updateTask);
                });
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.stream()
                .filter(st -> st.getId() == subtask.getId())
                .findFirst()
                .ifPresent(existingSubtask -> {
                    if (checkOverlapsExcludingSelf(subtask, existingSubtask)) {
                        throw new IllegalArgumentException("Новый Subtask пересекается по времени с другой задачей");
                    }
                    removeTaskFromPriorityMap(existingSubtask);
                    subtasks.set(subtasks.indexOf(existingSubtask), subtask);
                    updateEpicStatus(subtask.getEpicId());
                    addTaskToPriorityMap(subtask);
                });
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.stream()
                .filter(e -> e.getId() == epic.getId())
                .findFirst()
                .ifPresent(existingEpic -> {
                    if (checkOverlapsExcludingSelf(epic, existingEpic)) {
                        throw new IllegalArgumentException("Новый Epic пересекается по времени с другой задачей");
                    }
                    removeTaskFromPriorityMap(existingEpic);
                    epics.set(epics.indexOf(existingEpic), epic);
                    updateEpicStatus(epic.getId());
                    addTaskToPriorityMap(epic);
                });
    }

    @Override
    public ArrayList<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = getEpicPerId(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }
        return epic.getSubtaskIds().stream()
                .map(this::getSubTaskPerId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void updateEpicStatus(int epicId) {
        Epic epic = getEpicPerId(epicId);
        if (epic == null) return;

        if (!epic.getSubtaskIds().isEmpty()) {
            epic.setStartTime(LocalTime.now());
        }

        final int[] done = {0};
        final int[] newCount = {0};
        final long[] duration = {0L};

        epic.getSubtaskIds().stream()
                .map(this::findSubTask)
                .filter(Objects::nonNull)
                .peek(sub -> {
                    duration[0] += sub.getDuration().toMinutes();
                    if (epic.getStartTime().isAfter(sub.getStartTime())) {
                        epic.setStartTime(sub.getStartTime());
                    }
                    if (sub.getStatus() == Status.DONE) done[0]++;
                    if (sub.getStatus() == Status.NEW) newCount[0]++;
                })
                .count();

        epic.setDuration(duration[0]);

        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else if (done[0] == epic.getSubtaskIds().size()) {
            epic.setStatus(Status.DONE);
        } else if (newCount[0] == epic.getSubtaskIds().size()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }


    private Subtask findSubTask(int id) {
        return subtasks.stream()
                .filter(sub -> sub.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void deleteTaskPerId(int id) {
        Task task = getTaskPerId(id);
        tasks.remove(task);
        historyManager.remove(id);
        removeTaskFromPriorityMap(task);
    }

    @Override
    public void deleteEpicPerId(int id) {
        Epic epicToRemove = getEpicPerId(id);
        if (epicToRemove != null) {
            epicToRemove.getSubtaskIds().forEach(subId -> {
                subtasks.removeIf(st -> st.getId() == subId);
                historyManager.remove(subId);
            });
            epics.removeIf(epic -> epic.getId() == id);
            historyManager.remove(id);
        }
        removeTaskFromPriorityMap(epicToRemove);
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
        removeTaskFromPriorityMap(sub);
    }

    private void addTaskToPriorityMap(Task task) {
        if (task != null) {
            prioritizedTasks.add(task);
        }
    }

    private void removeTaskFromPriorityMap(Task task) {
        if (task != null) {
            prioritizedTasks.remove(task);
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean checkOverlaps(Task newTask) {
        return getPrioritizedTasks().stream()
                .anyMatch(task -> task.isOverlapping(newTask));
    }

    private boolean checkOverlapsExcludingSelf(Task newTask, Task excludeTask) {
        return getPrioritizedTasks().stream()
                .filter(task -> task != excludeTask)
                .anyMatch(task -> task.isOverlapping(newTask));
    }
}