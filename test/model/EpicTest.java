package model;

import manager.Managers;
import manager.TaskManager;
import manager.TimeOverlapException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static model.StatusTask.IN_PROGRESS;
import static model.StatusTask.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;


class EpicTest {

    private TaskManager taskManager;

    private Epic epic;
    private Subtask subtask;


    @BeforeEach
    public void setUp() {
        taskManager = Managers.getDefault();
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
    public void testUpdateStatusEpic() throws TimeOverlapException {
        epic = new Epic("Починить авто", "Ремонт подвески машины", NEW);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Купить запчасти", "Выбрать и заказать запчасти", StatusTask.NEW, epic.getId(), Duration.ofHours(2), LocalDateTime.now().plusDays(4));
        taskManager.createSubtask(subtask);
        taskManager.updateStatusEpic(1);
        assertEquals(IN_PROGRESS, taskManager.getEpicById(1).getStatus());

    }
}