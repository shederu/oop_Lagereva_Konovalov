package ru.ssau.tk._shederu_._lab1_.functions;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.io.Serializable;
import java.io.Serial;

public class LinkedListTabulatedFunction extends AbstractTabulatedFunction implements Removable, TabulatedFunction, Serializable {
    private final double eRate = 1e-9;


    @Serial
    private static final long serialVersionUID = -6743567631108323096L;

    private static class Node implements Serializable {
        @Serial
        private static final long serialVersionUID = 3381324567890123456L;
        private Node next;
        private Node prev;
        private double x;
        private double y;

        public Node(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }


    private Node head = null;
    private int count = 0;


    public LinkedListTabulatedFunction(double[] xValues, double[] yValues) {
        if (xValues.length < 2) {
            throw new IllegalArgumentException("Длина таблицы должна быть не менее 2 точек");
        }

        if (xValues.length != yValues.length) {
            throw new IllegalArgumentException("Длина массивов должна быть одинакова.");
        }

        checkSorted(xValues);

        for (int i = 0; i < xValues.length; i++) {
            addNode(xValues[i], yValues[i]);
        }
    }


    private void addNode(double x, double y) {
        Node newNode = new Node(x, y);

        if (head == null) {
            head = newNode;
            head.prev = head;
            head.next = head;
        } else {
            Node last = head.prev;
            last.next = newNode;
            newNode.prev = last;
            newNode.next = head;
            head.prev = newNode;
        }

        count++;
    }

    public LinkedListTabulatedFunction(MathFunctions source, double xFrom, double xTo, int count) {
        if (count < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        if (xFrom > xTo) {
            double temp = xFrom;
            xFrom = xTo;
            xTo = temp;
        }

        if (xFrom == xTo) {
            double yValue = source.apply(xFrom);
            for (int i = 0; i < count; i++) {
                addNode(xFrom, yValue);
            }
        } else {
            double step = (xTo - xFrom) / (count - 1);
            for (int i = 0; i < count; i++) {
                double x = xFrom + i * step;
                double y = source.apply(x);
                addNode(x, y);
            }
        }
    }
    private Node getNode(int index) {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException("Индекс: " + index + ", Размер: " + count);
        }

        if (index < count / 2) {
            Node current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
            return current;
        } else {
            Node current = head.prev;
            for (int i = count - 1; i > index; i--) {
                current = current.prev;
            }
            return current;
        }
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public double leftBound() {
        return head.x;
    }

    @Override
    public double rightBound() {
        return head.prev.x;
    }

    @Override
    public double getX(int index) {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException("Невозможный индекс!");
        }
        return getNode(index).x;
    }

    @Override
    public double getY(int index) {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException("Невозможный индекс!");
        }
        return getNode(index).y;
    }

    @Override
    public void setY(int index, double value) {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException("Невозможный индекс!");
        }
        getNode(index).y = value;
    }

    @Override
    public int indexOfX(double x) {
        Node current = head;
        for (int i = 0; i < count; i++) {
            if (Math.abs(current.x - x) < eRate) {
                return i;
            }
            current = current.next;
        }
        return -1;
    }

    @Override
    public int indexOfY(double y) {
        if (head == null) return -1;
        Node p = head;
        int i = 0;
        do {
            if (Math.abs(p.y - y) < eRate) {
                return i;
            }
            p = p.next;
            ++i;
        } while (p != head);
        return -1;
    }

    @Override
    protected int floorIndexOfX(double x) {
        if (head == null) return 0;
        if (x < head.x) return 0;
        if (x > head.prev.x) return count - 1;

        Node current = head;
        int index = 0;
        while (index < count - 1) {
            if (x >= current.x && x < current.next.x) {
                return index;
            }
            current = current.next;
            index++;
        }
        return count - 1;
    }

    @Override
    protected double extrapolateLeft(double x) {
        if (head.next == head) return head.y;
        Node first = head;
        Node second = head.next;
        return interpolate(x, first.x, second.x, first.y, second.y);
    }

    @Override
    protected double extrapolateRight(double x) {
        if (head.next == head) return head.y;
        Node last = head.prev;
        Node secondLast = last.prev;
        return interpolate(x, secondLast.x, last.x, secondLast.y, last.y);
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        Node left = getNode(floorIndex);
        Node right = getNode(floorIndex + 1);
        return interpolate(x, left.x, right.x, left.y, right.y);
    }

    @Override
    protected double interpolate(double x, double leftX, double rightX, double leftY, double rightY) {
        return leftY + (rightY - leftY) / (rightX - leftX) * (x - leftX);
    }

    @Override
    public void remove(int index) {

        if (head == null) {
            throw new IllegalStateException("Список пуст");
        }

        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException("Невозможный индекс!");
        }

        Node nodeToRemove = getNode(index);

        Node prevNode = nodeToRemove.prev;
        Node nextNode = nodeToRemove.next;

        prevNode.next = nextNode;
        nextNode.prev = prevNode;

        if (nodeToRemove == head) {
            head = nextNode;
        }

        count--;
    }

    @Override
    public Iterator<Point> iterator() {
        return new Iterator<Point>() {
            private Node curNode = head;
            private int elementsRet = 0;

            @Override
            public boolean hasNext() {
                return elementsRet < count;
            }

            @Override
            public Point next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("Итератор не поддерживается.");
                }

                Point point = new Point(curNode.x, curNode.y);
                curNode = curNode.next;
                elementsRet++;

                return point;
            }
        };
    }
}