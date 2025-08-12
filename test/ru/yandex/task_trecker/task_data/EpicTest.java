package ru.yandex.task_trecker.task_data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для Epic")
class EpicTest {

    @Test
    @DisplayName("Эпики равны, если совпадают все поля и ID")
    void testEpicsAreEqual_WhenFieldsMatch_ThenEqualsTrue() {
        Epic epic1 = new Epic("Epic", "Desc");
        Epic epic2 = new Epic("Epic", "Desc");
        epic1.setId(10);
        epic2.setId(10);
        epic1.setStartTime(epic2.getStartTime());
        assertEquals(epic1, epic2);
    }

    @Test
    @DisplayName("Новый эпик должен начинаться с пустым списком подзадач")
    void testEpicStartsWithEmptySubtaskList_WhenCreated_ThenEmptyList() {
        Epic epic = new Epic("Empty", "No subtasks");

        assertTrue(epic.getSubtaskIds().isEmpty(), "Список подзадач должен быть пустым");
    }

    @Test
    @DisplayName("Метод addSubTaskId должен корректно сохранять подзадачи")
    void testAddSubtaskIdsToEpic_WhenCalled_ThenStoredCorrectly() {
        Epic epic = new Epic("Container", "Tracks subtasks");

        epic.addSubtaskId(101);
        epic.addSubtaskId(202);

        assertEquals(2, epic.getSubtaskIds().size());
        assertTrue(epic.getSubtaskIds().contains(101));
        assertTrue(epic.getSubtaskIds().contains(202));
    }
}
