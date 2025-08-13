package ru.yandex.task_trecker.service;

import ru.yandex.task_trecker.task_data.*;

import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    private static final String CSV_HEADER = "id,type,name,description,status,duration,startTime,epic";

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(CSV_HEADER);
            writer.newLine();

            for (Task task : getTasks()) {
                writer.write(toString(task));
                writer.newLine();
            }
            for (Epic epic : getEpics()) {
                writer.write(toString(epic));
                writer.newLine();
            }
            for (Subtask subtask : getSubtasks()) {
                writer.write(toString(subtask));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения задач в файл " + file.getName(), e);
        }
    }

    private String toString(Task task) {
        StringBuilder line = new StringBuilder();

        line.append(task.getId()).append(",");
        if (task instanceof Epic) {
            line.append(TaskType.EPIC);
        } else if (task instanceof Subtask) {
            line.append(TaskType.SUBTASK);
        } else {
            line.append(TaskType.TASK);
        }
        line.append(",").append(task.getName())
                .append(",").append(task.getDescription())
                .append(",").append(task.getStatus())
                .append(",").append(task.getDuration())
                .append(",").append(task.getStartTime())
                .append(",");

        if (task instanceof Subtask) {
            line.append(((Subtask) task).getEpicId());
        }
        return line.toString();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask, int epicId) {
        super.createSubtask(subtask, epicId);
        save();
    }

    @Override
    public void updateTask(Task updateTask) {
        super.updateTask(updateTask);
        save();
    }

    @Override
    public void updateEpic(Epic updateEpic) {
        super.updateEpic(updateEpic);
        save();
    }

    @Override
    public void updateSubtask(Subtask updateSubtask) {
        super.updateSubtask(updateSubtask);
        save();
    }

    @Override
    public void deleteTaskPerId(int id) {
        super.deleteTaskPerId(id);
        save();
    }

    @Override
    public void deleteEpicPerId(int id) {
        super.deleteEpicPerId(id);
        save();
    }

    @Override
    public void deleteSubtaskPerId(int id) {
        super.deleteSubtaskPerId(id);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    public FileBackedTaskManager loadFromFile(File file) {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        FileBackedTaskManager manager = new FileBackedTaskManager(historyManager, file);

        if (!file.exists() || file.length() == 0) {
            return manager;
        }

        try {
            List<String> lines = Files.lines(file.toPath()).toList();

            if (lines.isEmpty() || !lines.getFirst().equals(CSV_HEADER)) {
                throw new ManagerSaveException("Некорректный формат файла");
            }

            Map<Integer, Epic> epicMap = new HashMap<>();

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isBlank()) {
                    continue;
                }
                Task task = fromString(line);
                if (task instanceof Epic) {
                    manager.getEpics().add((Epic) task);
                    epicMap.put(task.getId(), (Epic) task);
                } else if (task instanceof Subtask) {
                    manager.getSubtasks().add((Subtask) task);
                } else {
                    manager.getTasks().add(task);
                }
                if (task.getId() > manager.getIdCounter()) {
                    manager.setIdCounter(task.getId());
                }
            }

            for (Subtask subtask : manager.getSubtasks()) {
                Epic epic = epicMap.get(subtask.getEpicId());
                if (epic != null) {
                    epic.addSubtaskId(subtask.getId());
                } else {
                    throw new ManagerSaveException("Подзадача с id " + subtask.getId() + " лежит не в существующем эпике");
                }
            }

            for (Epic epic : manager.getEpics()) {
                manager.updateEpicStatus(epic.getId());
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла " + file.getName(), e);
        }
        return manager;
    }

    public Task fromString(String value) {
        String[] fields = value.split(",", -1);
        if (fields.length != CSV_HEADER.split(",").length) {
            throw new IllegalArgumentException("Некорректное количество полей в строке CSV: " + value);
        }

        try {
            int id = Integer.parseInt(fields[0]);
            TaskType type = TaskType.valueOf(fields[1]);
            String name = fields[2];
            String description = fields[3];
            Status status = Status.valueOf(fields[4]);
            long duration = Long.parseLong(fields[5]);
            LocalTime startTime = LocalTime.parse(fields[6], DateTimeFormatter.ofPattern("HH:mm:ss"));
            String epicField = fields[7];

            return switch (type) {
                case TASK -> createTaskInstance(id, name, status, description, duration, startTime);
                case EPIC -> createEpicInstance(id, name, status, description, duration, startTime);
                case SUBTASK -> createSubtaskInstance(id, name, status, description, duration, startTime, epicField);
                default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
            };
        } catch (Exception e) {
            throw new ManagerSaveException("Ошибка парсинга строки CSV: " + value, e);
        }
    }


    private Task createTaskInstance(int id, String name, Status status, String description, long duration, LocalTime startTime) {
        Task task = new Task(name, description, status, duration);
        task.setId(id);
        task.setStartTime(startTime);
        return task;
    }

    private Epic createEpicInstance(int id, String name, Status status, String description, long duration, LocalTime startTime) {
        Epic epic = new Epic(name, description);
        epic.setId(id);
        epic.setStatus(status);
        epic.setStartTime(startTime);
        epic.setDuration(duration);
        return epic;
    }

    private Subtask createSubtaskInstance(int id, String name, Status status, String description, long duration, LocalTime startTime, String epicField) {
        Subtask subtask = new Subtask(name, description, status, duration);
        subtask.setId(id);
        subtask.setStartTime(startTime);
        if (!epicField.isEmpty()) {
            subtask.setEpicId(Integer.parseInt(epicField));
        } else {
            throw new IllegalArgumentException("У подзадачи отсутствует epicId");
        }
        return subtask;
    }
}
