public class LinkedListDeque<Item> {

    private class IntNode {
        private Item item;
        private IntNode next;
        private IntNode prev;
        private IntNode(Item i, IntNode n, IntNode p) {
            item = i;
            next = n;
            prev = p;
        }
    }

    private IntNode frontSentinel;
    private IntNode backSentinel;
    private int size;
    private Item item;

    public void addFirst(Item item) {
        frontSentinel.next.prev = new IntNode(item, frontSentinel.next, frontSentinel);
        frontSentinel.next = frontSentinel.next.prev;
        size++;
    }

    public void addLast(Item item) {
        backSentinel.prev.next = new IntNode(item, backSentinel, backSentinel.prev);
        backSentinel.prev = backSentinel.prev.next;
        size++;
    }

    public boolean isEmpty() {
        if (frontSentinel.next == backSentinel) {
            return true;
        } else {
            return false;
        }
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        int i = 0;
        IntNode p = new IntNode(item, frontSentinel.next, backSentinel);
        while (i < size) {
            System.out.print(p.next.item);
            System.out.print(" ");
            p = p.next;
            i++;
        }
    }

    public Item removeFirst() {
       if (frontSentinel.next == backSentinel) {
           return null;
       } else {
           Item p = frontSentinel.next.item;
           frontSentinel.next.next.prev = frontSentinel;
           frontSentinel.next = frontSentinel.next.next;
           size--;
           return p;
       }
    }

    public Item removeLast() {
        if (backSentinel.prev == frontSentinel) {
            return null;
        } else {
            Item p = backSentinel.prev.item;
            backSentinel.prev.prev.next = backSentinel;
            backSentinel.prev = backSentinel.prev.prev;
            size--;
            return p;
        }
    }

    public Item get(int index) {
        if (index == 0) {
            return frontSentinel.next.item;
        } else if (index >= size) {
            return null;
        } else {
            int i = 0;
            IntNode p = new IntNode(item, frontSentinel.next, backSentinel);
            while (i <= index) {
                p = p.next;
                i++;
            }
            return p.item;
        }
    }

    private Item getRecursiveH(IntNode first, int index) {
        if (first.next == null || index == 0) {
            return first.item;
        } else {
            first = first.next;
            index--;
            return getRecursiveH(first, index);
        }
    }

    public Item getRecursive(int index) {
        if (index >= size) {
            return null;
        }
        return getRecursiveH(frontSentinel.next, index);
    }

    public LinkedListDeque() {
        frontSentinel = new IntNode(item, null, null);
        backSentinel = new IntNode(item, null, null);
        backSentinel.prev = frontSentinel;
        frontSentinel.next = backSentinel;
        size = 0;
    }
}
