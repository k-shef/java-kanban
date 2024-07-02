package manager;

import model.StatusTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryTaskHistoryManagerTest {

    private InMemoryTaskHistoryManager historyManager;

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryTaskHistoryManager();
    }

    @Test
    public void testAddToHistory() {
        Task task = new Task("Задача 1", "Описание Задачи 1", StatusTask.NEW);
        task.setId(1);
        historyManager.addToHistory(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    public void testRemoveFromHistory() {
        Task task1 = new Task("Задача 1", "Описание Задачи 1", StatusTask.NEW);
        Task task2 = new Task("Задача 2", "Описание Задачи 2", StatusTask.NEW);
        task1.setId(1);
        task2.setId(2);

        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
    }

    @Test
    public void testGetHistory() {
        Task task1 = new Task("Задача 1", "Описание Задачи 1", StatusTask.NEW);
        Task task2 = new Task("Задача 2", "Описание Задачи 2", StatusTask.NEW);
        task1.setId(1);
        task2.setId(2);

        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    public void testRemoveNonExistentTask() {
        Task task1 = new Task("Задача 1", "Описание Задачи 1", StatusTask.NEW);
        task1.setId(1);
        historyManager.addToHistory(task1);

        historyManager.remove(999);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    public void testAddDuplicateTask() {
        Task task1 = new Task("Задача 1", "Описание Задачи 1", StatusTask.NEW);
        task1.setId(1);
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }
}

