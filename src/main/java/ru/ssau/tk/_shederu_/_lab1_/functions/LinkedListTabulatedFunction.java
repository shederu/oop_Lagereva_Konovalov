package ru.ssau.tk._shederu_._lab1_.functions;

public class LinkedListTabulatedFunction extends AbstractTabulatedFunction implements Removable {

    private static class Node{
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



private void addNode(double x, double y){
    Node nNode = new Node(x, y);
    if(head == null){
        head = nNode;
        head.prev = head;
        head.next = head;
    }
    else{
        Node last = head.prev;
        last.next = nNode;
        head.prev = nNode;
        nNode.next = head;
        nNode.next = last;
    }

    ++count;
    }

    public LinkedListTabulatedFunction(double[] xValues, double[] yValues){
        for (int i = 0; i < xValues.length; ++i) {
            addNode(xValues[i], yValues[i]);
        }

    }

    public LinkedListTabulatedFunction(MathFunctions source, double xFrom, double xTo, int count){
        if(xFrom>xTo){
            double t = xFrom;
            xFrom = xTo;
            xTo = t;
        }

        if(xTo == xFrom){
            int i = 0;
            while(i < count){
                addNode(xFrom, source.apply(xFrom));
                ++i;
            }
        }


        double step = (xTo - xFrom) / (count - 1);
        double x = xFrom;

        for (int i = 0; i < count; i++) {
            addNode(x, source.apply(x));
            x += step;
        }
    }
    private Node getNode(int index){
        Node p=head;
        for(int i = 0; i<index; ++i){
               p=p.next;
        }
        return p;
    }


    @Override
    public int getCount() { return count; }

    @Override
    public double leftBound() { return head.x; }

    @Override
    public double rightBound() { return head.prev.x; }

    @Override
    public double getX(int index) { return getNode(index).x; }

    @Override
    public double getY(int index) { return getNode(index).y; }

    @Override
    public void setY(int index, double value){ getNode(index).y = value; }

    @Override
    public int indexOfX(double x){
    Node p = head;
    int i = 0;
        do {
           if(p.x == x){
               return i;
           }
           p = p.next;
           ++i;
        } while(p.next!=head);
       return -1;
    }

    @Override
    public int indexOfY(double y){
        Node p = head;
        int i = 0;
        do {
            if(p.y == y){
                return i;
            }
            p = p.next;
            ++i;
        } while(p.next!=head);
        return -1;
    }


    @Override
    protected int floorIndexOfX(double x){

    if(head.x>x) {
        return 0;
    }
        Node p = head;
        int i = 0;

        do{
            if(p.x>=x){
                return i -1 ;
            }
            p=p.next;
            ++i;
        }while(p!=head);

        return count;
    }

    @Override
    protected double extrapolateLeft(double x){
        if(head.next == head) return head.y;
        Node f = head;
        Node s = head.next;

        return f.y + (s.y-f.y)/(s.x-f.x)*(x- f.x);
    }

    @Override
    protected double extrapolateRight(double x){
        if(head.next == head) return head.y;
        Node s = head.prev;
        Node f = s.prev;

        return f.y + (s.y-f.y)/(s.x-f.x)*(x- f.x);
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        if(head.next == head) return head.y;
        Node lNode = getNode(floorIndex);
        Node rNode = getNode(floorIndex + 1);
        return lNode.y + (rNode.y - lNode.y) / (rNode.x - lNode.x) * (x - lNode.x);
    }

    protected double interpolate(double x, double leftX, double rightX, double leftY, double rightY) { return leftY + (rightY - leftY) / (rightX - leftX) * (x - leftX); }

    @Override
    public void remove(int index) {
        Node nToRem = getNode(index);

        Node prevNode = nToRem.prev;
        Node nextNode = nToRem.next;

        prevNode.next = nextNode;
        nextNode.prev = prevNode;

        if (nToRem == head) {
            head = nextNode;
        }

        count--;
    }

    @Override
    public void insert(double x, double y) {
        if (head == null) {
            addNode(x, y);
            return;
        }

        Node current = head;
        do {
            if (current.x == x) {
                current.y = y;
                return;
            }
            current = current.next;
        } while (current != head);

        current = head;
        Node newNode = new Node(x, y);

        do {
            if (current.x > x) {
                break;
            }
            current = current.next;
        } while (current != head);

        Node prevNode = current.prev;

        newNode.next = current;
        newNode.prev = prevNode;
        prevNode.next = newNode;
        current.prev = newNode;

        if (current == head && head.x > x) {
            head = newNode;
        }
        count++;
    }
}
