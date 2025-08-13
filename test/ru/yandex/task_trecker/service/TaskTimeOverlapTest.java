package ru.yandex.task_trecker.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.task_trecker.task_data.Task;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Тесты для пересечения временных интервалов")
class TaskTimeOverlapTest {

    @Test
    @DisplayName("Задачи не должны пересекаться во времени")
    void testTaskTimeOverlap_WhenIntervalsIntersect_ThenThrowException() {
        Task task1 = new Task("Task 1", "Description", Status.NEW, 1);
        task1.setStartTime(LocalTime.of(1, 0));

        Task task2 = new Task("Task 2", "Description", Status.NEW, 1);
        task2.setStartTime(LocalTime.of(1, 10));

        assertTrue(!task1.isOverlapping(task2), "Задачи не должны пересекаться");
    }
}

