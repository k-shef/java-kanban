import manager.FileBackedTaskManager;
import manager.InMemoryTaskHistoryManager;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import model.StatusTask;
import model.Subtask;
import model.Task;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new InMemoryTaskManager(new InMemoryTaskHistoryManager());
        File file = new File("src/tasks.csv");  // Укажите путь к файлу
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileBackedTaskManager taskFromFile = FileBackedTaskManager.loadFromFile(file);
        // Создание задач
        Task task1 = new Task("В магазин", "Сходить в пятерочку", StatusTask.NEW);
        Task task2 = new Task("Уборка", "Загрузить посудомойку и запустить пылесос", StatusTask.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskFromFile.createTask(task1);
        taskFromFile.createTask(task2);

        // Создание эпиков и подзадач
        Epic epic1 = new Epic("Починить авто", "Ремонт подвески машины", StatusTask.NEW);
        taskManager.createEpic(epic1);
        taskFromFile.createEpic(epic1);
        Subtask subtask1 = new Subtask("Купить запчасти", "Выбрать и заказать запчасти", StatusTask.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Отдать в сервис", "Отвезти машину в сервис", StatusTask.NEW, epic1.getId());
        Subtask subtask3 = new Subtask("Забрать из сервиса", "Приехать в сервис за авто", StatusTask.NEW, epic1.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);
        taskFromFile.createSubtask(subtask1);
        taskFromFile.createSubtask(subtask2);
        taskFromFile.createSubtask(subtask3);


        Epic epic2 = new Epic("Велосипед", "Купить велик ребенку", StatusTask.NEW);
        taskManager.createEpic(epic2);
        taskFromFile.createEpic(epic2);

        // Запрос созданных задач и печать истории
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getSubtasksById(subtask1.getId());
        taskManager.getSubtasksById(subtask2.getId());
        taskManager.getSubtasksById(subtask3.getId());
        // Запрос созданных задач и печать истории from file
        taskFromFile.getTaskById(task1.getId());
        taskFromFile.getTaskById(task2.getId());
        taskFromFile.getTaskById(epic1.getId());
        taskFromFile.getTaskById(epic2.getId());
        taskFromFile.getSubtasksById(subtask1.getId());
        taskFromFile.getSubtasksById(subtask2.getId());
        taskFromFile.getSubtasksById(subtask3.getId());


        System.out.println("История после запросов:");
        printHistory(taskManager);
        System.out.println();
        System.out.println("История после запросов from file:");
        printHistory(taskFromFile);

        // Удаление задачи, которая есть в истории
        taskManager.removeTaskById(task1.getId());
        System.out.println("\nИстория после удаления задачи 1:");
        printHistory(taskManager);

        // Удаление эпика с тремя подзадачами
        taskManager.removeEpicById(epic1.getId());
        System.out.println("\nИстория после удаления эпика 1 с подзадачами:");
        printHistory(taskManager);

        // Печать списков эпиков, задач и подзадач
        System.out.println("\nСписок эпиков:");
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
        System.out.println("Статус подзадачи \"" + subtask3.getName() + "\" для эпика \"" + epic1.getName() + "\": " + subtask3.getStatus());

        // Проверка статуса эпика после изменения
        Epic epik = taskManager.getEpicById(epic1.getId());
        if (epik != null) {
            System.out.println("\nСтатус эпика \"" + epik.getName() + "\": " + epik.getStatus());
        } else {
            System.out.println("Эпик не найден.");
        }

        // Удаление задач и подзадач
        taskManager.removeTaskById(task2.getId());
        taskManager.removeSubtaskById(subtask1.getId());

        // Проверка после удаления
        System.out.println("\nСписок задач после удаления задачи 2:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task.getName());
        }

        System.out.println("\nСписок эпиков после удаления эпика 1:");
        for (Epic epiic : taskManager.getAllEpics()) {
            System.out.println(epiic.getName());
        }

        System.out.println("\nСписок подзадач для эпика 1 после удаления подзадачи 1:");
        for (Subtask subtask : taskManager.getAllSubtasksByEpicId(epic1.getId())) {
            System.out.println(subtask.getName());
        }
    }

    private static void printHistory(TaskManager taskManager) {
        for (Task task : taskManager.getHistory()) {
            System.out.println(task.getName());
        }
    }
}
