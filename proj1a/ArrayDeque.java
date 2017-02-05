
public class ArrayDeque<Item> {

    private Item[] array;
    private int nextFirst;
    private int nextLast;
    private int size;

   public ArrayDeque() {
        array = (Item[]) new Object[8];
        nextFirst = 4;
        nextLast = 5;
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
        return size == 0;
    }

    private void resize(int i) {
        Item[] temp = (Item[]) new Object[i];
        int pointer = (nextFirst + 1) % array.length;
        for (int j = 0; j < size; j++) {
            temp[j] = array[pointer];
            pointer = (pointer + 1) % array.length;
        }
        nextFirst = temp.length - 1;
        nextLast = size;
        array = temp;
    }

    public int size() {
        return size;
    }

    public void addFirst(Item item) {
        if (size >= array.length) {
            resize(array.length * 2);
        }
        array[nextFirst] = item;
        nextFirst = (nextFirst - 1 + array.length) % array.length;
        size++;
    }

    public void addLast(Item item) {
        if (size >= array.length) {
            resize(array.length * 2);
        }
        array[nextLast] = item;
        nextLast = (nextLast + 1) % array.length;
        size++;
    }

    public Item removeFirst() {
        if (isEmpty()) {
            return null;
        }
        Item f = array[(nextFirst + 1) % array.length];
        array[(nextFirst + 1) % array.length] = null;
        nextFirst = (nextFirst + 1) % array.length;
        size--;
        if (4 * size <= array.length && array.length > 16) {
            resize(array.length / 2);
        }
        return f;
    }

    public Item removeLast() {
        if (isEmpty()) {
            return null;
        }
        Item f = array[(nextLast - 1 + array.length) % array.length];
        array[(nextLast - 1 + array.length) % array.length] = null;
        size--;
        nextLast = (nextLast - 1 + array.length) % array.length;
        if (4 * size <= array.length && array.length > 16) {
            resize(array.length / 2);
        }
        return f;
    }

    public void printDeque() {
        Item[] temp = (Item[]) new Object[size];
        int pointer = (nextFirst + 1) % array.length;
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
        if (index > size) {
            return null;
        }
        Item[] temp = (Item[]) new Object[size];
        int pointer = (nextFirst + 1) % array.length;
        for (int j = 0; j < size; j++) {
            temp[j] = array[pointer];
            pointer = (pointer + 1) % array.length;
        }
        return temp[index];
    }
}
