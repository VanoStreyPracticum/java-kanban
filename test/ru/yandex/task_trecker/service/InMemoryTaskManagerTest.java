package ru.yandex.task_trecker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.task_trecker.task_data.Epic;
import ru.yandex.task_trecker.task_data.Subtask;
import ru.yandex.task_trecker.task_data.Task;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.task_trecker.service.Status.NEW;

@DisplayName("Тесты для InMemoryTaskManager")
class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    @Test
    @DisplayName("Должен создать и вернуть задачу по ID")
    void testShouldCreateAndRetrieveTask_WhenValid_ThenCorrectResult() {
        Task task = new Task("Task", "Desc", NEW, 1);
        task.setStartTime(LocalTime.of(0, 0));
        taskManager.createTask(task);
        Task retrieved = taskManager.getTaskPerId(task.getId());

        assertEquals(task, retrieved);
    }

    @Test
    @DisplayName("Не должен позволять подзадаче быть своим же эпиком")
    void testShouldNotAllowSubtaskToBeItsOwnEpic_WhenIdSame_ThenThrow() {
        Epic epic = new Epic("Epic", "Self-linked");
        epic.setStartTime(LocalTime.of(0, 0));
        Subtask sub = new Subtask("Sub", "Loop", NEW, 10);
        sub.setStartTime(LocalTime.of(1, 0));
        epic.setId(100);
        sub.setId(100);

        taskManager.createEpic(epic);

        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.createSubtask(sub, sub.getId());
        });
    }

    @Test
    @DisplayName("Подзадача не может иметь тот же ID, что и эпик")
    void testShouldNotAllowEpicToContainItself_WhenSubTaskIdMatches_ThenThrow() {
        Epic epic = new Epic("Epic", "Self-ref");
        epic.setStartTime(LocalTime.of(0, 0));
        taskManager.createEpic(epic);
        Subtask sub = new Subtask("Bad", "Self", NEW, 10);
        sub.setStartTime(LocalTime.of(1, 0));
        sub.setId(epic.getId());

        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.createSubtask(sub, epic.getId());
        });
    }

    @Test
    @DisplayName("Проверка, что задача сохраняется с корректными параметрами")
    void testTaskShouldRemainUnchangedAfterAddition_WhenRetrieved_ThenDataMatches() {
        Task task = new Task("Orig", "Copy", NEW, 10);
        task.setStartTime(LocalTime.of(0, 0));

        taskManager.createTask(task);
        Task retrieved = taskManager.getTaskPerId(task.getId());

        assertEquals(task.getName(), retrieved.getName());
        assertEquals(task.getDescription(), retrieved.getDescription());
        assertEquals(task.getStatus(), retrieved.getStatus());
    }
}
