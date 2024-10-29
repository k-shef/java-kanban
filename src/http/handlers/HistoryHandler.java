package http.handlers;

import com.sun.net.httpserver.HttpExchange;
import model.Task;
import manager.TaskManager;

import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        String method = exchange.getRequestMethod();

        if ("GET".equals(method)) {
            try {
                List<Task> history = taskManager.getHistory();
                sendText(exchange, gson.toJson(history));
            } catch (Exception exception) {
                internalServerError(exchange, exception.getMessage());
            }
        } else {
            sendNotFound(exchange, "Такого эндпоинта нет");
        }
    }
}