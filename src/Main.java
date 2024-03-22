public class Main {
    public static void main(String[] args) {
        // Создание объекта TaskManager
        TaskManager taskManager = new TaskManager();

        // Создание двух задач
        Task task1 = new Task("В магазин", "Сходить в пятерочку", StatusTask.NEW, 0);
        Task task2 = new Task("Уборка", "Загрузить посудомойку и запустить пылесос", StatusTask.NEW, 0);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        // Создание эпика с двумя подзадачами
        Epic epic1 = taskManager.createEpic("Починить авто", "Ремонт подвески машины");
        Subtask subtask1 = taskManager.createSubtask("Купить запчасти", "Выбрать и заказать запчасти", epic1.getId());
        Subtask subtask2 = taskManager.createSubtask("Отдать в сервис", "Отвезти машину в сервис", epic1.getId());

        // Создание эпика с одной подзадачей
        Epic epic2 = taskManager.createEpic("Велосипед", "Купить велик ребенку");
        Subtask subtask3 = taskManager.createSubtask("Выбор велосипеда", "Выбрать и купить велосипед", epic2.getId());

        // Печать списков эпиков, задач и подзадач
        System.out.println("Список эпиков:");
        for (Epic epic : taskManager.getAllEpic()) {
            System.out.println(epic.getName());
        }

        System.out.println("\nСписок задач:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task.getName());
        }

        System.out.println("\nСписок подзадач для эпика 1:");
        for (Subtask subtask : taskManager.getAllSubtasksByEpicId(epic1.getId())) {
            System.out.println(subtask.getName());
        }

        // Изменение статусов задач и подзадач
        task1.setStatus(StatusTask.IN_PROGRESS);
        subtask1.setStatus(StatusTask.IN_PROGRESS);
        subtask2.setStatus(StatusTask.DONE);
        subtask3.setStatus(StatusTask.IN_PROGRESS);

        // Печать измененных статусов
        System.out.println("\nСтатус задачи \"" + task1.getName() + "\": " + task1.getStatus());
        System.out.println("Статус подзадачи \"" + subtask1.getName() + "\" для эпика \"" + epic1.getName() + "\": " + subtask1.getStatus());
        System.out.println("Статус подзадачи \"" + subtask2.getName() + "\" для эпика \"" + epic1.getName() + "\": " + subtask2.getStatus());
        System.out.println("Статус подзадачи \"" + subtask3.getName() + "\" для эпика \"" + epic2.getName() + "\": " + subtask3.getStatus());

        // Удаление одной из задач и одного из эпиков
        taskManager.removeTaskById(task1.getId());
        taskManager.removeEpicById(epic2.getId());

        // Проверка после удаления
        System.out.println("\nСписок задач после удаления задачи 1:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task.getName());
        }

        System.out.println("\nСписок эпиков после удаления эпика 2:");
        for (Epic epic : taskManager.getAllEpic()) {
            System.out.println(epic.getName());
        }
    }


}