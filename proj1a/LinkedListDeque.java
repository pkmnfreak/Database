public class LinkedListDeque<Item> {

    private class IntNode {
        public Item item;
        public IntNode next;
        public IntNode prev;
        public IntNode(Item i, IntNode n, IntNode p) {
            item = i;
            next = n;
            prev = p;
        }
    }

    private IntNode FrontSentinel;
    private IntNode BackSentinel;
    private int size;
    private Item item;

    public void addFirst(Item Item) {
        FrontSentinel.next.prev = new IntNode(Item, FrontSentinel.next, FrontSentinel);
        FrontSentinel.next = FrontSentinel.next.prev;
        size ++;
    }

    public void addLast(Item Item) {
        BackSentinel.prev.next = new IntNode(Item, BackSentinel, BackSentinel.prev);
        BackSentinel.prev = BackSentinel.prev.next;
        size ++;
    }

    public boolean isEmpty() {
        if (FrontSentinel.next == BackSentinel) {
            return true;
        }
        else{
            return false;
        }
    }

    public int size(){
        return size;
    }

    public void printDeque() {
        int i = 0;
        IntNode p = new IntNode(item ,FrontSentinel.next, BackSentinel);
        while (i < size) {
            System.out.print(p.next.item);
            System.out.print(" ");
            p = p.next;
            i++;
        }
    }

    public Item removeFirst() {
       if (FrontSentinel.next == BackSentinel) {
           return null;
       }
       else{
           Item p = FrontSentinel.next.item;
           FrontSentinel.next.next.prev = FrontSentinel;
           FrontSentinel.next = FrontSentinel.next.next;
           size--;
           return p;
       }
    }

    public Item removeLast() {
        if (BackSentinel.prev == FrontSentinel) {
            return null;
        }
        else{
            Item p = BackSentinel.prev.item;
            BackSentinel.prev.prev.next = BackSentinel;
            BackSentinel.prev = BackSentinel.prev.prev;
            size--;
            return p;
        }
    }

    public Item get(int index) {
        if (index == 0) {
            return FrontSentinel.next.item;
        }
        else if (index >= size) {
            return null;
        }
        else {
            int i = 0;
            IntNode p = new IntNode(item,FrontSentinel.next,BackSentinel);
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
        }
        else{
            first = first.next;
            index--;
            return getRecursiveH(first, index);
        }
    }

    public Item getRecursive(int index) {
        if (index >= size) {
            return null;
        }
        return getRecursiveH(FrontSentinel.next, index);
    }

    public LinkedListDeque(){
        FrontSentinel = new IntNode(item, null, null);
        BackSentinel = new IntNode(item, null, null);
        BackSentinel.prev = FrontSentinel;
        FrontSentinel.next = BackSentinel;
        size = 0;
    }

}
