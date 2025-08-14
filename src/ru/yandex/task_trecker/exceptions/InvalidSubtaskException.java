package ru.yandex.task_trecker.exceptions;

public class InvalidSubtaskException extends TaskManagerException {
    public InvalidSubtaskException(String message) {
        super(message);
    }
}
