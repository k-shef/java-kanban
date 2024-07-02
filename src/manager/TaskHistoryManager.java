package manager;

import model.Task;

import java.util.List;

public interface TaskHistoryManager {
    void addToHistory(Task task);

    void remove(int id);

    List<Task> getHistory();
}
