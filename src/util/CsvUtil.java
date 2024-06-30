package util;


import model.Epic;
import model.Subtask;
import model.Task;
import model.TypeTask;

public class CsvUtil {

    public static String taskToCsv(Task task) {
        return String.format("%d,%s,%s,%s,%s,", task.getId(), TypeTask.TASK, task.getName(), task.getStatus(), task.getDescription());
    }

    public static String epicToCsv(Epic epic) {
        StringBuilder idsSubtask = new StringBuilder();
        for (Integer id : epic.getIdsSubtask()) {
            idsSubtask.append(id).append(",");
        }
        if (idsSubtask.length() > 0) {
            idsSubtask.setLength(idsSubtask.length() - 1);
        }
        return String.format("%d,%s,%s,%s,%s,%s", epic.getId(), TypeTask.EPIC, epic.getName(), epic.getStatus(), epic.getDescription(), idsSubtask);
    }

    public static String subtaskToCsv(Subtask subtask) {
        return String.format("%d,%s,%s,%s,%s,%d", subtask.getId(), TypeTask.SUBTASK, subtask.getName(), subtask.getStatus(), subtask.getDescription(), subtask.getEpicId());
    }
}