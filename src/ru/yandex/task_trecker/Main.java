package ru.yandex.task_trecker;

import ru.yandex.task_trecker.service.*;
import ru.yandex.task_trecker.task_data.*;

import java.time.LocalTime;

public class Main {
    static LocalTime updatedTime = LocalTime.of(9, 0);

    public static void main(String[] args) {
        Managers managers = new Managers();
        // Создаем две обычные задачи
        Task task1 = new Task("Переезд", "Перевозка вещей в новый дом", Status.NEW, 50);
        task1.setStartTime(addTime()); // Устанавливаем время начала для задачи
        Task task2 = new Task("Покупка еды", "Составить список покупок", Status.NEW, 10);
        task2.setStartTime(addTime());
        managers.getDefault().createTask(task1);
        managers.getDefault().createTask(task2);

        // Создаем эпик с двумя подзадачами
        Epic epic1 = new Epic("Организация семейного праздника", "Подготовка к празднику");
        epic1.setStartTime(addTime());
        managers.getDefault().createEpic(epic1);
        Subtask subtask1 = new Subtask("Купить декорации", "Найти магазин", Status.NEW, 10);
        subtask1.setStartTime(addTime());
        Subtask subtask2 = new Subtask("Заказать торт", "Выбрать кондитера", Status.NEW, 10);
        subtask2.setStartTime(addTime());
        managers.getDefault().createSubtask(subtask1, epic1.getId());
        managers.getDefault().createSubtask(subtask2, epic1.getId());

        // Создаем эпик с одной подзадачей
        Epic epic2 = new Epic("Покупка квартиры", "Выбор и покупка квартиры");
        epic2.setStartTime(addTime());
        managers.getDefault().createEpic(epic2);
        Subtask subtask3 = new Subtask("Осмотр квартир", "Записаться на просмотры", Status.NEW, 10);
        subtask3.setStartTime(addTime());
        managers.getDefault().createSubtask(subtask3, epic2.getId());

        printAllTasks(managers);

        // Изменяем статусы
        task1.setStatus(Status.IN_PROGRESS);
        managers.getDefault().updateTask(task1);

        // Для эпика 1 меняем статусы обеих подзадач для перехода в DONE
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        managers.getDefault().updateEpicStatus(subtask1.getEpicId());

        // Для эпика 2 меняем статус подзадачи на IN_PROGRESS
        subtask3.setStatus(Status.IN_PROGRESS);
        managers.getDefault().updateEpicStatus(subtask3.getEpicId());

        printAllTasks(managers);

        // Удаляем одну обычную задачу и один эпик (а с эпиком автоматически удалятся его подзадачи)
        managers.getDefault().deleteTaskPerId(task2.getId());
        managers.getDefault().deleteEpicPerId(epic2.getId());

        printAllTasks(managers);
    }

    private static LocalTime addTime() {
        updatedTime = updatedTime.plusMinutes(60);
        return updatedTime;
    }

    private static void printAllTasks(Managers managers) {
        System.out.println("Задачи:");
        for (Task task : managers.getDefault().getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : managers.getDefault().getEpics()) {
            System.out.println(epic);
            for (Task task : managers.getDefault().getSubtasksOfEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : managers.getDefault().getSubtasks()) {
            System.out.println(subtask);
        }
        System.out.println("История:");
        for (Task task : managers.getDefaultHistory().getHistory()) {
            System.out.println(task);
        }
        System.out.println("Приоритет:");
        for (Task task : managers.getDefault().getPrioritizedTasks()) {
            System.out.println(task);
        }
    }
}
