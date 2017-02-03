/**
 * Created by pkmnfreak on 2/2/17.
 */
public class ArrayDeque {
    public static void main(String[] args) {
        ArrayDeque a = new ArrayDeque();
        a.addFirst(5);
        a.addFirst(4);
        a.addFirst(3);
        a.addLast(6);
        a.addLast(7);
        a.addLast(8);
        a.addFirst(2);
        a.addFirst(1);
        a.addFirst(0);
    }

    private int[] Array;
    private int NextFirst;
    private int NextLast;
    private int size;

    public ArrayDeque(){
        Array = new int[8];
        NextFirst = 0;
        NextLast = 1;
        size = 0;
    }

    public void resize(){
        if (size < Array.length){
            return;
        }
        else{
            NextFirst++;
            int[] temp = new int[size+1];
            System.arraycopy(Array,0, temp, 0, Array.length);
            Array = temp;
            int index = Array.length-1;
            while (index > NextFirst){
                Array[index] = Array[index-1];
                index--;
            }
        }
    }

    public void findNextFirst(){
        if ((NextFirst-1) >= 0){
            NextFirst--;
        }
        else{
            NextFirst = Array.length-1;
        }
    }

    public void findNextLast(){
        if ((NextLast+1) < Array.length){
            NextLast++;
        }
        else{
            NextLast = 0;
        }
    }

    public void addFirst(int Item){
        resize();
        Array[NextFirst] = Item;
        findNextFirst();
        size++;
    }

    public void addLast(int Item){
        resize();
        Array[NextLast] = Item;
        findNextLast();
        size++;
    }

    public boolean isEmpty(){
        int index = 0;
        while (index < Array.length){
            if (Array[index] != 0){
                return false;
            }
            index++;
        }
        return true;
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        int i = 0;
        while (i < size) {
            System.out.print(Array[i]);
            System.out.print(" ");
            i++;
        }
    }

    /* GNEREALIZE*/
    public int removeFirst(){
        if (size == 0){
            return 0;
        }
        return 0;
    }
}