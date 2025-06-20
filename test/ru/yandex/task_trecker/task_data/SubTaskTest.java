package ru.yandex.task_trecker.task_data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.task_trecker.service.Status;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для SubTask")
class SubTaskTest {

    private static final String NAME = "Sub";
    private static final String DESCRIPTION = "Desc";
    private static final int ID = 5;

    @Test
    @DisplayName("Подзадачи считаются равными, если совпадают ID")
    void testSubTasksAreEqual_WhenIdsMatch_ThenEqualsTrue() {
        SubTask sub1 = new SubTask(NAME, DESCRIPTION, Status.NEW);
        SubTask sub2 = new SubTask(NAME, DESCRIPTION, Status.NEW);
        sub1.setId(ID);
        sub2.setId(ID);

        assertEquals(sub1, sub2);
    }
}
