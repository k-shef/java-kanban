package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm");
    Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    // Методы работы с model.Task
    ArrayList<Task> getAllTasks();

    void removeAllTasks();

    Task getTaskById(int id);

    Task createTask(Task task) throws TimeOverlapException;

    void updateTask(Task updatedTask) throws TimeOverlapException;

    void removeTaskById(int id);

    // Методы работы с model.Epic
    ArrayList<Epic> getAllEpics();

    void removeAllEpics();

    Epic getEpicById(int id);

    Epic createEpic(Epic epic);

    void updateEpic(Epic updatedEpic);

    void updateStatusEpic(int epicId);


    void removeEpicById(int id);

    // Методы работы с model.Subtask
    Subtask createSubtask(Subtask subtask) throws TimeOverlapException;

    void updateSubtask(Subtask subtask) throws TimeOverlapException;

    Subtask getSubtasksById(int subtaskId);

    List<Subtask> getAllSubtasksByEpicId(int epicId);

    List<Subtask> getAllSubtasks();

    void removeAllSubtasks();

    void removeSubtaskById(int id);

    List<Task> getHistory();

    TreeSet<Task> getPrioritizedTasks();

}
