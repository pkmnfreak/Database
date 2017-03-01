package db;

import java.util.ArrayList;
/**
 * Created by noraharhen on 2/24/17.
 */

/** delegation of db.column class to an arraylist **/
public class column<T> {

    private ArrayList<T> items = new ArrayList<>();

    public column() {
        this.items = items;
    }

    public column(String type) {
        this.items = items;
    }

    /** adds item to end of db.column **/
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
