package ru.yandex.task_trecker.task_data;

import org.junit.jupiter.api.Test;
import ru.yandex.task_trecker.service.Status;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void shouldTasksBeEqualIfAllFieldsMatch() {
        Task t1 = new Task("Test", "Desc", Status.NEW);
        Task t2 = new Task("Test", "Desc", Status.NEW);
        t1.setId(1);
        t2.setId(1);
        assertEquals(t1, t2);
    }

}
