package ru.yandex.task_trecker.task_data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void shouldEpicsBeEqualIfAllFieldsMatch() {
        Epic epic1 = new Epic("Epic", "Desc");
        Epic epic2 = new Epic("Epic", "Desc");
        epic1.setId(10);
        epic2.setId(10);
        assertEquals(epic1, epic2);
    }


    @Test
    void epicShouldStartWithEmptySubtaskList() {
        Epic epic = new Epic("Empty", "No subtasks");
        assertTrue(epic.getSubtaskIds().isEmpty(), "Список подзадач нового эпика должен быть пустым");
    }

    @Test
    void shouldAddSubtaskIdsToEpic() {
        Epic epic = new Epic("Container", "Tracks subtasks");
        epic.addSubTaskId(101);
        epic.addSubTaskId(202);
        assertEquals(2, epic.getSubtaskIds().size());
        assertTrue(epic.getSubtaskIds().contains(101));
        assertTrue(epic.getSubtaskIds().contains(202));
    }
}
