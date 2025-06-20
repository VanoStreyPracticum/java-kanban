package ru.yandex.task_trecker.service;

import org.junit.jupiter.api.Test;
import ru.yandex.task_trecker.service.Managers;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void shouldReturnInitializedManagers() {
        assertNotNull(Managers.getDefault());
        assertNotNull(Managers.getDefaultHistory());
    }
}
