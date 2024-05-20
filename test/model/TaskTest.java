package model;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static model.StatusTask.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class TaskTest {
    private TaskManager taskManager;
    private Task task;


    @BeforeEach
    public void setUp() {
        Managers managers = new Managers();
        taskManager = managers.getDefault();
        task = new Task("В магазин", "Сходить в пятерочку", NEW);
    }


    @Test
    public void testCreateAndGetTaskAndEqualityById() {
        taskManager.createTask(task);
        Task retrievedTask = taskManager.getTaskById(1);
        assertEquals(task, retrievedTask);
    }

    @Test
    public void testRemoveTask() {
        taskManager.createTask(task);
        taskManager.removeTaskById(1);
        Assertions.assertNull(taskManager.getTaskById(1));
    }

    @Test
    void addNewTask() {
        final int taskId = taskManager.createTask(task).getId();
        final Task savedTask = taskManager.getTaskById(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        final List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }
}
