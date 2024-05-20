package manager;

import model.Epic;
import model.StatusTask;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> taskMap = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskMap = new HashMap<>();
    private final HashMap<Integer, Epic> epicMap = new HashMap<>();

    private final TaskHistoryManager historyManager;

    public InMemoryTaskManager(TaskHistoryManager historyManager) {
        this.historyManager = historyManager;
    }


    private int generateId = 1;

    // Методы работы с model.Task
    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public void removeAllTasks() {
        taskMap.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = taskMap.get(id);
        historyManager.addToHistory(task);
        return task;
    }

    @Override
    public Task createTask(Task task) {
        int id;
        if (task.getId() > 0) {
            id = task.getId();
        } else {
            id = generateId++;
        }
        task.setId(id);
        taskMap.put(task.getId(), task);
        return task;
    }

    @Override
    public void updateTask(Task updatedTask) {
        if (taskMap.containsKey(updatedTask.getId())) {
            taskMap.put(updatedTask.getId(), updatedTask);
        }
    }

    @Override
    public void removeTaskById(int id) {
        taskMap.remove(id);
    }

    // Методы работы с model.Epic
    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epicMap.values());
    }

    @Override
    public void removeAllEpics() {
        epicMap.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epicTask = epicMap.get(id);
        historyManager.addToHistory(epicMap.get(id));
        return epicTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        int id = generateId++;
        epic.setId(id);
        epicMap.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        Epic trueEpic = epicMap.get(updatedEpic.getId());
        if (trueEpic != null) {
            trueEpic.setName(updatedEpic.getName());
            trueEpic.setDescription(updatedEpic.getDescription());
            epicMap.put(updatedEpic.getId(), trueEpic);
        }
    }

    public void updateStatusEpic(int epicId) {
        Epic epic = epicMap.get(epicId);
        if (epic != null) {
            boolean allSubtasksDone = true;
            for (int subtaskId : epic.getIdsSubtask()) {
                Subtask subtaskInEpic = subtaskMap.get(subtaskId);
                if (subtaskInEpic.getStatus() != StatusTask.DONE) {
                    allSubtasksDone = false;
                    break;
                }
            }
            if (allSubtasksDone) {
                epic.setStatus(StatusTask.DONE);
            } else {
                epic.setStatus(StatusTask.IN_PROGRESS);
            }
            updateEpic(epic);
        }
    }

    @Override
    public void removeEpicById(int id) {
        Epic removedEpic = epicMap.remove(id);
        if (removedEpic != null) {
            for (int subtaskId : removedEpic.getIdsSubtask()) {
                subtaskMap.remove(subtaskId);
            }
        }
    }

    // Методы работы с model.Subtask
    @Override
    public Subtask createSubtask(Subtask subtask) {
        Epic epic = epicMap.get(subtask.getEpicId());
        if (epic != null) {
            int id = generateId++;
            subtask.setId(id);
            subtaskMap.put(subtask.getId(), subtask);
            epic.addIdSubtasks(id);
            if (epic.getStatus() == StatusTask.DONE) {
                epic.setStatus(StatusTask.IN_PROGRESS);
                updateEpic(epic);
            }
            return subtask;
        }
        return null;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtaskMap.containsKey(subtask.getId())) {
            subtaskMap.put(subtask.getId(), subtask);
            updateStatusEpic(subtask.getEpicId());
        }
    }

    @Override
    public Subtask getSubtasksById(int subtaskId) {
        Subtask subtask = subtaskMap.get(subtaskId);
        historyManager.addToHistory(subtask);
        return subtask;
    }

    @Override
    public List<Subtask> getAllSubtasksByEpicId(int epicId) {
        Epic epic = epicMap.get(epicId);
        if (epic != null) {
            List<Subtask> subtasks = new ArrayList<>();
            for (int subtaskId : epic.getIdsSubtask()) {
                Subtask subtask = getSubtasksById(subtaskId);
                if (subtask != null) {
                    subtasks.add(subtask);
                }
            }
            return subtasks;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtaskMap.values());
    }

    @Override
    public void removeAllSubtasks() {
        subtaskMap.clear();
        for (Epic epic : epicMap.values()) {
            updateStatusEpic(epic.getId());
        }
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask removedSubtask = subtaskMap.remove(id);
        if (removedSubtask != null) {
            Epic epic = getEpicById(removedSubtask.getEpicId());
            if (epic != null) {
                epic.getIdsSubtask().remove(Integer.valueOf(id));
                updateStatusEpic(epic.getId());
            }
        }
    }


    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}

