package model;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static model.StatusTask.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskTest {
    private TaskManager taskManager;
    private Epic epic;
    private Subtask subtask;
    private Subtask subtask2;

    @BeforeEach
    public void setUp() {
        Managers managers = new Managers();
        taskManager = managers.getDefault();
        epic = new Epic("Починить авто", "Ремонт подвески машины", NEW);
        taskManager.createEpic(epic);
        subtask = new Subtask("Купить запчасти", "Выбрать и заказать запчасти", NEW,  epic.getId());
        taskManager.createSubtask(subtask);
        subtask2 = new Subtask("Отдать в сервис", "Отвезти машину в сервис", NEW,  epic.getId());
        taskManager.createSubtask(subtask2);
    }


    @Test
    public void testCreateAndGetSubtask() {
        taskManager.getAllSubtasks();
        Subtask retrievedSubtask = taskManager.getSubtasksById(2);
        assertEquals(subtask, retrievedSubtask);
    }

    @Test
    public void testRemoveSubtask() {
        taskManager.removeSubtaskById(subtask.getId());
        List<Subtask> subtasks = taskManager.getAllSubtasksByEpicId(epic.getId());
        assertEquals(1, subtasks.size());
        Assertions.assertNull(taskManager.getSubtasksById(2));
    }

    @Test
    public void testUpdateSubtask() {
        subtask.setName("Новая подзадача");
        taskManager.updateSubtask(subtask);
        Subtask updatedSubtask = taskManager.getSubtasksById(2);
        assertEquals("Новая подзадача", updatedSubtask.getName());
    }

    @Test
    public void testGetAllSubtasksByEpicId() {
        List<Subtask> subtasks = taskManager.getAllSubtasksByEpicId(1);
        assertEquals(2, subtasks.size());
    }
}
