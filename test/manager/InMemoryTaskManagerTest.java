package manager;


import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static model.StatusTask.NEW;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        Managers managers = new Managers();
        taskManager = managers.getDefault();

    }

    @Test
    public void testNoIdConflict() {
        Task taskWithId = new Task("Задача с указанным ID", "Описание задачи с указанным ID", NEW, 10);
        Task generatedTask = new Task("Задача с сгенерированным ID", "Описание задачи с сгенерированным ID", NEW);

        taskManager.createTask(taskWithId);
        taskManager.createTask(generatedTask);

        Task retrievedTask1 = taskManager.getTaskById(10);
        Task retrievedTask2 = taskManager.getTaskById(generatedTask.getId());

        assertEquals(taskWithId, retrievedTask1);
        assertEquals(generatedTask, retrievedTask2);
    }

    @Test
    public void testImmutableTask() {
        Task originalTask = new Task("Оригинальная задача", "Описание оригинальной задачи", NEW);
        Task createdTask = taskManager.createTask(originalTask);

        // Получаем задачу по ID из менеджера
        Task retrievedTask = taskManager.getTaskById(createdTask.getId());

        // Проверяем, что задача в менеджере осталась неизменной
        assertEquals("Оригинальная задача", retrievedTask.getName());
        assertEquals("Описание оригинальной задачи", retrievedTask.getDescription());
        assertEquals(NEW, retrievedTask.getStatus());
    }

    @Test
    public void testTaskHistoryEarly() {
        // Создаем и добавляем задачу в менеджер
        Task task1 = new Task("Задача для истории1", "Описание задачи для истории", NEW);
        taskManager.createTask(task1);
        taskManager.getTaskById(1);
        Task task2 = new Task("Задача для истории1", "Описание задачи для истории", NEW);
        taskManager.createTask(task2);
        taskManager.getTaskById(2);

        // Получаем историю из менеджера
        List<Task> history = taskManager.getHistory();

        // Проверяем, что история не пустая и содержит нашу задачу
        assertNotNull(history);
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    public void testSubtaskCannotBeEpic() {
        // Создаем эпик и пытаемся добавить его в себя как подзадачу
        Epic epic = new Epic("Эпик", "Описание эпика", NEW);
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", NEW, epic.getId());

        // Пытаемся создать подзадачу, связанную с этим эпиком
        Subtask createdSubtask = taskManager.createSubtask(subtask);

        // Убеждаемся, что подзадача не была добавлена
        assertNull(createdSubtask);
    }
}
