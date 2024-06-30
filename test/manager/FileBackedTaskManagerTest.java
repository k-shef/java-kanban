package manager;

import model.Epic;
import model.StatusTask;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private File tempFile;

    {
        try {
            tempFile = File.createTempFile("test_tasks", ".csv");
            tempFile = File.createTempFile("test_tasks", ".csv", new File("C:/newFolder"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private FileBackedTaskManager taskManager;

    @BeforeEach
    void setUp() throws IOException {

        if (!tempFile.exists()) {
            try {
                tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        taskManager = new FileBackedTaskManager(new InMemoryTaskHistoryManager(), tempFile);
    }

    @AfterEach
    void tearDown() {
        tempFile.delete();
    }

    @Test
    void testSaveAndLoadTasks() {
        Task task1 = new Task("Task 1", "Description 1", StatusTask.NEW);
        Task task2 = new Task("Task 2", "Description 2", StatusTask.IN_PROGRESS);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> tasks = loadedManager.getAllTasks();
        assertEquals(2, tasks.size());
        assertEquals("Task 1", tasks.get(0).getName());
        assertEquals("Task 2", tasks.get(1).getName());
    }

    @Test
    void testSaveAndLoadEpicsAndSubtasks() {
        Epic epic1 = new Epic("Epic1", "Epic Description", StatusTask.NEW);
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask1", "Subtask1 Description", StatusTask.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Subtask2", "Subtask2 Description", StatusTask.NEW, epic1.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager.save();

        TaskManager loadedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedTaskManager.getAllEpics().size(), "Количество эпиков не совпадает");
        Epic loadedEpic1 = loadedTaskManager.getEpicById(epic1.getId());
        assertEquals(epic1, loadedEpic1, "Эпики не совпадают");

        assertEquals(2, loadedTaskManager.getAllSubtasksByEpicId(epic1.getId()).size(), "Количество подзадач не совпадает");
        Subtask loadedSubtask1 = loadedTaskManager.getSubtasksById(subtask1.getId());
        Subtask loadedSubtask2 = loadedTaskManager.getSubtasksById(subtask2.getId());
        assertEquals(subtask1, loadedSubtask1, "Первая подзадача не совпадает");
        assertEquals(subtask2, loadedSubtask2, "Вторая подзадача не совпадает");

        List<Integer> loadedSubtaskIds = loadedEpic1.getIdsSubtask();
        assertEquals(Arrays.asList(subtask1.getId(), subtask2.getId()), loadedSubtaskIds, "Порядок подзадач не совпадает");
    }

    @Test
    void testRemoveTask() {
        Task task1 = new Task("Task 1", "Description 1", StatusTask.NEW);
        Task task2 = new Task("Task 2", "Description 2", StatusTask.IN_PROGRESS);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.removeTaskById(task1.getId());

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> tasks = loadedManager.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals("Task 2", tasks.get(0).getName());
    }

    @Test
    void shouldSetNextIdCorrectlyAfterLoadingFromFile() throws IOException {
        // Создаем несколько задач, эпиков и подзадач
        Task task1 = new Task("Task 1", "Description 1", StatusTask.NEW);
        Task task2 = new Task("Task 2", "Description 2", StatusTask.IN_PROGRESS);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        Epic epic1 = new Epic("Epic 1", "Epic Description 1", StatusTask.NEW);
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask 1", "Subtask Description 1", StatusTask.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask Description 2", StatusTask.DONE, epic1.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        // Сохраняем менеджер в файл
        taskManager.save();

        // Загружаем менеджер из файла
        FileBackedTaskManager loadedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверяем, что задачи были успешно добавлены в исходный менеджер
        assertEquals(2, loadedTaskManager.getAllTasks().size(), "Количество задач в загруженном менеджере неверно");
        assertEquals(1, loadedTaskManager.getAllEpics().size(), "Количество эпиков в загруженном менеджере неверно");
        assertEquals(2, loadedTaskManager.getAllSubtasks().size(), "Количество подзадач в загруженном менеджере неверно");

        // Проверяем, что значения идентификаторов были восстановлены корректно
        // После загрузки из файла значение generateId должно быть равно максимальному идентификатору
        assertEquals(5, loadedTaskManager.generateId, "Значение идентификатора после загрузки неверно");
    }

    @Test
    void testRemoveEpicAndSubtasks() {
        Epic epic = new Epic("Epic 1", "Description Epic 1", StatusTask.NEW);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description Subtask 1", StatusTask.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description Subtask 2", StatusTask.DONE, epic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager.removeEpicById(epic.getId());

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        List<Epic> epics = loadedManager.getAllEpics();
        assertEquals(0, epics.size());

        List<Subtask> subtasks = loadedManager.getAllSubtasks();
        assertEquals(0, subtasks.size());
    }
}