package ru.yandex.task_trecker.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.task_trecker.service.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final HttpServer server;
    private final Managers managers;
    private static final Gson gson = GsonConfig.buildGson();

    public HttpTaskServer(Managers managers) throws IOException {
        this.managers = managers;
        server = HttpServer.create(new InetSocketAddress(8080), 0);
        initContext();
    }

    private void initContext() {
        server.createContext("/tasks", new TasksHandler(managers));
        server.createContext("/epics", new EpicsHandler(managers));
        server.createContext("/subtasks", new SubtasksHandler(managers));
        server.createContext("/history", new HistoryHandler(managers));
        server.createContext("/prioritized", new PrioritizedHandler(managers));
    }

    public void start() {
        server.start();
        System.out.println("HTTP Task Server started on port 8080");
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP Task Server stopped");
    }

    public static Gson getGson() {
        return gson;
    }

    static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer(new Managers());
        server.start();
    }
}
