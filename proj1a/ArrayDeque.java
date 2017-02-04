
public class ArrayDeque<Item> {

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
        /*If all is addFirst and first box was removed (nextLast = 1 even tho it should be 0)*/
        else if (nextLast == 0 || nextFirst < nextLast) {
            Item[] temp = (Item[]) new Object[size * 2];
            System.arraycopy(array, 0, temp, temp.length - array.length, array.length);
            nextFirst = temp.length - array.length - 1;
            array = temp;
            if (array[0] == null) {
                nextLast = 0;
            }
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
        if (nextFirst == lastFirst) {
            Item f = array[nextFirst + 1];
            Item[] temp = (Item[]) new Object[array.length - 1];
            System.arraycopy(array, 0, temp, 0, nextFirst + 1);
            System.arraycopy(array, nextFirst + 2, temp, nextFirst + 1, array.length - nextFirst - 2);
            array = temp;
            if (!isFull() && nextLast != 0) {
                nextLast--;
                lastLast--;
            }
            return f;
        }
        else if (lastFirst == array.length) {
            lastFirst = 0;
            Item f = array[lastFirst];
            Item[] temp = (Item[]) new Object[array.length - 1];
            System.arraycopy(array, 0, temp, 0, lastFirst);
            array = temp;
            return f;
        }
        else if (lastFirst + 1 == array.length) {
            Item f = array[lastFirst];
            Item[] temp = (Item[]) new Object[array.length - 1];
            System.arraycopy(array, 0, temp, 0, lastFirst);
            array = temp;
            return f;
        }
        else {
            Item f = array[lastFirst];
            Item[] temp = (Item[]) new Object[array.length - 1];
            System.arraycopy(array, 0, temp, 0, lastFirst);
            System.arraycopy(array, lastFirst + 1, temp, lastFirst, array.length - lastFirst - 1);
            array = temp;
            return f;
        }
    }

    public Item removeLast() {
        if (isEmpty()) {
            return null;
        }
        size--;
        if (nextLast == lastLast) {
            Item[] temp = (Item[]) new Object[array.length - 1];
            /*array is addfirst with 0 as null*/
            /*array is all addfirst, no null*/
            if (lastFirst == 0 || array[0] == null){
                Item f = array[array.length - 1];
                System.arraycopy(array, 0, temp, 0, array.length - 1);
                array = temp;
                return f;
            }
            /*array is all addfirst*/
            else {
                Item f = array[0];
                System.arraycopy(array, 1, temp, 0, array.length - 1);
                array = temp;
                nextFirst--;
                lastFirst--;
                nextLast--;
                lastLast--;
                if (lastFirst < 0) {
                    lastFirst = 0;
                }
                return f;
            }
        }
        else {
            if (lastLast < 0 || lastLast >= array.length) {
                lastLast = array.length - 1;
            }
            Item f = array[lastLast];
            Item[] temp = (Item[]) new Object[array.length - 1];
            System.arraycopy(array, 0, temp, 0, lastLast);
            if (lastLast != array.length - 1) {
                System.arraycopy(array, lastLast + 1, temp, lastLast, array.length - lastLast - 1);
            }
            array = temp;
            if (nextLast != 0) {
                nextLast--;
                lastLast--;
            }
            if (nextFirst >= array.length) {
                nextFirst--;
                lastFirst = 0;
            }
            return f;
        }
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