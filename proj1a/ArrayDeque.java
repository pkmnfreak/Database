/**
 * Created by pkmnfreak on 2/2/17.
 */
public class ArrayDeque<Item> {

    private Item[] Array;
    private int NextFirst;
    private int NextLast;
    private int size;

    public ArrayDeque() {
        Array = (Item[]) new Object[8];
        NextFirst = 0;
        NextLast = 0;
        size = 0;
    }

    private void resize() {
        if (size < Array.length) {
            return;
        }
        else{
            NextFirst++;
            Item[] temp = (Item[]) new Object[size + 1];
            System.arraycopy(Array , 0 ,  temp ,  0 ,  Array.length);
            Array = temp;
            int index = Array.length - 1;
            while (index > NextFirst) {
                Array[index] = Array[index - 1];
                index--;
            }
        }
    }

    private void findNextFirst() {
        if ((NextFirst - 1) >= 0) {
            NextFirst--;
        }
        else{
            NextFirst = Array.length - 1;
        }
    }

    private void findNextLast() {
        if ((NextLast + 1) < Array.length) {
            NextLast++;
        }
        else{
            NextLast = 0;
        }
    }

    public void addFirst(Item Item) {
        resize();
        Array[NextFirst] = Item;
        findNextFirst();
        size++;
    }

    public void addLast(Item Item) {
        resize();
        Array[NextLast] = Item;
        findNextLast();
        size++;
    }

    public boolean isEmpty() {
        int index = 0;
        while (index < Array.length) {
            if (Array[index] != null) {
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
        int i = 0;
        while (i < size) {
            System.out.print(Array[i]);
            System.out.print(" ");
            i++;
        }
    }

    /* GNEREALIZE*/
    public Item removeFirst() {
        if (size == 0) {
            return null;
        }
        int index = NextFirst + 1;
        Item f = Array[index];
        while (index < Array.length - 1) {
            Array[index] = Array[index + 1];
            index++;
        }
        Item[] temp = (Item[]) new Object[Array.length - 1];
        System.arraycopy(Array , 0 , temp , 0 , Array.length - 1);
        Array = temp;
        size--;
        NextFirst--;
        return f;
    }

    /* GENERALIZE */
    public Item removeLast() {
        if (size == 0) {
            return null;
        }
        int index = NextLast - 1;
        Item f = Array[index];
        while (index < Array.length - 1) {
            Array[index] = Array[index + 1];
            index++;
        }
        Item[] temp = (Item[]) new Object[Array.length - 1];
        System.arraycopy(Array , 0 , temp , 0 , Array.length - 1);
        Array = temp;
        size--;
        NextLast--;
        return f;
    }

    public Item get(int index) {
        return Array[index];
    }

}