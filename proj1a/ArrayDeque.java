/**
 * Created by pkmnfreak on 2/2/17.
 */
public class ArrayDeque<Bleep> {
    public static void main(String[] args) {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        a.addFirst(5);
        a.addFirst(4);
        a.addFirst(3);
        a.addLast(6);
        a.addLast(7);
        a.addLast(8);
        a.addFirst(2);
        a.addFirst(1);
        a.addFirst(0);
        a.removeFirst();
        a.removeLast();
        a.addFirst(0);
        a.addLast(10);
        a.addLast(11);
    }

    private Bleep[] Array;
    private int NextFirst;
    private int NextLast;
    private int size;

    public ArrayDeque(){
        Array = (Bleep[]) new Object[8];
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
            Bleep[] temp = (Bleep[]) new Object[size+1];
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

    public void addFirst(Bleep Item){
        resize();
        Array[NextFirst] = Item;
        findNextFirst();
        size++;
    }

    public void addLast(Bleep Item){
        resize();
        Array[NextLast] = Item;
        findNextLast();
        size++;
    }

    public boolean isEmpty(){
        int index = 0;
        while (index < Array.length){
            if (Array[index] != null){
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
    public Bleep removeFirst(){
        if (size == 0){
            return null;
        }
        int index = NextFirst+1;
        Bleep f = Array[index];
        while (index < Array.length-1){
            Array[index] = Array[index+1];
            index++;
        }
        Bleep[] temp = (Bleep[]) new Object[Array.length-1];
        System.arraycopy(Array,0,temp,0,Array.length-1);
        Array = temp;
        size--;
        NextFirst--;
        return f;
    }

    /* GENERALIZE */
    public Bleep removeLast(){
        if (size == 0){
            return null;
        }
        int index = NextLast-1;
        Bleep f = Array[index];
        while (index < Array.length-1){
            Array[index] = Array[index+1];
            index++;
        }
        Bleep[] temp = (Bleep[]) new Object[Array.length-1];
        System.arraycopy(Array,0,temp,0,Array.length-1);
        Array = temp;
        size--;
        NextLast--;
        return f;
    }

    public Bleep get(int index){
        return Array[index];
    }

}