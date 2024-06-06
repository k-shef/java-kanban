package manager;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskHistoryManager implements TaskHistoryManager {
    private final HashMap<Integer, Node<Task>> tasks = new HashMap<>();
    private Node<Task> firstNode = null;
    private Node<Task> lastNode = null;

    private static class Node<T> {
        private final T item;
        private Node<T> next;
        private Node<T> prev;

        Node(Node<T> prev, T item, Node<T> next) {
            this.prev = prev;
            this.item = item;
            this.next = next;
        }
    }

    @Override
    public void addToHistory(Task task) {
        if (task != null) {
            if (tasks.containsKey(task.getId())) {
                removeNode(tasks.get(task.getId()));
            }
            tasks.put(task.getId(), linkLast(task));
        }
    }

    @Override
    public void remove(int id) {
        removeNode(tasks.get(id));
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node<Task> currentNode = firstNode;
        while (currentNode != null) {
            history.add(currentNode.item);
            currentNode = currentNode.next;
        }
        return history;
    }

    private boolean isEmpty() {
        return firstNode == null;
    }

    private Node<Task> linkLast(Task task) {
        Node<Task> newNode = new Node<>(lastNode, task, null);
        if (isEmpty()) {
            firstNode = newNode;
        } else {
            lastNode.next = newNode;
        }
        lastNode = newNode;
        return newNode;
    }

    private void removeNode(Node<Task> node) {
        if (node == null) return;

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            firstNode = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            lastNode = node.prev;
        }

        tasks.remove(node.item.getId());
    }
}
