public class Subtask extends Task {
    private int epicId;

    public int getEpicId() {
        return epicId;
    }

    public Subtask(String name, String description, StatusTask status, int id) {
        super(name, description, status, id);
    }

    public Subtask(String name, String description, StatusTask status, int id, int epicId) {
        super(name, description, status, id);
        this.epicId = epicId;
    }
}
