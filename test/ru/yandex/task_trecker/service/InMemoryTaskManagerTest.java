package ru.yandex.task_trecker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.task_trecker.task_data.Epic;
import ru.yandex.task_trecker.task_data.SubTask;
import ru.yandex.task_trecker.task_data.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.task_trecker.service.Status.NEW;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void setup() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void shouldCreateAndRetrieveTask() {
        Task task = new Task("Task", "Desc", NEW);
        taskManager.createTask(task);
        Task fetched = taskManager.getTaskPerId(task.getId());
        assertEquals(task, fetched);
    }

    @Test
    void shouldNotAllowSubtaskToBeItsOwnEpic() {
        Epic epic = new Epic("Epic", "Self-linked");
        SubTask sub = new SubTask("Sub", "Loop", Status.NEW);

        epic.setId(100);
        sub.setId(100);

        taskManager.createEpic(epic); // Важно: сначала создать эпик

        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.createSubtask(sub, sub.getId()); // Тут должен сработать запрет
        });
    }


    @Test
    void shouldNotAllowEpicToContainItself() {
        Epic epic = new Epic("Epic", "Self-ref");
        taskManager.createEpic(epic);

        SubTask sub = new SubTask("Bad", "Self", Status.NEW);
        sub.setId(epic.getId());

        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.createSubtask(sub, epic.getId());
        });
    }


    @Test
    void taskShouldRemainUnchangedAfterAddition() {
        Task task = new Task("Orig", "Copy", NEW);
        taskManager.createTask(task);
        Task retrieved = taskManager.getTaskPerId(task.getId());
        assertEquals(task.getName(), retrieved.getName());
        assertEquals(task.getDescription(), retrieved.getDescription());
        assertEquals(task.getStatus(), retrieved.getStatus());
    }
}
