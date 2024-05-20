package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    // Методы работы с model.Task
    ArrayList<Task> getAllTasks();

    void removeAllTasks();

    Task getTaskById(int id);

    Task createTask(Task task);

    void updateTask(Task updatedTask);

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
    Subtask createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    Subtask getSubtasksById(int subtaskId);

    List<Subtask> getAllSubtasksByEpicId(int epicId);

    List<Subtask> getAllSubtasks();

    void removeAllSubtasks();

    void removeSubtaskById(int id);

    List<Task> getHistory();

}
