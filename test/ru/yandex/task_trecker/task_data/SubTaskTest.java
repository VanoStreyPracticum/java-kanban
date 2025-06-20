package ru.yandex.task_trecker.task_data;

import org.junit.jupiter.api.Test;
import ru.yandex.task_trecker.service.Status;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    @Test
    void shouldSubtasksBeEqualIfIdsMatch() {
        SubTask sub1 = new SubTask("test", "Desc", Status.NEW);
        SubTask sub2 = new SubTask("test", "Desc", Status.NEW);
        sub1.setId(5);
        sub2.setId(5);
        assertEquals(sub1, sub2);
    }
}
