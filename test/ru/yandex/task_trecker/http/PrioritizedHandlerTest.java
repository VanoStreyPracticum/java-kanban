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

public class PrioritizedHandlerTest {

    Managers manager = new Managers();
    HttpTaskServer taskServer;

    public PrioritizedHandlerTest() throws IOException {
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
    @DisplayName("Получение приоритетных задач через GET /prioritized")
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "First task", Status.NEW, 20);
        task1.setStartTime(LocalTime.now());
        Task task2 = new Task("Task 2", "Second task", Status.NEW, 15);
        task2.setStartTime(LocalTime.now().plusMinutes(50));

        manager.getDefault().createTask(task1);
        manager.getDefault().createTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String body = response.body();
        int index1 = body.indexOf("Task 1");
        int index2 = body.indexOf("Task 2");
        assertTrue(index1 < index2, "Задачи должны быть в приоритетном порядке по времени начала");
    }
}
