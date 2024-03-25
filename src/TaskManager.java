import java.util.*;

public class TaskManager {
    private final HashMap<Integer, Task> taskMap = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskMap = new HashMap<>();
    private final HashMap<Integer, Epic> epicMap = new HashMap<>();

    private int idCounter = 1;

    public TaskManager() {
    }

    private int generateId() {
        return idCounter++;
    }

    // Методы работы с Task
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(taskMap.values());
    }

    public void removeAllTasks() {
        taskMap.clear();
    }

    public Task getTaskById(int id) {
        return taskMap.get(id);
    }

    public Task createTask(Task task) {
        task.setId(generateId());
        taskMap.put(task.getId(), task);
        return task;
    }

    public void updateTask(Task updatedTask) {
        taskMap.put(updatedTask.getId(), updatedTask);
    }

    public void removeTaskById(int id) {
        taskMap.remove(id);
    }

    // Методы работы с Epic
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epicMap.values());
    }

    public void removeAllEpics() {
        epicMap.clear();
    }

    public Epic getEpicById(int id) {
        return epicMap.get(id);
    }

    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epicMap.put(epic.getId(), epic);
        return epic;
    }

    public void updateEpic(Epic updatedEpic) {
        epicMap.put(updatedEpic.getId(), updatedEpic);
    }

    public void removeEpicById(int id) {
        Epic removedEpic = epicMap.remove(id);
        if (removedEpic != null) {
            for (int subtaskId : removedEpic.getIdsSubtask()) {
                subtaskMap.remove(subtaskId);
            }
        }
    }

    // Методы работы с Subtask
    public Subtask createSubtask(Subtask subtask) {
        Epic epic = getEpicById(subtask.getEpicId());
        if (epic != null) {
            subtask.setId(generateId());
            subtaskMap.put(subtask.getId(), subtask);
            epic.addIdSubtasks(subtask.getId());
            return subtask;
        }
        return null;
    }

    public List<Subtask> getAllSubtasksByEpicId(int epicId) {
        Epic epic = epicMap.get(epicId);
        if (epic != null) {
            List<Subtask> subtasks = new ArrayList<>();
            for (int subtaskId : epic.getIdsSubtask()) {
                Subtask subtask = subtaskMap.get(subtaskId);
                if (subtask != null) {
                    subtasks.add(subtask);
                }
            }
            return subtasks;
        } else {
            return new ArrayList<>();
        }
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtaskMap.values());
    }

    public void removeAllSubtasks() {
        subtaskMap.clear();
    }
}