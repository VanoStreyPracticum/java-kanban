package ru.yandex.task_trecker.http;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import ru.yandex.task_trecker.service.*;
import ru.yandex.task_trecker.task_data.Epic;
import ru.yandex.task_trecker.task_data.Subtask;
import ru.yandex.task_trecker.service.Status;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SubtasksHandlerTest {

    Managers manager = new Managers();
    HttpTaskServer taskServer;
    Gson gson = HttpTaskServer.getGson();

    public SubtasksHandlerTest() throws IOException {
        taskServer = new HttpTaskServer(manager);
    }

    @BeforeEach
    public void setUp() {
        manager.setHistoryManager(new InMemoryHistoryManager());
        manager.setTaskManager(new InMemoryTaskManager(manager.getDefaultHistory()));
        manager.getDefault().deleteTasks();
        manager.getDefault().deleteSubtasks();
        manager.getDefault().deleteEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    @DisplayName("Добавление сабтаска через POST /subtasks")
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic for subtask", "Parent epic");
        epic.setStartTime(LocalTime.now());
        manager.getDefault().createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Desc", Status.NEW, 30);
        subtask.setStartTime(LocalTime.now());
        subtask.setEpicId(epic.getId());
        String json = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> subtasks = manager.getDefault().getSubtasks();
        assertEquals(1, subtasks.size());
        assertEquals("Subtask 1", subtasks.getFirst().getName());
    }

    @Test
    @DisplayName("Обновление сабтаска через POST /subtasks")
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic for subtask update", "Parent epic");
        epic.setStartTime(LocalTime.now());
        manager.getDefault().createEpic(epic);

        Subtask subtask = new Subtask("Subtask 2", "Initial", Status.NEW, 25);
        subtask.setStartTime(LocalTime.now());
        manager.getDefault().createSubtask(subtask, epic.getId());

        subtask.setName("Updated Subtask");
        String json = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Subtask updated = manager.getDefault().getSubTaskPerId(subtask.getId());
        assertEquals("Updated Subtask", updated.getName());
    }

    @Test
    @DisplayName("Удаление сабтаска через DELETE /subtasks/{id}")
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic for delete", "Parent epic");
        epic.setStartTime(LocalTime.now());
        manager.getDefault().createEpic(epic);

        Subtask subtask = new Subtask("Subtask 3", "To delete", Status.NEW, 20);
        subtask.setEpicId(epic.getId());
        subtask.setStartTime(LocalTime.now());
        manager.getDefault().createSubtask(subtask, epic.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Subtask> subtasks = manager.getDefault().getSubtasks();
        assertTrue(subtasks.isEmpty());
    }
}
