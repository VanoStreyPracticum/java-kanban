package ru.yandex.task_trecker.service;

import ru.yandex.task_trecker.task_data.Task;

import java.util.*;


public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> head;
    private Node<Task> tail;
    private final Map<Integer, Node<Task>> nodeMap = new HashMap<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            throw new NullPointerException("Задача не может быть null");
        }
        remove(task.getId());
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node<Task> node = nodeMap.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            history.add(current.data);
            current = current.next;
        }
        return history;
    }

    private void linkLast(Task task) {
        Node<Task> newNode = new Node<>(tail, task, null);
        if (tail != null) {
            tail.next = newNode;
        } else {
            head = newNode;
        }
        tail = newNode;
        nodeMap.put(task.getId(), newNode);
    }

    private void removeNode(Node<Task> node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }
}
