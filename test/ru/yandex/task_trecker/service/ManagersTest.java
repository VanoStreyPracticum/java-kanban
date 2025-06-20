package ru.yandex.task_trecker.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для Managers")
class ManagersTest {

    @Test
    @DisplayName("Должен возвращать не null для TaskManager и HistoryManager")
    void testShouldReturnInitializedManagers_WhenCalled_ThenValidInstances() {

        Managers managers = new Managers();

        assertNotNull(managers.getDefault());
        assertNotNull(managers.getDefaultHistory());
    }
}
