

package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.StatusTask;
import model.TypeTask;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(TaskHistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public void save() {
        try {
            List<String> lines = new ArrayList<>();
            lines.add("id,type,name,status,description,epic,idsSubtask");

            for (Task task : getAllTasks()) {
                lines.add(taskToCsv(task));
            }

            for (Epic epic : getAllEpics()) {
                lines.add(epicToCsv(epic));
            }

            for (Subtask subtask : getAllSubtasks()) {
                lines.add(subtaskToCsv(subtask));
            }

            Files.write(Paths.get(file.toURI()), lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при сохранении данных.", e);
        }
    }

    private String taskToCsv(Task task) {
        return String.format("%d,%s,%s,%s,%s,", task.getId(), TypeTask.TASK, task.getName(), task.getStatus(), task.getDescription());
    }

    private String epicToCsv(Epic epic) {
        StringBuilder idsSubtask = new StringBuilder();
        for (Integer id : epic.getIdsSubtask()) {
            idsSubtask.append(id).append(",");
        }
        if (idsSubtask.length() > 0) {
            idsSubtask.setLength(idsSubtask.length() - 1);
        }
        return String.format("%d,%s,%s,%s,%s,%s", epic.getId(), TypeTask.EPIC, epic.getName(), epic.getStatus(), epic.getDescription(), idsSubtask);
    }

    private String subtaskToCsv(Subtask subtask) {
        return String.format("%d,%s,%s,%s,%s,%d", subtask.getId(), TypeTask.SUBTASK, subtask.getName(), subtask.getStatus(), subtask.getDescription(), subtask.getEpicId());
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        TaskHistoryManager historyManager = new InMemoryTaskHistoryManager();
        FileBackedTaskManager taskManager = new FileBackedTaskManager(historyManager, file);

        try {
            List<String> lines = Files.readAllLines(Paths.get(file.toURI()), StandardCharsets.UTF_8);
            boolean isHistory = false;

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isEmpty()) {
                    continue;
                }

                if (line.equals("History")) {
                    isHistory = true;
                    continue;
                }

                if (isHistory) {
                    int id = Integer.parseInt(line.trim());
                    Task task = taskManager.getTaskById(id);
                    if (task == null) task = taskManager.getEpicById(id);
                    if (task == null) task = taskManager.getSubtasksById(id);
                    if (task != null) {
                        taskManager.historyManager.addToHistory(task);
                    }
                } else {
                    String[] fields = line.split(",");
                    int id = Integer.parseInt(fields[0]);
                    TypeTask type = TypeTask.valueOf(fields[1]);
                    String name = fields[2];
                    StatusTask status = StatusTask.valueOf(fields[3]);
                    String description = fields[4];

                    switch (type) {
                        case TASK:
                            Task task = new Task(name, description, status, id);
                            taskManager.taskMap.put(id, task);
                            break;
                        case EPIC:
                            Epic epic = new Epic(name, description, status);
                            epic.setId(id);
                            taskManager.epicMap.put(id, epic);
                            break;
                        case SUBTASK:
                            int epicId = Integer.parseInt(fields[5]);
                            Subtask subtask = new Subtask(name, description, status, epicId);
                            subtask.setId(id);
                            taskManager.subtaskMap.put(id, subtask);
                            Epic parentEpic = taskManager.epicMap.get(epicId);
                            if (parentEpic != null) {
                                parentEpic.addIdSubtasks(id);
                            }
                            break;
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при загрузке данных.", e);
        }

        return taskManager;
    }


    @Override
    public Task createTask(Task task) {
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        super.updateEpic(updatedEpic);
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        Epic parentEpic = getEpicById(subtask.getEpicId());
        if (parentEpic != null) {
            List<Integer> idsSubtask = parentEpic.getIdsSubtask();
            if (!idsSubtask.contains(subtask.getId())) {
                idsSubtask.add(subtask.getId());
            }
        }
        save();
        return subtask;
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }
}
