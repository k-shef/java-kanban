package manager;

import model.Epic;
import model.StatusTask;
import model.Subtask;
import model.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> taskMap = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtaskMap = new HashMap<>();
    protected final HashMap<Integer, Epic> epicMap = new HashMap<>();

    protected final TaskHistoryManager historyManager = Managers.getDefaultHistory();

    private TreeSet<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(LocalDateTime::compareTo)));
    }

    protected int generateId = 1;

    protected int getNextUniqueId() {
        int id = generateId++;
        while (taskMap.containsKey(id) || subtaskMap.containsKey(id) || epicMap.containsKey(id)) {
            id = generateId++;
        }
        return id;
    }


    public TreeSet<Task> getPrioritizedTasks() {
        return new TreeSet<>(prioritizedTasks); // Возвращаем копию для защиты от изменений
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
        prioritizedTasks.clear();
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
    public Task createTask(Task task) throws TimeOverlapException {
        if (task.getStartTime() == null) {
            taskMap.put(task.getId(), task);
            return task;
        }
        for (Task existingTask : prioritizedTasks) {
            if (isOverlapping(task, existingTask)) {
                throw new TimeOverlapException("Задача пересекается по времени с существующей задачей.");
            }
        }
        int id;
        if (task.getId() > 0) {
            id = task.getId();
        } else {
            id = getNextUniqueId();
        }
        task.setId(id);
        taskMap.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
        return task;
    }

    @Override
    public void updateTask(Task updatedTask) throws TimeOverlapException {
        if (updatedTask.getStartTime() != null) {
            for (Task existingTask : prioritizedTasks) {
                if (!existingTask.equals(updatedTask) && isOverlapping(updatedTask, existingTask)) {
                    throw new TimeOverlapException("Задача пересекается по времени с существующей задачей.");
                }
            }
        }
        if (taskMap.containsKey(updatedTask.getId())) {
            Task existingTask = taskMap.get(updatedTask.getId());
            prioritizedTasks.remove(existingTask);
            taskMap.put(updatedTask.getId(), updatedTask);
            if (updatedTask.getStartTime() != null) {
                prioritizedTasks.add(updatedTask);
            }
        }
    }

    @Override
    public void removeTaskById(int id) {
        Task task = taskMap.remove(id);
        if (task != null) {
            prioritizedTasks.remove(task);
            historyManager.remove(id);
        }
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

    private boolean isOverlapping(Task task1, Task task2) {
        // Проверка на null значения
        if (task1.getStartTime() == null || task2.getStartTime() == null ||
                task1.getEndTime() == null || task2.getEndTime() == null) {
            return false;
        }

        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        // Проверка на пересечение времени
        boolean startOverlap = (start1.isBefore(start2) || start1.equals(start2)) ||
                (start1.isAfter(start2) && start1.isBefore(end2));

        boolean endOverlap = (end1.equals(end2) || end1.isAfter(end2)) ||
                (end1.isBefore(end2) && end1.isAfter(start2));

        return startOverlap && endOverlap;
    }

    // Методы работы с model.Subtask
    @Override
    public Subtask createSubtask(Subtask subtask) throws TimeOverlapException {
        if (subtask.getStartTime() == null) {
            taskMap.put(subtask.getId(), subtask);
            return subtask;
        }
        for (Task existingSubtask : prioritizedTasks) {
            if (isOverlapping(subtask, existingSubtask)) {
                throw new TimeOverlapException("Подзадача пересекается по времени с существующей подзадачей.");
            }
        }
        Epic epic = epicMap.get(subtask.getEpicId());
        if (epic != null) {
            int id = getNextUniqueId();
            subtask.setId(id);
            subtaskMap.put(subtask.getId(), subtask);
            epic.addIdSubtasks(id);
            updateEpicDurationAndTime(epic);
            if (subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
            }
            if (epic.getStartTime() != null) {
                prioritizedTasks.add(epic);
            }
            updateStatusEpic(epic.getId());

            updateEpic(epic);
            return subtask;
        }
        return null;
    }

    @Override
    public void updateSubtask(Subtask subtask) throws TimeOverlapException {
        if (subtask.getStartTime() != null) {
            for (Task existingTask : prioritizedTasks) {
                if (!existingTask.equals(subtask) && isOverlapping(subtask, existingTask)) {
                    throw new TimeOverlapException("Задача пересекается по времени с существующей задачей.");
                }
            }
        }
        if (subtaskMap.containsKey(subtask.getId())) {
            Subtask existingSubtask = subtaskMap.get(subtask.getId());
            prioritizedTasks.remove(existingSubtask);
            subtaskMap.put(subtask.getId(), subtask);
            if (subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
            }
            updateEpicDurationAndTime(epicMap.get(subtask.getEpicId()));
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
            prioritizedTasks.remove(removedSubtask);
            Epic epic = epicMap.get(removedSubtask.getEpicId());
            if (epic != null) {
                epic.getIdsSubtask().remove(Integer.valueOf(id));
                updateEpicDurationAndTime(epic);
                updateStatusEpic(epic.getId());
            }
            historyManager.remove(id);
        }
    }

    private void updateEpicDurationAndTime(Epic epic) {
        List<Subtask> subtasks = getAllSubtasksByEpicId(epic.getId());
        epic.updateDurationAndTime(subtasks);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}

