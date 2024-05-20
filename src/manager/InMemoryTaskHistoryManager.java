package manager;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskHistoryManager implements TaskHistoryManager {

    private final static int MAX_HISTORY_SIZE = 10;
    private final List<Task> tasksHistory = new ArrayList<>();

    @Override
    public void addToHistory(Task task) {
        if (task != null) {
            if (tasksHistory.size() == MAX_HISTORY_SIZE) {
                tasksHistory.removeFirst();
            }
            tasksHistory.add(task);
        }
    }
    @Override
    public List<Task> getHistory() {
        return List.copyOf(tasksHistory) ;
    }
}
