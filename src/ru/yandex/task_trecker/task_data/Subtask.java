package ru.yandex.task_trecker.task_data;

import ru.yandex.task_trecker.service.Status;

import java.util.Objects;

public class Subtask extends Task {
    /**
     * Наличие этого поля epicId оправдана упрощением логики программы.(В результате меньше кода)
     * <p>
     * К тому же в ТЗ 4 спринта было сказанно что epic и subTask должны знать id друг друга.
     */

    private int epicId;

    public Subtask(String name, String description, Status status) {
        super(name, description, status);
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subTask = (Subtask) o;
        return epicId == subTask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + id +
                ", epicId=" + epicId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
