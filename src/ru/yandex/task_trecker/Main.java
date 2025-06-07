package ru.yandex.task_trecker;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        // Создаем две обычные задачи
        Task task1 = new Task("Переезд", "Перевозка вещей в новый дом", Status.NEW);
        Task task2 = new Task("Покупка еды", "Составить список покупок", Status.NEW);
        manager.createTask(task1);
        manager.createTask(task2);

        // Создаем эпик с двумя подзадачами
        Epic epic1 = new Epic("Организация семейного праздника", "Подготовка к празднику");
        manager.createEpic(epic1);
        SubTask subtask1 = new SubTask("Купить декорации", "Найти магазин", Status.NEW, epic1.getId());
        SubTask subtask2 = new SubTask("Заказать торт", "Выбрать кондитера", Status.NEW, epic1.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        // Создаем эпик с одной подзадачей
        Epic epic2 = new Epic("Покупка квартиры", "Выбор и покупка квартиры");
        manager.createEpic(epic2);
        SubTask subtask3 = new SubTask("Осмотр квартир", "Записаться на просмотры", Status.NEW, epic2.getId());
        manager.createSubtask(subtask3);

        // Выводим текущие состояния задач
        System.out.println("----- Список обычных задач -----");
        for (Task t : manager.getTasks()) {
            System.out.println(t);
        }

        System.out.println("\n----- Список эпиков -----");
        for (Epic e : manager.getEpics()) {
            System.out.println(e);
        }

        System.out.println("\n----- Список подзадач -----");
        for (SubTask s : manager.getSubTasks()) {
            System.out.println(s);
        }

        // Изменяем статусы
        task1.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task1);

        // Для эпика 1 меняем статусы обеих подзадач для перехода в DONE
        subtask1.setStatus(Status.DONE);
        manager.updateSubTask(subtask1);
        subtask2.setStatus(Status.DONE);
        manager.updateSubTask(subtask2);

        // Для эпика 2 меняем статус подзадачи на IN_PROGRESS
        subtask3.setStatus(Status.IN_PROGRESS);
        manager.updateSubTask(subtask3);

        System.out.println("\n----- После обновления статусов -----");
        System.out.println("Обычные задачи:");
        for (Task t : manager.getTasks()) {
            System.out.println(t);
        }

        System.out.println("\nЭпики:");
        for (Epic e : manager.getEpics()) {
            System.out.println(e);
        }

        System.out.println("\nПодзадачи:");
        for (SubTask s : manager.getSubTasks()) {
            System.out.println(s);
        }

        // Удаляем одну обычную задачу и один эпик (а с эпиком автоматически удалятся его подзадачи)
        manager.deleteTaskPerId(task2.getId());
        manager.deleteEpicPerId(epic2.getId());

        System.out.println("\n----- После удаления обычной задачи и эпика -----");
        System.out.println("Обычные задачи:");
        for (Task t : manager.getTasks()) {
            System.out.println(t);
        }

        System.out.println("\nЭпики:");
        for (Epic e : manager.getEpics()) {
            System.out.println(e);
        }

        System.out.println("\nПодзадачи:");
        for (SubTask s : manager.getSubTasks()) {
            System.out.println(s);
        }
    }
}
