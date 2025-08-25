package ru.yandex.task_trecker.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.task_trecker.service.Managers;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final Managers manager;
    private final Gson gson = HttpTaskServer.getGson();

    public PrioritizedHandler(Managers manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                sendText(exchange, gson.toJson(manager.getDefault().getPrioritizedTasks()));
            } else {
                sendServerError(exchange, new UnsupportedOperationException("Unsupported HTTP method"));
            }
        } catch (Exception e) {
            sendServerError(exchange, e);
        }
    }
}
