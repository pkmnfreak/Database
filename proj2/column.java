import java.util.ArrayList;
/**
 * Created by noraharhen on 2/24/17.
 */

/** delegation of column class to an arraylist **/
public class column<T> {

    private ArrayList<T> items = new ArrayList<>();

    public column() {
        this.items = items;
    }

    /*copies column2 into column1. Assume column1 is empty*/
    public static void copy(column column1, column column2) {
        for (int i = 0; i < column2.items.size(); i++) {
            column1.add(column2.items.get(i));
        }
    }

    /** adds item to end of column **/
    public boolean add(T x) {
        items.add(x);
        return true;
    }

    public int size() {
        return items.size();
    }

    public T get(int index) {
        return items.get(index);
    }

}
