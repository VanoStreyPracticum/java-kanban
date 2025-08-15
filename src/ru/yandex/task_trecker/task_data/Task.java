package ru.yandex.task_trecker.task_data;

import ru.yandex.task_trecker.service.Status;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;

public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected Status status;
    protected long duration;
    protected LocalTime startTime;

    public Task(String name, String description, Status status, long duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
    }

    public LocalTime getEndTime() {
        return startTime.plusMinutes(duration);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public Duration getDuration() {
        return Duration.ofMinutes(duration);
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public boolean isOverlapping(Task other) {
        return (this.getStartTime().isBefore(other.getEndTime()) && this.getEndTime().isAfter(other.getStartTime()));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && duration == task.duration && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status, duration, startTime);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + Duration.ofMinutes(duration) +
                ", startTime=" + startTime +
                '}';
    }
}

