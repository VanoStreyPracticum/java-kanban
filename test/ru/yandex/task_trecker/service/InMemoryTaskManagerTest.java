package ru.yandex.task_trecker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.task_trecker.task_data.Epic;
import ru.yandex.task_trecker.task_data.Subtask;
import ru.yandex.task_trecker.task_data.Task;

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
        Task task = new Task("Task", "Desc", NEW);

        taskManager.createTask(task);
        Task retrieved = taskManager.getTaskPerId(task.getId());

        assertEquals(task, retrieved);
    }

    @Test
    @DisplayName("Не должен позволять подзадаче быть своим же эпиком")
    void testShouldNotAllowSubtaskToBeItsOwnEpic_WhenIdSame_ThenThrow() {
        Epic epic = new Epic("Epic", "Self-linked");
        Subtask sub = new Subtask("Sub", "Loop", NEW);
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
        // Given
        Epic epic = new Epic("Epic", "Self-ref");
        taskManager.createEpic(epic);
        Subtask sub = new Subtask("Bad", "Self", NEW);
        sub.setId(epic.getId());

        // Then
        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.createSubtask(sub, epic.getId());
        });
    }

    @Test
    @DisplayName("Проверка, что задача сохраняется с корректными параметрами")
    void testTaskShouldRemainUnchangedAfterAddition_WhenRetrieved_ThenDataMatches() {
        // Given
        Task task = new Task("Orig", "Copy", NEW);

        // When
        taskManager.createTask(task);
        Task retrieved = taskManager.getTaskPerId(task.getId());

        // Then
        assertEquals(task.getName(), retrieved.getName());
        assertEquals(task.getDescription(), retrieved.getDescription());
        assertEquals(task.getStatus(), retrieved.getStatus());
    }
}
