package fix_atk_micro.util;

import java.util.NoSuchElementException;

public class CyclicQueue<T> {
    private Node<T> front;
    private Node<T> rear;
    private int size;

    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    /**
     * Adds an element to the back of the queue.
     *
     * @param element the element to add
     */
    public void add(T element) {
        Node<T> newNode = new Node<>(element);
        if (size == 0) {
            front = rear = newNode;
            rear.next = front; // Point to itself to form a cycle
        } else {
            rear.next = newNode;
            newNode.next = front;
            rear = newNode;
        }
        size++;
    }

    /**
     * Removes the front element and moves it to the back of the queue.
     *
     * @throws NoSuchElementException if the queue is empty
     */
    public void pop() {
        if (size == 0) {
            throw new NoSuchElementException("Queue is empty");
        }
        if (size > 1) {
            rear = front;
            front = front.next;
        }
    }

    /**
     * Peeks at the front element without removing it.
     *
     * @return the front element
     * @throws NoSuchElementException if the queue is empty
     */
    public T peek() {
        if (size == 0) {
            throw new NoSuchElementException("Queue is empty");
        }
        return front.data;
    }

    /**
     * Returns the number of elements in the queue.
     *
     * @return the size of the queue
     */
    public int size() {
        return size;
    }

    /**
     * Checks if the queue is empty.
     *
     * @return true if the queue is empty, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }
}
