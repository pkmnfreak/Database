public interface Deque<Item> {

    public void addFirst(Item x);

    public void addLast(Item y);

    public Item removeFirst();

    public Item removeLast();

    public Item get(int i);

    public int size();

    public void printDeque();

    public boolean isEmpty();
}