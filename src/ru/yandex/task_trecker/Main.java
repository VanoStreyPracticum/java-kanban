package ru.yandex.task_trecker;

import ru.yandex.task_trecker.service.Managers;
import ru.yandex.task_trecker.service.Status;
import ru.yandex.task_trecker.service.TaskManager;
import ru.yandex.task_trecker.task_data.Epic;
import ru.yandex.task_trecker.task_data.SubTask;
import ru.yandex.task_trecker.task_data.Task;

public class Main {
    public static void main(String[] args) {
        /**
         *      Первую версию кода ФЗ 5 спринта я отправил случайно(
         *      Случайно нажал на кнопку, прошу прошения!
         *      Можете пожалуйста не считать первую версию кода, первой попыткой?
         */


        TaskManager manager = Managers.getDefault();

        // Создаем две обычные задачи
        Task task1 = new Task("Переезд", "Перевозка вещей в новый дом", Status.NEW);
        Task task2 = new Task("Покупка еды", "Составить список покупок", Status.NEW);
        manager.createTask(task1);
        manager.createTask(task2);

        // Создаем эпик с двумя подзадачами
        Epic epic1 = new Epic("Организация семейного праздника", "Подготовка к празднику");
        manager.createEpic(epic1);
        SubTask subtask1 = new SubTask("Купить декорации", "Найти магазин", Status.NEW);
        SubTask subtask2 = new SubTask("Заказать торт", "Выбрать кондитера", Status.NEW);
        manager.createSubtask(subtask1, 3);
        manager.createSubtask(subtask2, 3);

        // Создаем эпик с одной подзадачей
        Epic epic2 = new Epic("Покупка квартиры", "Выбор и покупка квартиры");
        manager.createEpic(epic2);
        SubTask subtask3 = new SubTask("Осмотр квартир", "Записаться на просмотры", Status.NEW);
        manager.createSubtask(subtask3, 6);


        printAllTasks(manager);

        // Изменяем статусы
        task1.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task1);

        // Для эпика 1 меняем статусы обеих подзадач для перехода в DONE
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        manager.updateEpicStatus(subtask1.getEpicId());

        // Для эпика 2 меняем статус подзадачи на IN_PROGRESS
        subtask3.setStatus(Status.IN_PROGRESS);
        manager.updateSubTask(subtask3);

        printAllTasks(manager);

        // Удаляем одну обычную задачу и один эпик (а с эпиком автоматически удалятся его подзадачи)
        manager.deleteTaskPerId(task2.getId());
        manager.deleteEpicPerId(epic2.getId());

        printAllTasks(manager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasksOfEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubTasks()) {
            System.out.println(subtask);
        }
        System.out.println("История:");
        for (Task task : Managers.getDefaultHistory().getHistory()) {
            System.out.println(task);
        }
    }
}
