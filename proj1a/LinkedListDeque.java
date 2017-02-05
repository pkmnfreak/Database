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
    private Item bleep;

    public void addFirst(Item bloop) {
        frontSentinel.next.prev = new IntNode(bloop, frontSentinel.next, frontSentinel);
        frontSentinel.next = frontSentinel.next.prev;
        size++;
    }

    public void addLast(Item bloop) {
        backSentinel.prev.next = new IntNode(bloop, backSentinel, backSentinel.prev);
        backSentinel.prev = backSentinel.prev.next;
        size++;
    }

    public boolean isEmpty() {
        return frontSentinel.next == backSentinel;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        int i = 0;
        IntNode p = new IntNode(bleep, frontSentinel.next, backSentinel);
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
            IntNode p = new IntNode(bleep, frontSentinel.next, backSentinel);
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
        frontSentinel = new IntNode(bleep, null, null);
        backSentinel = new IntNode(bleep, null, null);
        backSentinel.prev = frontSentinel;
        frontSentinel.next = backSentinel;
        size = 0;
    }
}
