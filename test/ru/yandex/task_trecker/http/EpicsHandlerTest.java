package ru.yandex.task_trecker.http;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import ru.yandex.task_trecker.service.*;
import ru.yandex.task_trecker.task_data.Epic;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EpicsHandlerTest {

    Managers manager = new Managers();
    HttpTaskServer taskServer;
    Gson gson = HttpTaskServer.getGson();

    public EpicsHandlerTest() throws IOException {
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
    @DisplayName("Добавление эпика через POST /epics")
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Epic description");
        epic.setStartTime(LocalTime.now());
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> epics = manager.getDefault().getEpics();
        assertEquals(1, epics.size());
        assertEquals("Epic 1", epics.get(0).getName());
    }

    @Test
    @DisplayName("Обновление эпика через POST /epics")
    public void testUpdateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 2", "Initial description");
        epic.setStartTime(LocalTime.now());
        manager.getDefault().createEpic(epic);

        epic.setName("Updated Epic");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Epic updated = manager.getDefault().getEpicPerId(epic.getId());
        assertEquals("Updated Epic", updated.getName());
    }

    @Test
    @DisplayName("Удаление эпика через DELETE /epics/{id}")
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 3", "To delete");
        epic.setStartTime(LocalTime.now());
        manager.getDefault().createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Epic> epics = manager.getDefault().getEpics();
        assertTrue(epics.isEmpty());
    }
}
