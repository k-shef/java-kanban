package manager;



public class Managers {
    public TaskManager getDefault() {
        TaskHistoryManager historyManager = new InMemoryTaskHistoryManager();
        return new InMemoryTaskManager(historyManager);
    }
}
