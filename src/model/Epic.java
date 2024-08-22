package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> idsSubtask;
    private LocalDateTime endTime;

    public Epic(String name, String description, StatusTask status) {
        this(name, description, status, new ArrayList<>());
    }

    public Epic(String name, String description, StatusTask status, List<Subtask> subtasks) {
        super(name, description, status, Duration.ZERO, null);
        this.idsSubtask = new ArrayList<>();
        this.endTime = null;
    }


    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public List<Integer> getIdsSubtask() {
        return idsSubtask;
    }

    public void addIdSubtasks(Integer idSubtask) {
        if (!idsSubtask.contains(idSubtask)) {
            idsSubtask.add(idSubtask);
        }
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

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", id=" + getId() +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                ", idsSubtask=" + idsSubtask +
                '}';
    }
}