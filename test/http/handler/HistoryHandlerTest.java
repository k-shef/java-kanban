package http.handler;

import http.HttpTaskServerTest;
import manager.TimeOverlapException;
import model.StatusTask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryHandlerTest extends HttpTaskServerTest {
    @Test
    protected void handle() throws IOException, InterruptedException, TimeOverlapException {
        Task task1 = new Task("Test1", "Test1", StatusTask.NEW,
                Duration.parse("PT1H30M"), LocalDateTime.of(2024, 1, 1, 0, 0));
        Task task2 = new Task("Test2", "Test2", StatusTask.NEW,
                Duration.parse("PT1H30M"), LocalDateTime.of(2024, 1, 1, 2, 0));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        URI url = URI.create("http://localhost:8089/prioritized");

        HttpRequest request = HttpRequest.newBuilder().uri(url).version(HttpClient.Version.HTTP_1_1).build();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = httpClient.send(request, handler);

        assertEquals(200, response.statusCode(), "Код ответа не совпадает с ожидаемым");
        assertEquals(2, response.body().split("},").length,
                "Получено неверное количество задач");
    }
}