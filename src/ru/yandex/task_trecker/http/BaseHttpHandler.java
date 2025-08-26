package ru.yandex.task_trecker.http;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.task_trecker.exceptions.OverlappingTaskException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(HttpURLConnection.HTTP_OK, resp.length); // 200
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendCreated(HttpExchange h) throws IOException {
        byte[] resp = new byte[0];
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(HttpURLConnection.HTTP_CREATED, resp.length); // 201
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        String text = "{\"error\":\"Resource not found\"}";
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, resp.length); // 404
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendHasOverlaps(HttpExchange h, OverlappingTaskException e) throws IOException {
        String text = "{\"error\":\"" + e.getMessage() + "\"}";
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(HttpURLConnection.HTTP_NOT_ACCEPTABLE, resp.length); // 406
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendServerError(HttpExchange h, Exception e) throws IOException {
        String text = "{\"error\":\"Internal server error: " + e.getMessage() + "\"}";
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, resp.length); // 500
        h.getResponseBody().write(resp);
        h.close();
    }
}
