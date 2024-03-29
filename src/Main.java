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
        Epic epic1 = new Epic("Починить авто", "Ремонт подвески машины", StatusTask.NEW, 0);
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Купить запчасти", "Выбрать и заказать запчасти", StatusTask.NEW, 0, epic1.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Отдать в сервис", "Отвезти машину в сервис", StatusTask.NEW, 0, epic1.getId());
        taskManager.createSubtask(subtask2);

// Создание эпика с одной подзадачей
        Epic epic2 = new Epic("Велосипед", "Купить велик ребенку", StatusTask.NEW, 0);
        taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("Выбор велосипеда", "Выбрать и купить велосипед", StatusTask.NEW, 0, epic2.getId());
        taskManager.createSubtask(subtask3);

        // Печать списков эпиков, задач и подзадач
        System.out.println("Список эпиков:");
        for (Epic epic : taskManager.getAllEpics()) {
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

        // Проверка статуса эпика
        Epic epic = taskManager.getEpicById(epic1.getId());
        if (epic != null) {
            System.out.println("Статус эпика \"" + epic1.getName() + "\": " + epic.getStatus());
        } else {
            System.out.println("Эпик не найден.");
        }

        // Изменение статусов задач и подзадач
        task1.setStatus(StatusTask.IN_PROGRESS);
        subtask1.setStatus(StatusTask.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(StatusTask.DONE);
        taskManager.updateSubtask(subtask2);
        subtask3.setStatus(StatusTask.IN_PROGRESS);
        taskManager.updateSubtask(subtask3);

        // Печать измененных статусов
        System.out.println("\nСтатус задачи \"" + task1.getName() + "\": " + task1.getStatus());
        System.out.println("\nСтатус подзадачи \"" + subtask1.getName() + "\" для эпика \"" + epic1.getName() + "\": " + subtask1.getStatus());
        System.out.println("Статус подзадачи \"" + subtask2.getName() + "\" для эпика \"" + epic1.getName() + "\": " + subtask2.getStatus());
        System.out.println("Статус подзадачи \"" + subtask3.getName() + "\" для эпика \"" + epic2.getName() + "\": " + subtask3.getStatus());

        // Проверка статуса эпика после изменения
        Epic epik = taskManager.getEpicById(epic1.getId());
        if (epic != null) {
            System.out.println("\nСтатус эпика \"" + epic.getName() + "\": " + epic.getStatus());
        } else {
            System.out.println("Эпик не найден.");
        }

        // Удаление одной из задач и одного из эпиков и подзадачи
        taskManager.removeTaskById(task1.getId());
        taskManager.removeEpicById(epic2.getId());
        taskManager.removeSubtaskById(subtask1.getId());

        // Проверка после удаления
        System.out.println("\nСписок задач после удаления задачи 1:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task.getName());
        }

        System.out.println("\nСписок эпиков после удаления эпика 2:");
        for (Epic epiic : taskManager.getAllEpics()) {
            System.out.println(epic.getName());
        }

        System.out.println("\nСписок подзадач для эпика 1 после удаления подзадачи 1:");
        for(Subtask subtask : taskManager.getAllSubtasksByEpicId(epic1.getId())) {
            System.out.println(subtask.getName());
        }
    }
}