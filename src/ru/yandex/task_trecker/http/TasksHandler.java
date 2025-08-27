package ru.yandex.task_trecker.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.task_trecker.exceptions.OverlappingTaskException;
import ru.yandex.task_trecker.service.Managers;
import ru.yandex.task_trecker.task_data.Task;

import java.io.IOException;
import java.util.List;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {

    private final Managers manager;
    private final Gson gson = HttpTaskServer.getGson();

    public TasksHandler(Managers manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");

            switch (method) {
                case "GET":
                    if (parts.length == 3) {
                        handleGetById(exchange, parts[2]);
                    } else {
                        handleGetAll(exchange);
                    }
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    if (parts.length == 3) {
                        handleDeleteById(exchange, parts[2]);
                    } else {
                        handleDeleteAll(exchange);
                    }
                    break;
                default:
                    sendServerError(exchange, new UnsupportedOperationException("Unsupported HTTP method"));
            }
        } catch (OverlappingTaskException e) {
            sendHasOverlaps(exchange, e);
        } catch (Exception e) {
            sendServerError(exchange, e);
        }
    }

    private void handleGetAll(HttpExchange exchange) throws IOException {
        List<Task> tasks = manager.getDefault().getTasks();
        sendText(exchange, gson.toJson(tasks));
    }

    private void handleGetById(HttpExchange exchange, String idStr) throws IOException {
        try {
            int id = Integer.parseInt(idStr);
            Task task = manager.getDefault().getTaskPerId(id);
            if (task != null) {
                sendText(exchange, gson.toJson(task));
            } else {
                sendNotFound(exchange);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        Task task = gson.fromJson(new String(exchange.getRequestBody().readAllBytes()), Task.class);
        if (task.getId() != 0 && manager.getDefault().getTaskPerId(task.getId()) != null) {
            manager.getDefault().updateTask(task);
            sendText(exchange, gson.toJson(task));
        } else {
            manager.getDefault().createTask(task);
            sendCreated(exchange);
        }
    }

    private void handleDeleteAll(HttpExchange exchange) throws IOException {
        manager.getDefault().deleteTasks();
        sendText(exchange, "{\"status\":\"All tasks deleted\"}");
    }

    private void handleDeleteById(HttpExchange exchange, String idStr) throws IOException {
        try {
            int id = Integer.parseInt(idStr);
            if (manager.getDefault().getTaskPerId(id) != null) {
                manager.getDefault().deleteTaskPerId(id);
                sendText(exchange, "{\"status\":\"Task " + id + " deleted\"}");
            } else {
                sendNotFound(exchange);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }
}
