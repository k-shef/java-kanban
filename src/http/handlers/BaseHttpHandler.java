package http.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.adapter.DurationAdapter;
import http.adapter.LocalDateTimeAdapter;
import model.Epic;
import model.Subtask;
import model.Task;
import manager.TaskManager;
import manager.NotFoundException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler implements HttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;
    protected String method;
    protected int id;
    protected Task newTask;
    protected Epic newEpic;
    protected Subtask newSubtask;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Основная логика в подклассах
    }

    protected Endpoint getParameter(HttpExchange exchange) throws IOException {
        method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] stringId = path.split("/");

        switch (method) {
            case "GET":
                if (stringId.length > 2) {
                    try {
                        id = Integer.parseInt(stringId[2]);
                        return Endpoint.GET_BY_ID;
                    } catch (NumberFormatException e) {
                        throw new NotFoundException("Идентификатор не является числом");
                    }
                } else {
                    return Endpoint.GET;
                }
            case "POST":
                if (stringId[1].equals("tasks")) {
                    newTask = gson.fromJson(readText(exchange), Task.class);
                    id = newTask.getId();
                } else if (stringId[1].equals("epics")) {
                    newEpic = gson.fromJson(readText(exchange), Epic.class);
                    id = newEpic.getId();
                } else if (stringId[1].equals("subtasks")) {
                    newSubtask = gson.fromJson(readText(exchange), Subtask.class);
                    id = newSubtask.getId();
                }
                return id != 0 ? Endpoint.POST_BY_ID : Endpoint.POST;
            case "DELETE":
                if (stringId.length > 2) {
                    try {
                        id = Integer.parseInt(stringId[2]);
                        return Endpoint.DELETE_BY_ID;
                    } catch (NumberFormatException e) {
                        throw new NotFoundException("Идентификатор не является числом");
                    }
                } else {
                    sendNotFound(exchange, "не был передан id для удаления");
                }
                break;
            default:
                sendNotFound(exchange, "Такого эндпоинта нет");
        }
        return Endpoint.UNKNOWN;
    }

    protected String readText(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    protected void sendText(HttpExchange exchange, String responseString) {
        try {
            byte[] resp = responseString.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(200, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (IOException e) {
            System.err.println("Ошибка отправки ответа: заголовки уже были отправлены.");
        } finally {
            exchange.close();
        }
    }

    protected void sendSuccess(HttpExchange exchange, String responseString) {
        try {
            byte[] resp = responseString.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(201, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (IOException e) {
            System.err.println("Ошибка отправки ответа: заголовки уже были отправлены.");
        } finally {
            exchange.close();
        }
    }

    protected void sendNotFound(HttpExchange exchange, String responseString) {
        try {
            byte[] resp = responseString.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(404, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (IOException e) {
            System.err.println("Ошибка отправки ответа: заголовки уже были отправлены.");
        } finally {
            exchange.close();
        }
    }

    protected void sendHasInteractions(HttpExchange exchange) {
        try {
            byte[] resp = "Задача пересекается по времени".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(406, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (IOException e) {
            System.err.println("Ошибка отправки ответа: заголовки уже были отправлены.");
        } finally {
            exchange.close();
        }
    }

    protected void internalServerError(HttpExchange exchange, String text) {
        try {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(500, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (IOException e) {
            System.err.println("Ошибка отправки ответа: заголовки уже были отправлены.");
        } finally {
            exchange.close();
        }
    }
}
