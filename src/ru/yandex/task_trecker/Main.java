package ru.yandex.task_trecker;

import ru.yandex.task_trecker.service.*;
import ru.yandex.task_trecker.task_data.*;

public class Main {
    public static void main(String[] args) {
        Managers managers = new Managers();

        // Создаем две обычные задачи
        Task task1 = new Task("Переезд", "Перевозка вещей в новый дом", Status.NEW);
        Task task2 = new Task("Покупка еды", "Составить список покупок", Status.NEW);
        managers.getDefault().createTask(task1);
        managers.getDefault().createTask(task2);

        // Создаем эпик с двумя подзадачами
        Epic epic1 = new Epic("Организация семейного праздника", "Подготовка к празднику");
        managers.getDefault().createEpic(epic1);
        SubTask subtask1 = new SubTask("Купить декорации", "Найти магазин", Status.NEW);
        SubTask subtask2 = new SubTask("Заказать торт", "Выбрать кондитера", Status.NEW);
        managers.getDefault().createSubtask(subtask1, 3);
        managers.getDefault().createSubtask(subtask2, 3);

        // Создаем эпик с одной подзадачей
        Epic epic2 = new Epic("Покупка квартиры", "Выбор и покупка квартиры");
        managers.getDefault().createEpic(epic2);
        SubTask subtask3 = new SubTask("Осмотр квартир", "Записаться на просмотры", Status.NEW);
        managers.getDefault().createSubtask(subtask3, 6);


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
        managers.getDefault().updateSubTask(subtask3);

        printAllTasks(managers);

        // Удаляем одну обычную задачу и один эпик (а с эпиком автоматически удалятся его подзадачи)
        managers.getDefault().deleteTaskPerId(task2.getId());
        managers.getDefault().deleteEpicPerId(epic2.getId());

        printAllTasks(managers);
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
        for (Task subtask : managers.getDefault().getSubTasks()) {
            System.out.println(subtask);
        }
        System.out.println("История:");
        for (Task task : managers.getDefaultHistory().getHistory()) {
            System.out.println(task);
        }
    }
}
