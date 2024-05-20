package model;

import model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> idsSubtask;

    public Epic(String name, String description, StatusTask status) {
        super(name, description, status);
        this.idsSubtask = new ArrayList<>();
    }

    public List<Integer> getIdsSubtask() {
        return idsSubtask;
    }

    public void addIdSubtasks(Integer idSubtask) {
        idsSubtask.add(idSubtask);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(idsSubtask, epic.idsSubtask);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), idsSubtask);
    }
}
