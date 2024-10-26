package http.handlers;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import manager.NotFoundException;

import java.io.IOException;

public class EpicHandler extends BaseHttpHandler {
    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getParameter(exchange);

        switch (endpoint) {
            case GET_BY_ID:
                try {
                    sendText(exchange, gson.toJson(taskManager.getEpicById(id)));
                } catch (NotFoundException notFoundException) {
                    sendNotFound(exchange, "Эпика с id =" + id + " нет");
                } catch (Exception exception) {
                    internalServerError(exchange, exception.getMessage());
                }
                break;
            case GET:
                try {
                    sendText(exchange, gson.toJson(taskManager.getAllEpics()));
                } catch (Exception exception) {
                    internalServerError(exchange, exception.getMessage());
                }
                break;
            case POST_BY_ID:
                try {
                    taskManager.updateEpic(newEpic);
                    sendSuccess(exchange, "Эпик с id =" + id + " обновлен");
                } catch (NotFoundException notFoundException) {
                    sendNotFound(exchange, "Эпика с id =" + newEpic.getId() + " нет");
                } catch (Exception exception) {
                    internalServerError(exchange, exception.getMessage());
                }
                break;
            case POST:
                try {
                    taskManager.createEpic(newEpic);
                    sendSuccess(exchange, "Эпик успешно добавлен");
                } catch (Exception exception) {
                    internalServerError(exchange, exception.getMessage());
                }
                break;
            case DELETE_BY_ID:
                try {
                    taskManager.removeEpicById(id);
                    sendSuccess(exchange, "Эпик с id =" + id + " удален");
                } catch (NotFoundException notFoundException) {
                    sendNotFound(exchange, "Эпик не найдена");
                } catch (Exception exception) {
                    internalServerError(exchange, exception.getMessage());
                }
                break;
            default:
                sendNotFound(exchange, "Такого эндпоинта нет");
        }
    }
}