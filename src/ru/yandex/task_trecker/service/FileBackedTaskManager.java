package ru.yandex.task_trecker.service;

import ru.yandex.task_trecker.task_data.*;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    // Формат CSV заголовка
    private static final String CSV_HEADER = "id,type,name,status,description,epic";

    // Метод сохранения текущего состояния менеджера в файл
    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(CSV_HEADER);
            writer.newLine();

            // Сохраняем все задачи
            for (Task task: getTasks()) {
                writer.write(toString(task));
                writer.newLine();
            }

            // Сохраняем эпики
            for (Epic epic: getEpics()) {
                writer.write(toString(epic));
                writer.newLine();
            }

            // Сохраняем подзадачи
            for (Subtask subtask: getSubtasks()) {
                writer.write(toString(subtask));
                writer.newLine();
            }

            // Можно дополнительно сохранить историю. В ТЗ пока не было, но можно расширить.
            // Например, добавить пустую строку и затем сохранить историю ID через запятую.
            // Для простоты сейчас опустим.

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения задач в файл " + file.getName(), e);
        }
    }

    private String toString(Task task) {
        // Формируем строку CSV для любой задачи: TASK, EPIC, SUBTASK
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
                .append(",").append(task.getStatus())
                .append(",").append(task.getDescription())
                .append(",");

        if (task instanceof Subtask) {
            line.append(((Subtask) task).getEpicId());
        } else {
            // для Epic и Task поле epic пустое
            line.append("");
        }
        return line.toString();
    }

    // Переопределяем методы, изменяющие состояние, чтобы добавить вызов save()

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

    // Метод восстановления менеджера из файла
    public static FileBackedTaskManager loadFromFile(File file) {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        FileBackedTaskManager manager = new FileBackedTaskManager(historyManager, file);
        if (!file.exists() || file.length() == 0) {
            return manager; // пустой менеджер
        }

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.isEmpty() || !lines.get(0).equals(CSV_HEADER)) {
                throw new ManagerSaveException("Некорректный формат файла");
            }

            // Словари для связи эпиков и подзадач
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

                // Обновим счетчик id, чтобы не пересекались при создании новых задач
                if (task.getId() > manager.getIdCounter()) {
                    manager.setIdCounter(task.getId());
                }
            }
            // Связать подзадачи с эпиками (добавить subtask id в epic)
            for (Subtask subtask : manager.getSubtasks()) {
                Epic epic = epicMap.get(subtask.getEpicId());
                if (epic != null) {
                    epic.addSubtaskId(subtask.getId());
                } else {
                    throw new ManagerSaveException("Подзадача с id " + subtask.getId() + " лежит не в существующем эпике");
                }
            }

            // Обновить статусы эпиков после загрузки
            for (Epic epic : manager.getEpics()) {
                manager.updateEpicStatus(epic.getId());
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла " + file.getName(), e);
        }
        return manager;
    }

    // Метод преобразования строки в задачу
    public static Task fromString(String value) {
        String[] fields = value.split(",", -1); // -1 чтобы сохранить пустые поля
        if (fields.length < 6) {
            throw new IllegalArgumentException("Строка CSV неполная: " + value);
        }
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        String epicField = fields[5];

        switch (type) {
            case TASK:
                Task task = new Task(name, description, status);
                task.setId(id);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status); // На всякий случай устанавливаем статус
                return epic;
            case SUBTASK:
                Subtask subtask = new Subtask(name, description, status);
                subtask.setId(id);
                if (!epicField.isEmpty()) {
                    subtask.setEpicId(Integer.parseInt(epicField));
                } else {
                    throw new IllegalArgumentException("У подзадачи отсутствует epicId");
                }
                return subtask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

}
