package http.handlers;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import manager.NotFoundException;
import manager.TimeOverlapException;

import java.io.IOException;

public class SubtaskHandler extends BaseHttpHandler {
    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getParameter(exchange);
        switch (endpoint) {
            case GET_BY_ID:
                try {
                    sendText(exchange, gson.toJson(taskManager.getSubtasksById(id)));
                } catch (NotFoundException notFoundException) {
                    sendNotFound(exchange, "Задачи с id =" + id + " нет");
                } catch (Exception exception) {
                    internalServerError(exchange, exception.getMessage());
                }
                break;
            case GET:
                try {
                    sendText(exchange, gson.toJson(taskManager.getAllSubtasks()));
                } catch (Exception exception) {
                    internalServerError(exchange, exception.getMessage());
                }
                break;
            case POST_BY_ID:
                try {
                    taskManager.updateSubtask(newSubTask);
                    sendSuccess(exchange, "Задача с id =" + id + " обновлена");
                } catch (TimeOverlapException timeOverlapException) {
                    sendHasInteractions(exchange);
                } catch (NotFoundException notFoundException) {
                    sendNotFound(exchange, "Задачи с id =" + newSubTask.getId() + " нет");
                } catch (Exception exception) {
                    internalServerError(exchange, exception.getMessage());
                }
                break;
            case POST:
                try {
                    taskManager.createSubtask(newSubTask);
                    sendSuccess(exchange, "Задача успешно добавлена");
                } catch (TimeOverlapException timeOverlapException) {
                    sendHasInteractions(exchange);
                } catch (Exception exception) {
                    internalServerError(exchange, exception.getMessage());
                }
                break;
            case DELETE_BY_ID:
                try {
                    taskManager.removeSubtaskById(id);
                    sendSuccess(exchange, "Задача с id =" + id + " удалена");
                } catch (NotFoundException notFoundException) {
                    sendNotFound(exchange, "Задача не найдена");
                } catch (Exception exception) {
                    internalServerError(exchange, exception.getMessage());
                }
                break;
            default:
                sendNotFound(exchange, "Такого эндпоинта нет");
        }
    }
}