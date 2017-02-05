
public class ArrayDeque<Item> {

    public static void main(String[] args) {
        ArrayDeque<Integer> ArrayDeque = new ArrayDeque<>();
        ArrayDeque.addLast(0);
        ArrayDeque.addLast(1);
        ArrayDeque.addLast(2);
        ArrayDeque.addLast(3);
        ArrayDeque.removeFirst();
        ArrayDeque.addLast(4);
        ArrayDeque.addLast(5);
        ArrayDeque.addLast(6);
        ArrayDeque.addLast(7);
        ArrayDeque.addLast(8);
        ArrayDeque.addFirst(9);
        ArrayDeque.addFirst(10);
        ArrayDeque.addLast(11);
        ArrayDeque.addLast(12);
        ArrayDeque.addLast(13);
        ArrayDeque.addFirst(14);
        ArrayDeque.addFirst(15);
        ArrayDeque.addFirst(16);
        ArrayDeque.addFirst(17);
        ArrayDeque.addFirst(18);
        ArrayDeque.addLast(19);
        ArrayDeque.addLast(20);
    }

    private Item[] array;
    private int nextFirst;
    private int nextLast;
    private int size;

    public ArrayDeque() {
        array = (Item[]) new Object[8];
        nextFirst = 4;
        nextLast = 4;
        size = 0;
    }

    private boolean isFull() {
        int i = 0;
        while (i < array.length - 1) {
            if (array[i] == null) {
                return false;
            }
            i++;
        }
        return true;
    }

    public boolean isEmpty() {
        int index = 0;
        while (index < array.length) {
            if (array[index] != null) {
                return false;
            }
            index++;
        }
        return true;
    }

    private void resize(int i) {
        if (size < array.length) {
            return;
        }
        if (array.length < 16) {
            return;
        }
        Item[] temp = (Item[]) new Object[i];
        int pointer = nextFirst + 1;
        for (int j = 0; j < array.length; j++) {
            temp[j] = array[pointer];
            pointer = (pointer + 1) % array.length;
        }
        nextFirst = temp.length;
        nextLast = size - 1;
        array = temp;
    }

    public int size() {
        return size;
    }

    public void addFirst(Item item) {
        if (size >= array.length) {
            resize(array.length * 2);
        }
        if (isEmpty()) {
            array[nextFirst] = item;
        }
        else {
            nextFirst = (nextFirst - 1 + array.length)  % array.length;
            array[nextFirst] = item;
        }
        size++;
    }

    public void addLast(Item item) {
        if (size >= array.length) {
            resize(array.length * 2);
        }
        nextLast = (nextLast + 1) % array.length;
        array[nextLast] = item;
        size++;
    }

    public Item removeFirst() {
        if (isEmpty()) {
            return null;
        }
        Item f = array[nextFirst + 1];
        array[nextFirst + 1] = null;
        nextFirst = (nextFirst + 1) % array.length;
        size--;
        if (size <= array.length/4) {
            resize(array.length/2);
        }
        return f;
    }

    public Item removeLast() {
        if (isEmpty()) {
            return null;
        }
        Item f = array[nextLast - 1];
        array[nextLast - 1] = null;
        size--;
        nextLast = (nextLast - 1 + array.length) % array.length;
        if (size <= array.length/4) {
            resize(array.length/2);
        }
        return f;
    }

    public void printDeque() {
        Item[] temp = (Item[]) new Object[size];
        int pointer = nextFirst + 1;
        for (int j = 0; j < array.length; j++) {
            temp[j] = array[pointer];
            pointer = (pointer + 1) % array.length;
        }
        for (int h = 0; h < temp.length; h++) {
            System.out.print(temp[h]);
            System.out.print(" ");
        }
    }

    public Item get(int index) {
        Item[] temp = (Item[]) new Object[size];
        int pointer = nextFirst + 1;
        for (int j = 0; j < array.length; j++) {
            temp[j] = array[pointer];
            pointer = (pointer + 1) % array.length;
        }
        return temp[index];
    }
}