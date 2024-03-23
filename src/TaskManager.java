import java.util.*;

public class TaskManager {
    private final HashMap<Integer, Task> taskMap = new HashMap<>();
    private final HashMap<Integer, List<Integer>> subtaskMap = new HashMap<>();
    private final HashMap<Integer, Epic> epicMap = new HashMap<>();

    //private int taskIdCounter = 1;
    private int IdCounter = 1;

    public TaskManager() {
    }

    private int generateId() {
        return IdCounter++;
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

    public void createTask(Task task) {
        task.setId(generateId());
        taskMap.put(task.getId(), task);
    }

    public void updateTask(Task updatedTask) {
        taskMap.put(updatedTask.getId(), updatedTask);
    }

    public void removeTaskById(int id) {
        taskMap.remove(id);
    }

    // Методы работы с Epic

    public ArrayList<Epic> getAllEpic() {
        return new ArrayList<>(epicMap.values());
    }

    public void removeAllEpics() {
        epicMap.clear();
    }

    public Epic getEpicById(int id) {

        return epicMap.get(id);
    }
    public Epic createEpic(String name, String description) {
        Epic epic = new Epic(name, description, StatusTask.NEW, generateId());
        epicMap.put(epic.getId(), epic);
        return epic;
    }

    public void updateEpic(Epic updatedEpic) {
        epicMap.put(updatedEpic.getId(), updatedEpic);
    }

    public void removeEpicById(int id) {
        Epic removedEpic = epicMap.remove(id);
        if (removedEpic != null) {
            List<Integer> subtaskIds = subtaskMap.remove(id);
            if (subtaskIds != null) {
                for (int subtaskId : subtaskIds) {
                    taskMap.remove(subtaskId);
                }
            }
        }
    }

    public ArrayList<Epic> getAllSubtaskByEpic(int epicId) {
        return new ArrayList<>(epicMap.values());
    }

    public Subtask createSubtask(String name, String description, int epicId) {
        Epic epic = getEpicById(epicId);
        if (epic != null) {
            Subtask subtask = new Subtask(name, description, StatusTask.NEW, generateId(), epicId);
            List<Integer> subtaskIds = subtaskMap.getOrDefault(epicId, new ArrayList<>());
            subtaskIds.add(subtask.getId());
            subtaskMap.put(epicId, subtaskIds);
            return subtask;
        }
        return null;
    }

    public List<Subtask> getAllSubtasksByEpicId(int epicId) {
        List<Integer> subtaskIds = subtaskMap.getOrDefault(epicId, Collections.emptyList());
        List<Subtask> subtasks = new ArrayList<>();
        for (int subtaskId : subtaskIds) {
            Task task = taskMap.get(subtaskId);
            if (task instanceof Subtask) {
                subtasks.add((Subtask) task);
            }
        }
        return subtasks;
    }

    public List<Integer> getSubtaskMap(int epicId) {

        return subtaskMap.getOrDefault(epicId, Collections.emptyList());
    }
}