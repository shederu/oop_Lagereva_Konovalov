package ru.ssau.tk._shederu_._lab1_.functions;

public class LinkedListTabulatedFunction extends AbstractTabulatedFunction implements Removable {
    private static final double eRate = 1e-9;

    private static class Node {
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

    private void addNode(double x, double y) {
        Node nNode = new Node(x, y);
        if (head == null) {
            head = nNode;
            head.prev = head;
            head.next = head;
        } else {
            Node last = head.prev;
            last.next = nNode;
            nNode.prev = last;
            nNode.next = head;
            head.prev = nNode;
        }
        ++count;
    }

    public LinkedListTabulatedFunction(double[] xValues, double[] yValues) {
        if (xValues.length != yValues.length) {
            throw new IllegalArgumentException("Arrays must have same length");
        }
        if (xValues.length < 2) {
            throw new IllegalArgumentException("At least 2 points required");
        }
        for (int i = 0; i < xValues.length; ++i) {
            addNode(xValues[i], yValues[i]);
        }
    }

    public LinkedListTabulatedFunction(MathFunction source, double xFrom, double xTo, int count) {
        if (count < 2) {
            throw new IllegalArgumentException("At least 2 points required");
        }
        if (xFrom > xTo) {
            double t = xFrom;
            xFrom = xTo;
            xTo = t;
        }

        double step = (xTo - xFrom) / (count - 1);
        double x = xFrom;

        for (int i = 0; i < count; i++) {
            addNode(x, source.apply(x));
            x += step;
        }
    }

    private Node getNode(int index) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Index out of bounds");
        }
        Node p = head;
        for (int i = 0; i < index; ++i) {
            p = p.next;
        }
        return p;
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
        return getNode(index).x;
    }

    @Override
    public double getY(int index) {
        return getNode(index).y;
    }

    @Override
    public void setY(int index, double value) {
        getNode(index).y = value;
    }

    @Override
    public int indexOfX(double x) {
        if (head == null) return -1;

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

        Node current = head;
        for (int i = 0; i < count; i++) {
            if (Math.abs(current.y - y) < eRate) {
                return i;
            }
            current = current.next;
        }
        return -1;
    }

    @Override
    protected int floorIndexOfX(double x) {
        if (x < getX(0)) return 0;
        for (int i = 0; i < count - 1; i++) {
            if (x >= getX(i) && x < getX(i + 1)) {
                return i;
            }
        }
        return count - 1;
    }

    @Override
    protected double extrapolateLeft(double x) {
        return interpolate(x, getX(0), getX(1), getY(0), getY(1));
    }

    @Override
    protected double extrapolateRight(double x) {
        return interpolate(x, getX(count - 2), getX(count - 1), getY(count - 2), getY(count - 1));
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        return interpolate(x, getX(floorIndex), getX(floorIndex + 1), getY(floorIndex), getY(floorIndex + 1));
    }

    @Override
    protected double interpolate(double x, double leftX, double rightX, double leftY, double rightY) {
        return leftY + (rightY - leftY) / (rightX - leftX) * (x - leftX);
    }

    @Override
    public void remove(int index) {
        if (count == 1) {
            head = null;
            count = 0;
            return;
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
}