package ru.yandex.task_trecker.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.task_trecker.service.Managers;
import ru.yandex.task_trecker.task_data.Subtask;

import java.io.IOException;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final Managers manager;
    private final Gson gson = HttpTaskServer.getGson();

    public SubtasksHandler(Managers manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String[] parts = exchange.getRequestURI().getPath().split("/");
            switch (method) {
                case "GET":
                    if (parts.length == 3) handleGetById(exchange, parts[2]);
                    else handleGetAll(exchange);
                    break;
                case "POST":
                    handlePost(exchange, parts);
                    break;
                case "DELETE":
                    if (parts.length == 3) handleDeleteById(exchange, parts[2]);
                    else handleDeleteAll(exchange);
                    break;
                default:
                    sendServerError(exchange, new UnsupportedOperationException("Unsupported HTTP method"));
            }
        } catch (Exception e) {
            sendServerError(exchange, e);
        }
    }

    private void handleGetAll(HttpExchange exchange) throws IOException {
        List<Subtask> subtasks = manager.getDefault().getSubtasks();
        sendText(exchange, gson.toJson(subtasks));
    }

    private void handleGetById(HttpExchange exchange, String idStr) throws IOException {
        try {
            int id = Integer.parseInt(idStr);
            Subtask subtask = manager.getDefault().getSubTaskPerId(id);
            if (subtask != null) sendText(exchange, gson.toJson(subtask));
            else sendNotFound(exchange);
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }


    private void handlePost(HttpExchange exchange, String[] parts) throws IOException {
        Subtask subtask = gson.fromJson(new String(exchange.getRequestBody().readAllBytes()), Subtask.class);

        if (parts.length == 3) {
            int id;
            try {
                id = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                sendNotFound(exchange);
                return;
            }

            Subtask existing = manager.getDefault().getSubTaskPerId(id);
            if (existing != null) {
                subtask.setId(id);
                subtask.setEpicId(existing.getEpicId());
                manager.getDefault().updateSubtask(subtask);
                sendCreated(exchange, gson.toJson(subtask));
            } else {
                sendNotFound(exchange);
            }
        } else { // создание /subtasks
            if (manager.getDefault().getEpicPerId(subtask.getEpicId()) == null) {
                sendServerError(exchange, new IllegalArgumentException("Epic does not exist"));
                return;
            }
            manager.getDefault().createSubtask(subtask, subtask.getEpicId());
            sendCreated(exchange, gson.toJson(subtask));
        }
    }


    private void handleDeleteAll(HttpExchange exchange) throws IOException {
        manager.getDefault().deleteSubtasks();
        sendText(exchange, "{\"status\":\"All subtasks deleted\"}");
    }

    private void handleDeleteById(HttpExchange exchange, String idStr) throws IOException {
        try {
            int id = Integer.parseInt(idStr);
            if (manager.getDefault().getSubTaskPerId(id) != null) {
                manager.getDefault().deleteSubtaskPerId(id);
                sendText(exchange, "{\"status\":\"Subtask " + id + " deleted\"}");
            } else sendNotFound(exchange);
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }
}
