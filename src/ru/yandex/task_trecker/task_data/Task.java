package ru.yandex.task_trecker.task_data;

import ru.yandex.task_trecker.service.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected Status status;
    protected long duration;
    protected LocalDateTime startTime;

    public Task(String name, String description, Status status, long duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        startTime = LocalDateTime.now();
    }

    public LocalDateTime getEndTime(){
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

    public Duration getDuration(){
        return Duration.ofMinutes(duration);
    }

    public LocalDateTime getStartTime(){
        return startTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setDuration(long duration){
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime){
        this.startTime = startTime;
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
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }
}

