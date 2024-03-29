import java.util.ArrayList;
import java.util.HashMap;

 class Epic extends Task {
   private final ArrayList<Integer> idsSubtask;

    public void addIdSubtasks(int idSubtask) {
        idsSubtask.add(idSubtask);
    }

    public ArrayList<Integer> getIdsSubtask() {
        return idsSubtask;
    }

    public Epic(String name, String description, StatusTask status, int id) {
        super(name, description, status, id);
        this.idsSubtask = new ArrayList<>();
    }


}
