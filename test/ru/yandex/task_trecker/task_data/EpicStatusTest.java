package ru.yandex.task_trecker.task_data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.task_trecker.service.*;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Тесты для расчёта статуса Epic")
class EpicStatusTest {
    TaskManager taskManager;

    @Test
    @BeforeEach
    void clearAllTasksInTaskManager() {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        taskManager.deleteTasks();
        taskManager.deleteEpics();
        taskManager.deleteSubtasks();
    }

    @Test
    @DisplayName("Все подзадачи со статусом NEW")
    void testEpicStatus_WhenAllSubtasksAreNew_ThenEpicStatusNew() {
        Epic epic = new Epic("Epic", "With new subtasks");
        Subtask subtask1 = new Subtask("Subtask 1", "Desc", Status.NEW, 1);
        Subtask subtask2 = new Subtask("Subtask 2", "Desc", Status.NEW, 2);
        epic.setStartTime(LocalTime.of(0, 0));
        subtask1.setStartTime(LocalTime.of(1, 0));
        subtask2.setStartTime(LocalTime.of(2, 0));
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1, epic.getId());
        taskManager.createSubtask(subtask2, epic.getId());

        assertEquals(Status.NEW, taskManager.getEpicPerId(epic.getId()).getStatus(), "Epic status should be NEW when all subtasks are NEW");
    }

    @Test
    @DisplayName("Все подзадачи со статусом DONE")
    void testEpicStatus_WhenAllSubtasksAreDone_ThenEpicStatusDone() {
        Epic epic = new Epic("Epic", "With done subtasks");
        Subtask subtask1 = new Subtask("Subtask 1", "Desc", Status.DONE, 1);
        Subtask subtask2 = new Subtask("Subtask 2", "Desc", Status.DONE, 2);
        epic.setStartTime(LocalTime.of(0, 0));
        subtask1.setStartTime(LocalTime.of(1, 0));
        subtask2.setStartTime(LocalTime.of(2, 0));
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1, epic.getId());
        taskManager.createSubtask(subtask2, epic.getId());

        assertEquals(Status.DONE, taskManager.getEpicPerId(epic.getId()).getStatus(), "Epic status should be DONE when all subtasks are DONE");
    }

    @Test
    @DisplayName("Подзадачи со статусами NEW и DONE")
    void testEpicStatus_WhenSubtasksAreNewAndDone_ThenEpicStatusInProgress() {
        Epic epic = new Epic("Epic", "With mixed subtasks");
        Subtask subtask1 = new Subtask("Subtask 1", "Desc", Status.NEW, 1);
        Subtask subtask2 = new Subtask("Subtask 2", "Desc", Status.DONE, 2);
        epic.setStartTime(LocalTime.of(0, 0));
        subtask1.setStartTime(LocalTime.of(1, 0));
        subtask2.setStartTime(LocalTime.of(2, 0));
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1, epic.getId());
        taskManager.createSubtask(subtask2, epic.getId());

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicPerId(epic.getId()).getStatus(), "Epic status should be IN_PROGRESS when subtasks are mixed");
    }

    @Test
    @DisplayName("Все подзадачи со статусом IN_PROGRESS")
    void testEpicStatus_WhenAllSubtasksAreInProgress_ThenEpicStatusInProgress() {
        Epic epic = new Epic("Epic", "With in-progress subtasks");
        Subtask subtask1 = new Subtask("Subtask 1", "Desc", Status.IN_PROGRESS, 1);
        Subtask subtask2 = new Subtask("Subtask 2", "Desc", Status.IN_PROGRESS, 2);
        epic.setStartTime(LocalTime.of(0, 0));
        subtask1.setStartTime(LocalTime.of(1, 0));
        subtask2.setStartTime(LocalTime.of(2, 0));
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1, epic.getId());
        taskManager.createSubtask(subtask2, epic.getId());

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicPerId(epic.getId()).getStatus(), "Epic status should be IN_PROGRESS when all subtasks are IN_PROGRESS");
    }
}

