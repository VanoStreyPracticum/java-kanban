package ru.yandex.task_trecker.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.task_trecker.exceptions.OverlappingTaskException;
import ru.yandex.task_trecker.service.Managers;
import ru.yandex.task_trecker.task_data.Epic;

import java.io.IOException;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final Managers manager;
    private final Gson gson = HttpTaskServer.getGson();

    public EpicsHandler(Managers manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String[] parts = exchange.getRequestURI().getPath().split("/");

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
        List<Epic> epics = manager.getDefault().getEpics();
        sendText(exchange, gson.toJson(epics));
    }

    private void handleGetById(HttpExchange exchange, String idStr) throws IOException {
        try {
            int id = Integer.parseInt(idStr);
            Epic epic = manager.getDefault().getEpicPerId(id);
            if (epic != null) {
                sendText(exchange, gson.toJson(epic));
            } else {
                sendNotFound(exchange);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        Epic epic = gson.fromJson(new String(exchange.getRequestBody().readAllBytes()), Epic.class);
        if (epic.getId() != 0 && manager.getDefault().getEpicPerId(epic.getId()) != null) {
            manager.getDefault().updateEpic(epic);
            sendText(exchange, gson.toJson(epic));
        } else {
            manager.getDefault().createEpic(epic);
            sendCreated(exchange);
        }
    }

    private void handleDeleteAll(HttpExchange exchange) throws IOException {
        manager.getDefault().deleteEpics();
        sendText(exchange, "{\"status\":\"All epics deleted\"}");
    }

    private void handleDeleteById(HttpExchange exchange, String idStr) throws IOException {
        try {
            int id = Integer.parseInt(idStr);
            if (manager.getDefault().getEpicPerId(id) != null) {
                manager.getDefault().deleteEpicPerId(id);
                sendText(exchange, "{\"status\":\"Epic " + id + " deleted\"}");
            } else {
                sendNotFound(exchange);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }
}
