package manager;


import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static model.StatusTask.NEW;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {


    @BeforeEach
    public void setUp() {
        taskManager = (InMemoryTaskManager) Managers.getDefault();
    }

    @Test
    public void testNoIdConflict() throws TimeOverlapException {
        Task task1 = new Task("Задача # 1", "Описание задачи # 1", NEW, Duration.ofMinutes(35), LocalDateTime.now().plusDays(3));
        Task task2 = new Task("Задача # 2", "Описание задачи # 2", NEW, Duration.ofMinutes(24), LocalDateTime.now().plusDays(1));

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Task retrievedTask1 = taskManager.getTaskById(task1.getId());
        Task retrievedTask2 = taskManager.getTaskById(task2.getId());

        assertEquals(task1, retrievedTask1);
        assertEquals(task2, retrievedTask2);
    }

    @Test
    public void testImmutableTask() throws TimeOverlapException {
        Task originalTask = new Task("Оригинальная задача", "Описание оригинальной задачи", NEW, Duration.ofMinutes(35), LocalDateTime.now().plusDays(3));
        Task createdTask = taskManager.createTask(originalTask);
        Task retrievedTask = taskManager.getTaskById(createdTask.getId());
        assertEquals("Оригинальная задача", retrievedTask.getName());
        assertEquals("Описание оригинальной задачи", retrievedTask.getDescription());
        assertEquals(NEW, retrievedTask.getStatus());
    }

    @Test
    public void testTaskHistoryEarly() throws TimeOverlapException {
        Task task1 = new Task("Задача для истории1", "Описание задачи для истории", NEW, Duration.ofMinutes(35), LocalDateTime.now().plusDays(3));
        taskManager.createTask(task1);
        taskManager.getTaskById(1);
        Task task2 = new Task("Задача для истории1", "Описание задачи для истории", NEW, Duration.ofMinutes(15), LocalDateTime.now().plusDays(8));
        taskManager.createTask(task2);
        taskManager.getTaskById(2);
        List<Task> history = taskManager.getHistory();
        assertNotNull(history);
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    public void testSubtaskCannotBeEpic() throws TimeOverlapException {
        Epic epic = new Epic("Эпик", "Описание эпика", NEW);
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", NEW, epic.getId(), Duration.ofMinutes(17), LocalDateTime.now().plusDays(7));
        Subtask createdSubtask = taskManager.createSubtask(subtask);
        assertNull(createdSubtask);
    }
}
