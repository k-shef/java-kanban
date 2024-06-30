package manager;

import model.Epic;
import model.StatusTask;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> taskMap = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtaskMap = new HashMap<>();
    protected final HashMap<Integer, Epic> epicMap = new HashMap<>();

    protected final TaskHistoryManager historyManager;

    public InMemoryTaskManager(TaskHistoryManager historyManager) {
        this.historyManager = historyManager;
    }


    protected int generateId = 1;

    protected int getNextUniqueId() {
        int id = generateId++;
        while (taskMap.containsKey(id) || subtaskMap.containsKey(id) || epicMap.containsKey(id)) {
            id = generateId++;
        }
        return id;
    }


    // Методы работы с model.Task
    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public void removeAllTasks() {
        for (Task task : taskMap.values()) {
            historyManager.remove(task.getId());
        }
        taskMap.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = taskMap.get(id);
        if (task != null) {
            historyManager.addToHistory(task);
        }
        return task;
    }

    @Override
    public Task createTask(Task task) {
        int id;
        if (task.getId() > 0) {
            id = task.getId();
        } else {
            id = getNextUniqueId();
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
        historyManager.remove(id);
    }

    // Методы работы с model.Epic
    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epicMap.values());
    }

    @Override
    public void removeAllEpics() {
        for (Epic epic : epicMap.values()) {
            for (int subtaskId : epic.getIdsSubtask()) {
                subtaskMap.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            historyManager.remove(epic.getId());
        }
        epicMap.clear();
        subtaskMap.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epicMap.get(id);
        if (epic != null) {
            historyManager.addToHistory(epic);
        }
        return epic;
    }

    @Override
    public Epic createEpic(Epic epic) {
        int id = getNextUniqueId();
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
                historyManager.remove(subtaskId);
            }
            historyManager.remove(id);
        }
    }

    // Методы работы с model.Subtask
    @Override
    public Subtask createSubtask(Subtask subtask) {
        Epic epic = epicMap.get(subtask.getEpicId());
        if (epic != null) {
            int id = getNextUniqueId();
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
        if (subtask != null) {
            historyManager.addToHistory(subtask);
        }
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
        for (Subtask subtask : subtaskMap.values()) {
            Epic epic = epicMap.get(subtask.getEpicId());
            if (epic != null) {
                epic.getIdsSubtask().remove(Integer.valueOf(subtask.getId()));
                updateStatusEpic(epic.getId());
            }
            historyManager.remove(subtask.getId());
        }
        subtaskMap.clear();
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

