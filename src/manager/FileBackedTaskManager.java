

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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try {
            List<String> lines = new ArrayList<>();
            lines.add("id,type,name,status,description,duration,startTime,endTime,idsSubtask,epic;");

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
        String startTimeStr = task.getStartTime() != null ? task.getStartTime().toString() : "";
        String endTimeStr = task.getEndTime() != null ? task.getEndTime().toString() : "";
        long durationMinutes = task.getDuration() != null ? task.getDuration().toMinutes() : 0;
        String result = String.format("%d,%s,%s,%s,%s,%d,%s,%s;", task.getId(), TypeTask.TASK, task.getName(), task.getStatus(), task.getDescription(), durationMinutes, startTimeStr, endTimeStr);
        return result;
    }

    private String epicToCsv(Epic epic) {
        StringBuilder idsSubtask = new StringBuilder();
        for (Integer id : epic.getIdsSubtask()) {
            idsSubtask.append(id).append(",");
        }
        if (idsSubtask.length() > 0) {
            idsSubtask.setLength(idsSubtask.length() - 1); // Удаляем последнюю запятую
        }

        String idsSubtaskStr = idsSubtask.toString();
        String startTimeStr = epic.getStartTime() != null ? epic.getStartTime().toString() : "";
        String endTimeStr = epic.getEndTime() != null ? epic.getEndTime().toString() : "";
        long durationMinutes = epic.getDuration() != null ? epic.getDuration().toMinutes() : 0;

        // Формирование строки CSV
        String result = String.format("%d,%s,%s,%s,%s,%d,%s,%s,%s,%s;",
                epic.getId(), TypeTask.EPIC, epic.getName(), epic.getStatus(),
                epic.getDescription(), durationMinutes, startTimeStr, endTimeStr, "", idsSubtaskStr);

        return result;
    }

    private String subtaskToCsv(Subtask subtask) {
        String startTimeStr = subtask.getStartTime() != null ? subtask.getStartTime().toString() : "";
        String endTimeStr = subtask.getEndTime() != null ? subtask.getEndTime().toString() : "";
        long durationMinutes = subtask.getDuration() != null ? subtask.getDuration().toMinutes() : 0;

        // Формирование строки CSV
        String result = String.format("%d,%s,%s,%s,%s,%d,%s,%s,%d;",
                subtask.getId(), TypeTask.SUBTASK, subtask.getName(), subtask.getStatus(),
                subtask.getDescription(), durationMinutes, startTimeStr, endTimeStr, subtask.getEpicId());

        return result;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        int maxId = 0;
        try {
            List<String> lines = Files.readAllLines(Paths.get(file.toURI()), StandardCharsets.UTF_8);
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isEmpty()) {
                    continue;
                }
                String cleanedLine = line.substring(0, line.length() - 1);
                String[] fields = cleanedLine.split(",");
                int id = Integer.parseInt(fields[0]);
                TypeTask type = TypeTask.valueOf(fields[1]);
                String name = fields[2];
                StatusTask status = StatusTask.valueOf(fields[3]);
                String description = fields[4];
                Duration duration = !fields[5].isEmpty() ? Duration.ofMinutes(Long.parseLong(fields[5])) : null;
                LocalDateTime startTime = !fields[6].isEmpty() ? LocalDateTime.parse(fields[6]) : null;
                LocalDateTime endTime = !fields[7].isEmpty() ? LocalDateTime.parse(fields[7]) : null;

                switch (type) {
                    case TASK:
                        Task task = new Task(name, description, status, duration, startTime);
                        task.setId(id);
                        taskManager.taskMap.put(id, task);
                        break;
                    case EPIC:
                        Epic epic = new Epic(name, description, status);
                        epic.setId(id);
                        epic.setDuration(duration);
                        epic.setStartTime(startTime);
                        epic.setEndTime(endTime);
                        if (fields.length > 8) {
                            String idsSubtaskStr = fields[8];
                            if (!idsSubtaskStr.isEmpty()) {
                                String[] idsSubtaskArray = idsSubtaskStr.split(";");
                                for (String subtaskId : idsSubtaskArray) {
                                    epic.addIdSubtasks(Integer.parseInt(subtaskId));
                                }
                            }
                        }
                        taskManager.epicMap.put(id, epic);
                        break;
                    case SUBTASK:
                        int epicId = Integer.parseInt(fields[fields.length - 1]);
                        Subtask subtask = new Subtask(name, description, status, epicId, duration, startTime);
                        subtask.setId(id);
                        taskManager.subtaskMap.put(id, subtask);
                        Epic parentEpic = taskManager.epicMap.get(epicId);
                        if (parentEpic != null) {
                            parentEpic.addIdSubtasks(id);
                        }
                        break;
                }
                if (id > maxId) {
                    maxId = id;
                }
            }
            taskManager.generateId = maxId + 1;

            // Обновление длительности и времени начала/окончания эпиков на основе подзадач
            for (Epic epic : taskManager.epicMap.values()) {
                List<Subtask> subtasks = new ArrayList<>();
                for (Integer subtaskId : epic.getIdsSubtask()) {
                    subtasks.add(taskManager.subtaskMap.get(subtaskId));
                }
                epic.updateDurationAndTime(subtasks);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при загрузке данных.", e);
        }
        return taskManager;
    }

    @Override
    public Task createTask(Task task) throws TimeOverlapException {
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
    public Subtask createSubtask(Subtask subtask) throws TimeOverlapException {
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