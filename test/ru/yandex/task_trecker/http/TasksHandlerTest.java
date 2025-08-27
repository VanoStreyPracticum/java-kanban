package ru.yandex.task_trecker.http;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import ru.yandex.task_trecker.service.*;
import ru.yandex.task_trecker.task_data.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TasksHandlerTest {

    Managers manager = new Managers();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public TasksHandlerTest() throws IOException {
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
    @DisplayName("Добавление задачи через POST /tasks")
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Task description",
                Status.NEW, 30);
        task.setStartTime(LocalDateTime.now().toLocalTime());

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getDefault().getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    @DisplayName("Получение задач через GET /tasks")
    public void testGetTasks() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Task description",
                Status.NEW, 15);
        task.setStartTime(LocalTime.now());
        manager.getDefault().createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Test 2"));
    }

    @Test
    @DisplayName("Получение задачи по id через GET /tasks/{id}")
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test 4", "Get by id", Status.NEW, 25);
        task.setStartTime(LocalTime.now());
        manager.getDefault().createTask(task);

        int taskId = task.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Get by id"), "Задача не найдена по id");
    }

    @Test
    @DisplayName("Обновление задачи через PUT /tasks/{id}")
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Test 5", "Update me", Status.NEW, 40);
        task.setStartTime(LocalTime.now());
        manager.getDefault().createTask(task);

        task.setName("Updated name");
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task updated = manager.getDefault().getTaskPerId(task.getId());
        assertEquals("Updated name", updated.getName(), "Имя задачи не обновилось");
    }

    @Test
    @DisplayName("Удаление задачи через DELETE /tasks/{id}")
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test 3", "Task for delete",
                Status.NEW, 20);
        task.setStartTime(LocalTime.now());
        manager.getDefault().createTask(task);

        int taskId = task.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getDefault().getTasks();
        assertTrue(tasksFromManager.isEmpty(), "Задача не удалилась");
    }
}
