package db;
import java.util.ArrayList;
/**
 * Created by noraharhen on 3/1/17.
 */
public class row<T> {
    ArrayList items = new ArrayList();

    public row(Object[] x) {
        this.items = items;
    }

    public T get(int index) {
        return (T) items.get(index);
    }

}

