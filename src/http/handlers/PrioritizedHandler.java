package http.handlers;

import com.sun.net.httpserver.HttpExchange;
import model.Task;
import manager.TaskManager;

import java.util.TreeSet;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        String method = exchange.getRequestMethod();

        if ("GET".equals(method)) {
            try {
                TreeSet<Task> setTask = taskManager.getPrioritizedTasks();
                sendText(exchange, gson.toJson(setTask));
            } catch (Exception exception) {
                internalServerError(exchange, exception.getMessage());
            }
        } else {
            sendNotFound(exchange, "Такого эндпоинта нет");
        }
    }
}