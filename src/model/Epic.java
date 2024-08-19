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
        updateDurationAndTime(subtasks);
    }

    public void updateDurationAndTime(List<Subtask> subtasks) {
        if (subtasks.isEmpty()) {
            this.setDuration(Duration.ZERO);
            this.setStartTime(null);
            this.endTime = null;
        } else {
            LocalDateTime start = subtasks.stream()
                    .map(Subtask::getStartTime)
                    .filter(Objects::nonNull)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);

            LocalDateTime end = subtasks.stream()
                    .map(subtask -> {
                        if (subtask.getStartTime() != null && subtask.getDuration() != null) {
                            return subtask.getStartTime().plus(subtask.getDuration());
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            Duration totalDuration = subtasks.stream()
                    .map(Subtask::getDuration)
                    .filter(Objects::nonNull)
                    .reduce(Duration.ZERO, Duration::plus);

            this.setDuration(totalDuration);
            this.setStartTime(start);
            this.endTime = end;
        }
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