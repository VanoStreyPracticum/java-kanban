package ru.yandex.task_trecker.http;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import ru.yandex.task_trecker.service.*;
import ru.yandex.task_trecker.task_data.Task;
import ru.yandex.task_trecker.service.Status;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryHandlerTest {

    Managers manager = new Managers();
    HttpTaskServer taskServer;

    public HistoryHandlerTest() throws IOException {
        taskServer = new HttpTaskServer(manager);
    }

    @BeforeEach
    public void setUp() {
        manager.setHistoryManager(new InMemoryHistoryManager());
        manager.setTaskManager(new InMemoryTaskManager(manager.getDefaultHistory()));
        manager.getDefault().deleteTasks();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    @DisplayName("Получение истории через GET /history")
    public void testGetHistory() throws IOException, InterruptedException {
        Task task = new Task("Task History", "Desc", Status.NEW, 20);
        task.setStartTime(LocalTime.now());
        manager.getDefault().createTask(task);
        manager.getDefault().getTaskPerId(task.getId()); // чтобы появилась в истории

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Task History"));
    }
}
