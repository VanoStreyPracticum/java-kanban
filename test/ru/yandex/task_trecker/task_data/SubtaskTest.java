package ru.yandex.task_trecker.task_data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.task_trecker.service.Status;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для SubTask")
class SubtaskTest {

    private static final String NAME = "Sub";
    private static final String DESCRIPTION = "Desc";
    private static final int ID = 5;

    @Test
    @DisplayName("Подзадачи считаются равными, если совпадают ID")
    void testSubTasksAreEqual_WhenIdsMatch_ThenEqualsTrue() {
        Subtask sub1 = new Subtask(NAME, DESCRIPTION, Status.NEW, 1);
        Subtask sub2 = new Subtask(NAME, DESCRIPTION, Status.NEW, 1);
        sub1.setId(ID);
        sub2.setId(ID);
        sub1.setStartTime(sub2.getStartTime());

        assertEquals(sub1, sub2);
    }
}
