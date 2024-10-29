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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File tempFile;

    @BeforeEach
    public void setUp() throws IOException {

        tempFile = File.createTempFile("test_tasks", ".csv");
        taskManager = new FileBackedTaskManager(tempFile);


    }

    @AfterEach
    void tearDown() {
        tempFile.delete();
    }

    @Test
    public void readTasksInEmptyFileTest() {
        FileBackedTaskManager f = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> list = f.getAllTasks();
        List<Epic> list1 = f.getAllEpics();
        List<Subtask> list2 = f.getAllSubtasks();

        assertEquals(0, list.size(),
                "Из пустого файла зкземпляр класса FileBackedTaskManager создается не пустым.");
        assertEquals(0, list1.size(),
                "Из пустого файла зкземпляр класса FileBackedTaskManager создается не пустым");
        assertEquals(0, list2.size(),
                "Из пустого файла зкземпляр класса FileBackedTaskManager создается не пустым");
    }

    @Test
    public void writeTasksInFileTest() throws IOException, TimeOverlapException {
        Task task1 = new Task("Test addNewTask", "Test addNewTask description", StatusTask.NEW,
                Duration.ofMinutes(90), LocalDateTime.of(2024, 1, 1, 0, 0));
        taskManager.createTask(task1); //id = 1
        Task task2 = new Task("Уборка", "Загрузить посудомойку и запустить пылесос", StatusTask.NEW,
                Duration.ofMinutes(45), LocalDateTime.of(2024, 1, 2, 0, 0));
        taskManager.createTask(task2);
        Epic epic1 = new Epic("Test addNewEpic", "Test addNewEpic description", StatusTask.NEW);
        taskManager.createEpic(epic1); //id = 2
        Subtask subtask1 = new Subtask("Test addNewSubTask", "Test addNewSubTask description", StatusTask.NEW,
                epic1.getId(), Duration.ofMinutes(90), LocalDateTime.of(2024, 1, 1, 2, 0)); //id = 3
        taskManager.createSubtask(subtask1);
        String fileContent = Files.readString(tempFile.toPath());
        String[] lines = fileContent.split(System.lineSeparator());
        assertEquals(5, lines.length, "Количество строк не совпадает с ожидаемым");
        String expectedHeader = "id,type,name,status,description,duration,startTime,endTime,idsSubtask,epic;";
        assertEquals(expectedHeader, lines[0], "Базовая строка не добавлена");
        String expectedTaskLine = "1,TASK,Test addNewTask,NEW,Test addNewTask description,90,2024-01-01T00:00,2024-01-01T01:30;";
        assertEquals(expectedTaskLine, lines[1], "Задачи добавляются неверно");
    }


    @Test
    void testSaveAndLoadTasks() throws TimeOverlapException {
        Task task1 = new Task("В магазин", "Сходить в пятерочку", StatusTask.NEW, Duration.parse("PT30M"), LocalDateTime.of(2024, 7, 12, 9, 0));
        taskManager.createTask(task1);
        Task task2 = new Task("Уборка", "Загрузить посудомойку и запустить пылесос", StatusTask.NEW, Duration.ofMinutes(45), LocalDateTime.of(2024, 7, 10, 5, 0));
        taskManager.createTask(task2);

        TaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> tasks = loadedManager.getAllTasks();
        assertEquals(2, tasks.size());
        assertEquals("В магазин", tasks.get(0).getName());
        assertEquals("Уборка", tasks.get(1).getName());
    }

    @Test
    void testSaveAndLoadEpicsAndSubtasks() throws TimeOverlapException {
        Epic epic1 = new Epic("Epic1", "Epic Description", StatusTask.NEW);
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask1", "Subtask1 Description", StatusTask.NEW, epic1.getId(), Duration.ofHours(2), LocalDateTime.of(2024, 7, 14, 5, 0));
        Subtask subtask2 = new Subtask("Subtask2", "Subtask2 Description", StatusTask.NEW, epic1.getId(), Duration.ofHours(1), LocalDateTime.of(2024, 7, 15, 7, 0));
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
    public void testRemoveTask() throws TimeOverlapException {
        Task task1 = new Task("Task 1", "Description 1", StatusTask.NEW, Duration.ofMinutes(35), LocalDateTime.now().plusDays(3));
        Task task2 = new Task("Task 2", "Description 2", StatusTask.IN_PROGRESS, Duration.ofMinutes(48), LocalDateTime.now().plusDays(4));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.removeTaskById(task1.getId());

        TaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> tasks = loadedManager.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals("Task 2", tasks.get(0).getName());
    }

    @Test
    void shouldSetNextIdCorrectlyAfterLoadingFromFile() throws TimeOverlapException {
        Task task1 = new Task("Task 1", "Description 1", StatusTask.NEW, Duration.ofMinutes(35), LocalDateTime.now().plusDays(3));
        Task task2 = new Task("Task 2", "Description 2", StatusTask.IN_PROGRESS, Duration.ofMinutes(48), LocalDateTime.now().plusDays(4));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        Epic epic1 = new Epic("Epic 1", "Epic Description 1", StatusTask.NEW);
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask1", "Subtask1 Description", StatusTask.NEW, epic1.getId(), Duration.ofHours(1), LocalDateTime.now().plusDays(5));
        Subtask subtask2 = new Subtask("Subtask2", "Subtask2 Description", StatusTask.DONE, epic1.getId(), Duration.ofHours(3), LocalDateTime.now().plusDays(12));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.save();
        FileBackedTaskManager loadedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(2, loadedTaskManager.getAllTasks().size(), "Количество задач в загруженном менеджере неверно");
        assertEquals(1, loadedTaskManager.getAllEpics().size(), "Количество эпиков в загруженном менеджере неверно");
        assertEquals(2, loadedTaskManager.getAllSubtasks().size(), "Количество подзадач в загруженном менеджере неверно");
        assertEquals(5 + 1, loadedTaskManager.generateId, "Значение идентификатора после загрузки неверно");
    }

    @Test
    void testRemoveEpicAndSubtasks() throws TimeOverlapException {
        Epic epic = new Epic("Epic 1", "Description Epic 1", StatusTask.NEW);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask1", "Subtask1 Description", StatusTask.NEW, epic.getId(), Duration.ofHours(2), LocalDateTime.now().plusDays(4));
        Subtask subtask2 = new Subtask("Subtask2", "Subtask2 Description", StatusTask.DONE, epic.getId(), Duration.ofHours(1), LocalDateTime.now().plusDays(5));
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