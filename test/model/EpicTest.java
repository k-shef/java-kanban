package model;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static model.StatusTask.IN_PROGRESS;
import static model.StatusTask.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;


class EpicTest {

    private TaskManager taskManager;

    private Epic epic;
    private Subtask subtask;


    @BeforeEach
    public void setUp() {
        Managers managers = new Managers();
        taskManager = managers.getDefault();
    }

    @Test
    public void testCreateAndGetEpicAndEqualityById() {
        epic = new Epic("Починить авто", "Ремонт подвески машины", NEW);
        taskManager.createEpic(epic);
        Epic retrievedEpic = taskManager.getEpicById(1);
        assertEquals(epic, retrievedEpic);
    }

    @Test
    public void testRemoveEpic() {
        epic = new Epic("Починить авто", "Ремонт подвески машины", NEW);
        taskManager.createEpic(epic);
        taskManager.removeEpicById(1);
        Assertions.assertNull(taskManager.getEpicById(1));
    }


    @Test
    public void testUpdateStatusEpic() {
        epic = new Epic("Починить авто", "Ремонт подвески машины", NEW);
        taskManager.createEpic(epic);
        subtask = new Subtask("Купить запчасти", "Выбрать и заказать запчасти", IN_PROGRESS, epic.getId());
        taskManager.createSubtask(subtask);
        taskManager.updateStatusEpic(1);
        assertEquals(IN_PROGRESS, taskManager.getEpicById(1).getStatus());

    }
}