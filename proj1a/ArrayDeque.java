
public class ArrayDeque<Item> {

    public static void main(String[] args) {
        ArrayDeque<Integer> ArrayDeque = new ArrayDeque<>();
        ArrayDeque.size();
        ArrayDeque.addLast(1);
        ArrayDeque.size();
        ArrayDeque.removeFirst();
        ArrayDeque.addLast(4);
        ArrayDeque.addLast(5);
        ArrayDeque.removeLast();
        ArrayDeque.addLast(7);
        ArrayDeque.addFirst(8);
        ArrayDeque.addLast(9);
        ArrayDeque.addLast(10);
        ArrayDeque.addFirst(11);
    }

    private Item[] array;
    private int nextFirst;
    private int lastFirst;
    private int nextLast;
    private int lastLast;
    private int size;

    public ArrayDeque() {
        array = (Item[]) new Object[8];
        nextFirst = 0;
        nextLast = 1;
        lastFirst = nextFirst;
        lastLast = nextLast;
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

    private void resize(){
        if (size <= array.length) {
            return;
        }
        if (lastFirst != 0) {
            Item[] temp = (Item[]) new Object[size * 2];
            System.arraycopy(array, 0, temp, 0, lastFirst);
            System.arraycopy(array, lastFirst, temp, temp.length - (array.length - lastFirst), array.length - lastFirst);
            nextFirst = temp.length - (array.length - lastFirst) - 1;
            array = temp;
        }
        else if (nextLast == 0) {
            Item[] temp = (Item[]) new Object[size * 2];
            System.arraycopy(array, 0, temp, temp.length - array.length, array.length);
            nextFirst = temp.length - array.length - 1;
            array = temp;
        }
        else {
            Item[] temp = (Item[]) new Object[size * 2];
            System.arraycopy(array, 0, temp, 0, nextLast);
            System.arraycopy(array, nextLast, temp, temp.length - (array.length - nextLast), array.length - nextLast);
            nextFirst = temp.length - (array.length);
            array = temp;
        }
    }

    private void findNextFirst() {
        lastFirst = nextFirst;
        if (nextFirst - 1 < 0 && array[array.length - 1] == null) {
            nextFirst = array.length - 1;
        }
        else if (nextFirst - 1 < 0 && array[array.length - 1] != null) {
            nextFirst = 0;
        }
        else {
            nextFirst--;
        }
    }

    private void findNextLast() {
        lastLast = nextLast;
        if (nextLast + 1 > array.length - 1 && array[0] == null) {
            nextLast = 0;
        }
        else {
            nextLast++;
        }
    }

    public void addFirst(Item Item) {
        size++;
        resize();
        if (array[nextFirst] != null) {
            int index = nextFirst;
            while (array[index] != null) {
                index++;
            }
            while (index > nextFirst) {
                array[index] = array[index - 1];
                index--;
            }
            nextLast++;
        }
        array[nextFirst] = Item;
        findNextFirst();
    }


    public void addLast(Item Item) {
        size++;
        resize();
        if (array[nextLast] != null) {
            int index = nextLast;
            while (array[index] != null) {
                index++;
            }
            while (index > nextLast) {
                array[index] = array[index - 1];
                index--;
            }
            nextFirst++;
        }
        array[nextLast] = Item;
        findNextLast();
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

    public int size() {
        return size;
    }

    public void printDeque() {
        if (isEmpty()) {
            return;
        }
        if (nextFirst != 0 && lastFirst != 0) {
            int i = lastFirst;
            while (i + 1 <= array.length) {
                System.out.print(array[i]);
                System.out.print(" ");
                i++;
            }
            i = 0;
            while (array[i] != null) {
                System.out.print(array[i]);
                System.out.print(" ");
                i++;
            }
            return;
        }
        else {
            int i = nextFirst + 1;
            while (i + 1 <= array.length) {
                System.out.print(array[i]);
                System.out.print(" ");
                i++;
            }
            i = 0;
            while (array[i] != null) {
                System.out.print(array[i]);
                System.out.print(" ");
                i++;
            }
        }
    }

    public Item removeFirst() {
        if (size == 0) {
            return null;
        }
        size--;
        /* If all addLast and no resizing */
        if (nextFirst == lastFirst) {
            int i = nextLast;
                while (array[i] == null) {
                    if (i + 1 >= array.length) {
                        i = 0;
                    } else {
                        i++;
                    }
                }
            Item f = array[i];
            Item[] temp = (Item[]) new Object[array.length - 1];
            System.arraycopy(array, 0, temp, 0, i);
            System.arraycopy(array, i + 1, temp, i, temp.length - 1);
            array = temp;
            return f;
        }
        else if (lastFirst != array.length - 1 && nextFirst != array.length - 1){
            Item f = array[lastFirst];
            Item[] temp = (Item[]) new Object[array.length - 1];
            System.arraycopy(array, 0, temp, 0, lastFirst);
            System.arraycopy(array, lastFirst + 1, temp, lastFirst, array.length - nextFirst - 2);
            array = temp;
            return f;
        }
        Item f = array[lastFirst];
        Item[] temp = (Item[]) new Object[array.length - 1];
        System.arraycopy(array, 0, temp, 0, lastFirst);
        array = temp;
        return f;
    }

    public Item removeLast() {
        if (isEmpty()) {
            return null;
        }
        size--;
        /* If all addFirst and no resizing */
        if (nextLast == lastLast ) {
            Item f = array[0];
            Item[] temp = (Item[]) new Object[array.length - 1];
            if (!isFull()) {
                if (f == null) {
                    f = array[array.length - 1];
                    System.arraycopy(array, 0, temp, 0, array.length - 1);
                }
                else {
                    System.arraycopy(array, 1, temp, 0, array.length - 1);
                }
            }
            else {
                f = array[lastLast];
                System.arraycopy(array, 0, temp, 0, lastLast);
                System.arraycopy(array, lastLast + 1, temp, lastLast, array.length - 2);
            }
            array = temp;
            findNextFirst();
            nextLast--;
            lastLast--;
            return f;
        }
        else if (lastLast != array.length - 1){
            Item f = array[lastLast];
            Item[] temp = (Item[]) new Object[array.length - 1];
            System.arraycopy(array, 0, temp, 0, lastLast);
            System.arraycopy(array, lastLast + 1, temp, lastLast, array.length - lastLast - 1);
            array = temp;
            if (lastLast - 1 < 0) {
                lastLast = array.length - 1;
            }
            nextLast--;
            return f;
        }
        Item f = array[array.length - 1];
        Item[] temp = (Item[]) new Object[array.length - 1];
        System.arraycopy(array, 0, temp, 0, lastLast);
        array = temp;
        findNextFirst();
        nextLast--;
        lastLast--;
        return f;
    }

    public Item get(int index) {
        if (index >= array.length || index < 0) {
            return null;
        }
        Item[] orderedArray = (Item[]) new Object[array.length];
        int j = 0;
        if (nextFirst != 0 && lastFirst != 0) {
            int i = lastFirst;
            while (i + 1 <= array.length) {
                orderedArray[j] = array[i];
                i++;
                j++;
            }
            i = 0;
            while (array[i] != null) {
                orderedArray[j] = array[i];
                i++;
                j++;
            }
            return orderedArray[index];
        }
        else {
            int i = nextFirst + 1;
            while (i + 1 <= array.length) {
                orderedArray[j] = array[i];
                i++;
                j++;
            }
            i = 0;
            while (array[i] != null) {
                orderedArray[j] = array[i];
                i++;
                j++;
            }
            return orderedArray[index];
        }
    }

}