package http.handler;

import http.HttpTaskServerTest;
import model.Epic;
import model.StatusTask;
import model.Subtask;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import static org.junit.jupiter.api.Assertions.*;


public class SubtaskHandlerTest extends HttpTaskServerTest {
    @Test
    public void getSubtaskTest() throws InterruptedException, IOException {
        Epic epic1 = new Epic("Test Epic1", "Test", StatusTask.NEW);
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Test Subtask1", "Test", StatusTask.NEW, epic1.getId(), Duration.parse("PT1H30M"),
                LocalDateTime.of(2024, 1, 1, 4, 0));
        String taskJson = gson.toJson(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8089/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        subtask1.setId(2);

        request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8089/subtasks/2")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа не совпадает с ожидаемым");

        Subtask taskOfGet = gson.fromJson(response.body(), Subtask.class);

        assertEquals(taskOfGet, subtask1,
                "Задача прошла конвертацию через Json не верно, или была получена не та задача");

        request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8089/subtasks/3")).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Код ответа не совпадает с ожидаемым");
    }

    @Test
    public void getSubtasksTest() throws InterruptedException, IOException {
        Epic epic1 = new Epic("Test Epic1", "Test", StatusTask.NEW);
        taskManager.createEpic(epic1);

        Subtask subTask1 = new Subtask("Test Subtask1", "Test", StatusTask.NEW, epic1.getId(), Duration.parse("PT1H30M"),
                LocalDateTime.of(2024, 1, 1, 4, 0));
        Subtask subTask2 = new Subtask("Test Subtask2", "Test", StatusTask.NEW, epic1.getId(), Duration.parse("PT1H30M"),
                LocalDateTime.of(2024, 1, 1, 6, 0));

        String taskJson1 = gson.toJson(subTask1);
        String taskJson2 = gson.toJson(subTask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8089/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа не совпадает с ожидаемым");

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        assertTrue(jsonElement.isJsonArray(), "Получен не список");
        assertEquals(jsonArray.size(), 2, "Получено неверное количество задач");
    }

    @Test
    public void addSubtaskTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test Epic1", "Test", StatusTask.NEW);
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Test Subtask1", "Test", StatusTask.NEW, epic1.getId(), Duration.parse("PT1H30M"),
                LocalDateTime.of(2024, 1, 1, 4, 0));

        String taskJson = gson.toJson(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8089/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не совпадает с ожидаемым");

        List<Subtask> tasksFromManager = taskManager.getAllSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test Subtask1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");

        Subtask subtask2 = new Subtask("Test Subtask2", "Test", StatusTask.NEW, epic1.getId(), Duration.parse("PT1H30M"),
                LocalDateTime.of(2024, 1, 1, 4, 30));

        String taskJson1 = gson.toJson(subtask2);

        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        HttpResponse<String> response1 = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response1.statusCode(), "Код ответа не совпадает с ожидаемым");
    }

    @Test
    public void updateSubtaskTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test Epic1", "Test", StatusTask.NEW);
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Test Subtask1", "Test", StatusTask.NEW, epic1.getId(),
                Duration.parse("PT1H30M"),
                LocalDateTime.of(2024, 1, 1, 4, 0));

        Subtask subtask2 = new Subtask("Test Subtask2", "Test", StatusTask.NEW, epic1.getId(),
                Duration.parse("PT1H30M"),
                LocalDateTime.of(2024, 1, 1, 6, 0));

        String taskJson1 = gson.toJson(subtask1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8089/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int firstSubtaskId = 1;
        subtask2.setId(firstSubtaskId);

        // Отправляем второй сабтаск, который должен перезаписать первый
        String taskJson2 = gson.toJson(subtask2);
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не совпадает с ожидаемым");

        List<Subtask> tasksFromManager = taskManager.getAllSubtasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void deleteSubtaskTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test Epic1", "Test", StatusTask.NEW);
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Test Subtask1", "Test", StatusTask.NEW, epic1.getId(), Duration.parse("PT1H30M"),
                LocalDateTime.of(2024, 1, 1, 4, 0));
        Subtask subtask2 = new Subtask("Test Subtask2", "Test", StatusTask.NEW, epic1.getId(), Duration.parse("PT1H30M"),
                LocalDateTime.of(2024, 1, 1, 6, 0));

        String taskJson1 = gson.toJson(subtask1);
        String taskJson2 = gson.toJson(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8089/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8089/subtasks/2"))
                .DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не совпадает с ожидаемым");

        List<Subtask> tasksFromManager = taskManager.getAllSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }
}