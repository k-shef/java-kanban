package manager;

import model.Epic;
import model.StatusTask;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static model.StatusTask.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected Task task, task2;
    protected Epic epic;
    protected Subtask subtask, subtask2;
    protected T taskManager;

    @BeforeEach
    public void setUpp() throws IOException {
        epic = new Epic("Починить авто", "Ремонт подвески машины", NEW);
        subtask = new Subtask("Купить запчасти", "Выбрать и заказать запчасти", NEW, 1, Duration.ofMinutes(90), LocalDateTime.of(2024, 8, 22, 21, 36));
        subtask2 = new Subtask("Отдать в сервис", "Отвезти машину в сервис", NEW, 1, Duration.ofMinutes(60), LocalDateTime.of(2023, 12, 31, 23, 20));
        task = new Task("В магазин", "Сходить в пятерочку", NEW, Duration.ofMinutes(35), LocalDateTime.now().plusDays(3));
        task2 = new Task("Task2", "Description 2", NEW, Duration.ofMinutes(55), LocalDateTime.now().plusDays(2));


    }

    @Test
    public void testCreateAndGetTaskAndEqualityById() throws TimeOverlapException, IOException {
        Task task = new Task("В магазин", "Сходить в пятерочку", NEW, Duration.ofMinutes(35), LocalDateTime.now().plusDays(3));
        taskManager.createTask(task);
        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertEquals(task, retrievedTask);
    }

    @Test
    public void testRemoveTask() throws TimeOverlapException, IOException {
        taskManager.createTask(task);
        taskManager.removeTaskById(task.getId());
        Assertions.assertNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    void addNewTask() throws TimeOverlapException, IOException {
        taskManager.createTask(task);
        taskManager.createTask(task2);
        Task t1 = taskManager.getTaskById(task.getId());
        assertNotNull(t1, "Задача не добавлена");
        assertEquals(task, t1, "Это не одна и та же задача");


        Task task3 = new Task("Test Task3", "Test", StatusTask.NEW, Duration.ofMinutes(90),
                task.getStartTime().minusMinutes(30));
        Task task4 = new Task("Test Task4", "Test", StatusTask.NEW, Duration.ofMinutes(55),
                task.getEndTime().minusMinutes(30));
        Task task5 = new Task("Test Task5", "Test", StatusTask.NEW, Duration.ofMinutes(60),
                task2.getStartTime().minusMinutes(30));
        Task task6 = new Task("Test Task6", "Test", StatusTask.NEW, Duration.ofMinutes(90),
                task2.getEndTime().minusMinutes(30));
        try {
            taskManager.createTask(task3);
            taskManager.createTask(task4);
            taskManager.createTask(task5);
            taskManager.createTask(task6);
        } catch (TimeOverlapException e) {
            //System.out.println("Ошибка: задача пересекается по времени с существующими задачами. " + e.getMessage());
        }
        int size = taskManager.getAllTasks().size();
        assertEquals(2, size, "Проверка на интервалы не срабатывает");
    }


    @Test
    public void testCreateAndGetSubtask() throws IOException, TimeOverlapException {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        Subtask retrievedSubtask = taskManager.getSubtasksById(subtask.getId());
        assertEquals(subtask, retrievedSubtask);
    }

    @Test
    public void testRemoveSubtask() throws IOException, TimeOverlapException {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask2);
        taskManager.removeSubtaskById(subtask.getId());
        List<Subtask> subtasks = taskManager.getAllSubtasksByEpicId(epic.getId());
        assertEquals(1, subtasks.size());
        Assertions.assertNull(taskManager.getSubtasksById(subtask.getId()));
    }

    @Test
    public void testUpdateSubtask() throws TimeOverlapException {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        subtask.setName("Updated Subtask");
        taskManager.updateSubtask(subtask);
        Subtask updatedSubtask = taskManager.getSubtasksById(subtask.getId());
        assertEquals("Updated Subtask", updatedSubtask.getName(), "Subtask name should be updated");
    }

    @Test
    public void testGetAllSubtasksByEpicId() throws IOException, TimeOverlapException {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask2);
        List<Subtask> subtasks = taskManager.getAllSubtasksByEpicId(epic.getId());
        assertEquals(2, subtasks.size());
    }
}
