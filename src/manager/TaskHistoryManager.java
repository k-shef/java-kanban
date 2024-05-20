package manager;

import model.Task;

import java.util.List;

public interface TaskHistoryManager {
    void addToHistory(Task task);

    List<Task> getHistory();
}
