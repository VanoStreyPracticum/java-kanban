package ru.yandex.task_trecker.task_data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.task_trecker.service.Status;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для Task")
class TaskTest {
    @Test
    @DisplayName("Задачи считаются равными, если совпадают все поля")
    void testTasksAreEqual_WhenFieldsMatch_ThenEqualsTrue() {
        Task t1 = new Task("Task", "Desc", Status.NEW, 1);
        Task t2 = new Task("Task", "Desc", Status.NEW, 1);
        t1.setId(1);
        t2.setId(1);
        t1.setStartTime(t2.getStartTime());
        assertEquals(t1, t2);
    }
}
