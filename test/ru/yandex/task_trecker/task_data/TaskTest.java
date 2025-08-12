package ru.yandex.task_trecker.task_data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.task_trecker.service.Status;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для Task")
class TaskTest {

    private static final String NAME = "Test";
    private static final String DESCRIPTION = "Desc";
    private static final int ID = 1;

    @Test
    @DisplayName("Задачи считаются равными, если совпадают все поля")
    void testTasksAreEqual_WhenFieldsMatch_ThenEqualsTrue() {
        Task t1 = new Task(NAME, DESCRIPTION, Status.NEW, 1);
        Task t2 = new Task(NAME, DESCRIPTION, Status.NEW, 1);
        t1.setId(ID);
        t2.setId(ID);
        t1.setStartTime(t2.getStartTime());
        assertEquals(t1, t2);
    }
}
