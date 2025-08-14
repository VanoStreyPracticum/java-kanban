package ru.yandex.task_trecker.exceptions;

public class OverlappingTaskException extends TaskManagerException {
    public OverlappingTaskException(String message) {
        super(message);
    }
}
