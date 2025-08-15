package ru.yandex.task_trecker.task_data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.task_trecker.service.Status;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для SubTask")
class SubtaskTest {

    @Test
    @DisplayName("Подзадачи считаются равными, если совпадают ID")
    void testSubTasksAreEqual_WhenIdsMatch_ThenEqualsTrue() {
        Subtask sub1 = new Subtask("Sub", "Desc", Status.NEW, 1);
        Subtask sub2 = new Subtask("Sub", "Desc", Status.NEW, 1);
        sub1.setId(1);
        sub2.setId(1);
        sub1.setStartTime(sub2.getStartTime());

        assertEquals(sub1, sub2);
    }
}
