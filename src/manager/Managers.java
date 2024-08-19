package manager;


public class Managers {
    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskHistoryManager getDefaultHistory() {
        return new InMemoryTaskHistoryManager();
    }
}
