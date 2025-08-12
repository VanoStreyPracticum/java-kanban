package ru.yandex.task_trecker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.task_trecker.task_data.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.task_trecker.service.Status.NEW;

@DisplayName("Тесты для InMemoryHistoryManager")
class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    private static final String TASK_NAME = "История";
    private static final String TASK_DESCRIPTION = "Проверка добавления";
    private static final int TASK_ID = 1;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    @DisplayName("Должен добавить задачу в историю при вызове add()")
    void testShouldAddTaskToHistory_WhenTaskAdded_ThenHistoryContainsIt() {
        Task task = new Task(TASK_NAME, TASK_DESCRIPTION, NEW, 1);
        task.setId(TASK_ID);

        historyManager.add(task);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }
}
