public class LinkedListDeque<Bloop> {

    public static void main(String[] args) {
        LinkedListDeque<String> L = new LinkedListDeque<>();
        L.addFirst("hello");
        L.addFirst("my");
        L.addFirst("name");
        System.out.print(L.getRecursive(1));
    }

    public class IntNode {
        public Bloop item;
        public IntNode next;
        public IntNode prev;
        public IntNode(Bloop i, IntNode n, IntNode p) {
            item = i;
            next = n;
            prev = p;
        }
    }

    private IntNode FrontSentinel;
    private IntNode BackSentinel;
    private int size;
    private Bloop Item;

    public void addFirst(Bloop Item){
        FrontSentinel.next.prev = new IntNode(Item, FrontSentinel.next, FrontSentinel);
        FrontSentinel.next = FrontSentinel.next.prev;
        size ++;
    }

    public void addLast(Bloop Item){
        BackSentinel.prev.next = new IntNode(Item, BackSentinel, BackSentinel.prev);
        BackSentinel.prev = BackSentinel.prev.next;
        size ++;
    }

    public boolean isEmpty(){
        if (FrontSentinel.next == BackSentinel){
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
        IntNode p = new IntNode(Item,FrontSentinel.next,BackSentinel);
        while (i < size) {
            System.out.print(p.next.item);
            System.out.print(" ");
            p = p.next;
            i++;
        }
    }

    public Bloop removeFirst(){
       if (FrontSentinel.next == BackSentinel){
           return null;
       }
       else{
           Bloop p = FrontSentinel.next.item;
           FrontSentinel.next.next.prev = FrontSentinel;
           FrontSentinel.next = FrontSentinel.next.next;
           size--;
           return p;
       }
    }

    public Bloop removeLast(){
        if (BackSentinel.prev == FrontSentinel){
            return null;
        }
        else{
            Bloop p = BackSentinel.prev.item;
            BackSentinel.prev.prev.next = BackSentinel;
            BackSentinel.prev = BackSentinel.prev.prev;
            size--;
            return p;
        }
    }

    public Bloop get(int index){
        if (index == 0){
            return FrontSentinel.next.item;
        }
        else if (index >= size){
            return null;
        }
        else{
            int i = 0;
            IntNode p = new IntNode(Item,FrontSentinel.next,BackSentinel);
            while (i <= index){
                p = p.next;
                i++;
            }
            return p.item;
        }
    }

    private Bloop getRecursiveH(IntNode first, int index){
        if (first.next == null || index == 0){
            return first.item;
        }
        else{
            first = first.next;
            index--;
            return getRecursiveH(first, index);
        }
    }

    public Bloop getRecursive(int index){
        if (index >= size){
            return null;
        }
        return getRecursiveH(FrontSentinel.next, index);
    }

    public LinkedListDeque(){
        FrontSentinel = new IntNode(Item, null, null);
        BackSentinel = new IntNode(Item, null, null);
        BackSentinel.prev = FrontSentinel;
        FrontSentinel.next = BackSentinel;
        size = 0;
    }

}