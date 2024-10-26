package http.handler;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import http.HttpTaskServerTest;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskHandlerTest extends HttpTaskServerTest {
    @Test
    public void getTaskTest() throws InterruptedException, IOException {
        Task task1 = new Task("Test1", "Test1", StatusTask.NEW,
                Duration.ofMinutes(90), LocalDateTime.of(2024, 1, 1, 0, 0));
        //task1.setId(1);
        Task task2 = new Task("Test2", "Test2", StatusTask.NEW,
                Duration.ofMinutes(90), LocalDateTime.of(2024, 1, 1, 2, 0));
        //task2.setId(2);

        String taskJson1 = gson.toJson(task1);
        String taskJson2 = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        task1.setId(1);
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        task2.setId(2);

        request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/1")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа не совпадает с ожидаемым");

        Task taskOfGet = gson.fromJson(response.body(), Task.class);

        assertEquals(taskOfGet, task1,
                "Задача прошла конвертацию через Json не верно, или была получена не та задача");
        assertNotEquals(taskOfGet, task2, "Была получена не та задача");

        request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/3")).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Код ответа не совпадает с ожидаемым");
    }

    @Test
    public void getTasksTest() throws InterruptedException, IOException {
        Task task1 = new Task("Test1", "Test1", StatusTask.NEW,
                Duration.ofMinutes(90), LocalDateTime.of(2024, 1, 1, 0, 0));
        Task task2 = new Task("Test2", "Test2", StatusTask.NEW,
                Duration.ofMinutes(90), LocalDateTime.of(2024, 1, 1, 2, 0));

        String taskJson1 = gson.toJson(task1);
        String taskJson2 = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
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
    public void addTaskTest() throws IOException, InterruptedException {
        // создаём задачу
        Task task1 = new Task("Test addNewTask", "Test addNewTask description", StatusTask.NEW,
                Duration.ofMinutes(90), LocalDateTime.of(2024, 1, 1, 0, 0));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode(), "Код ответа не совпадает с ожидаемым");

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = taskManager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test addNewTask", tasksFromManager.get(0).getName(), "Некорректное имя задачи");

        // добавляем задачу, которая пересекается по времени
        Task task2 = new Task("Test addNewTask", "Test addNewTask description", StatusTask.NEW,
                Duration.ofMinutes(90), LocalDateTime.of(2024, 1, 1, 0, 40));

        String taskJson1 = gson.toJson(task2);

        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        HttpResponse<String> response1 = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response1.statusCode(), "Код ответа не совпадает с ожидаемым");
    }

    @Test
    public void updateTaskTest() throws IOException, InterruptedException {
        Task task1 = new Task("Test1", "Test1", StatusTask.NEW,
                Duration.ofMinutes(90), LocalDateTime.of(2024, 1, 1, 0, 0));
        Task task2 = new Task("Test2", "Test2", StatusTask.NEW,
                Duration.ofMinutes(90), LocalDateTime.of(2024, 1, 1, 2, 0));
        Task task3 = new Task("Test addNewTask", "Test addNewTask description", StatusTask.NEW,
                Duration.ofMinutes(90), LocalDateTime.of(2024, 1, 1, 0, 0));

        String taskJson1 = gson.toJson(task1);
        String taskJson2 = gson.toJson(task2);
        String taskJson3 = gson.toJson(task3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        task1.setId(1);
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        task2.setId(2);
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson3)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        task3.setId(3);

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Код ответа не совпадает с ожидаемым");

        List<Task> tasksFromManager = taskManager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void deleteTaskTest() throws IOException, InterruptedException {
        Task task1 = new Task("Test1", "Test1", StatusTask.NEW,
                Duration.ofMinutes(90), LocalDateTime.of(2024, 1, 1, 0, 0));
        Task task2 = new Task("Test2", "Test2", StatusTask.NEW,
                Duration.ofMinutes(90), LocalDateTime.of(2024, 1, 1, 2, 0));

        String taskJson1 = gson.toJson(task1);
        String taskJson2 = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/1"))
                .DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не совпадает с ожидаемым");

        List<Task> tasksFromManager = taskManager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }
}