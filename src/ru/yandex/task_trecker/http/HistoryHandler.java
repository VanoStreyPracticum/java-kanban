package ru.yandex.task_trecker.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.task_trecker.service.Managers;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final Managers manager;
    private final Gson gson = HttpTaskServer.getGson();

    public HistoryHandler(Managers manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                sendText(exchange, gson.toJson(manager.getDefaultHistory()));
            } else {
                sendServerError(exchange, new UnsupportedOperationException("Unsupported HTTP method"));
            }
        } catch (Exception e) {
            sendServerError(exchange, e);
        }
    }
}
