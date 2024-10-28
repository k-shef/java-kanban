package http.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import http.HttpTaskServerTest;
import manager.TimeOverlapException;
import model.Epic;

import model.StatusTask;
import model.Subtask;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class EpicHandlerTest extends HttpTaskServerTest {

    @Test
    public void getEpicTest() throws InterruptedException, IOException {
        Epic epic1 = new Epic("Test Epic1", "Test", StatusTask.NEW);

        epic1.setStartTime(LocalDateTime.of(2024, 1, 1, 4, 0));
        epic1.setEndTime(LocalDateTime.of(2024, 1, 1, 5, 30));
        epic1.setDuration(Duration.ofMinutes(90));
        String taskJson = gson.toJson(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8089/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        epic1.setId(1);

        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8089/epics/1")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа не совпадает с ожидаемым");

        Epic taskOfGet = gson.fromJson(response.body(), Epic.class);

        assertEquals(taskOfGet, epic1,
                "Задача прошла конвертацию через Json не верно, или была получена не та задача");

        request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8089/epics/3")).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Код ответа не совпадает с ожидаемым");
    }

    @Test
    public void getEpicsTest() throws InterruptedException, IOException {
        Epic epic1 = new Epic("Test Epic1", "Test", StatusTask.NEW);

        epic1.setStartTime(LocalDateTime.of(2024, 1, 1, 4, 0));
        epic1.setEndTime(LocalDateTime.of(2024, 1, 1, 5, 30));
        epic1.setDuration(Duration.parse("PT1H30M"));
        String taskJson = gson.toJson(epic1);

        Epic epic2 = new Epic("Test Epic2", "Test", StatusTask.NEW);

        epic1.setStartTime(LocalDateTime.of(2024, 1, 1, 0, 0));
        epic1.setEndTime(LocalDateTime.of(2024, 1, 1, 1, 30));
        epic1.setDuration(Duration.parse("PT1H30M"));
        String taskJson1 = gson.toJson(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8089/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        epic1.setId(1);
        epic2.setId(2);

        request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа не совпадает с ожидаемым");

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        assertTrue(jsonElement.isJsonArray(), "Получен не список");
        assertEquals(jsonArray.size(), 2, "Получено неверное количество задач");
    }

    @Test
    public void addEpicTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test Epic1", "Test", StatusTask.NEW);

        epic1.setStartTime(LocalDateTime.of(2024, 1, 1, 4, 0));
        epic1.setEndTime(LocalDateTime.of(2024, 1, 1, 5, 30));
        epic1.setDuration(Duration.parse("PT1H30M"));
        String taskJson = gson.toJson(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8089/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        epic1.setId(1);
        assertEquals(201, response.statusCode(), "Код ответа не совпадает с ожидаемым");

        List<Epic> tasksFromManager = taskManager.getAllEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test Epic1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void updateEpicTest() throws IOException, InterruptedException, TimeOverlapException {
        Epic epic = new Epic("Test Epic", "Test", StatusTask.NEW);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test SubTask1", "Test", StatusTask.NEW, epic.getId(), Duration.parse("PT1H30M"),
                LocalDateTime.of(2024, 1, 1, 4, 10));
        taskManager.createSubtask(subtask);

        Epic epic1 = new Epic("Test Epic1", "Test", StatusTask.NEW);
        epic1.setId(epic.getId());
        epic1.setStartTime(LocalDateTime.of(2024, 1, 1, 4, 10));
        epic1.setEndTime(LocalDateTime.of(2024, 1, 1, 4, 40));
        epic1.setDuration(Duration.parse("PT30M"));
        String taskJson = gson.toJson(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8089/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не совпадает с ожидаемым");

        List<Epic> tasksFromManager = taskManager.getAllEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void deleteEpicTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test Epic1", "Test", StatusTask.NEW);

        epic1.setStartTime(LocalDateTime.of(2024, 1, 1, 4, 0));
        epic1.setEndTime(LocalDateTime.of(2024, 1, 1, 5, 30));
        epic1.setDuration(Duration.parse("PT1H30M"));
        String taskJson = gson.toJson(epic1);

        Epic epic2 = new Epic("Test Epic2", "Test", StatusTask.NEW);

        epic1.setStartTime(LocalDateTime.of(2024, 1, 1, 0, 0));
        epic1.setEndTime(LocalDateTime.of(2024, 1, 1, 1, 30));
        epic1.setDuration(Duration.parse("PT1H30M"));
        String taskJson1 = gson.toJson(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8089/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        epic1.setId(1);
        epic2.setId(2);

        request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8089/epics/2"))
                .DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не совпадает с ожидаемым");

        List<Epic> tasksFromManager = taskManager.getAllEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }
}