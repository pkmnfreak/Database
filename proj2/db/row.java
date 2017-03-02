package db;
import java.util.ArrayList;
/**
 * Created by noraharhen on 3/1/17.
 */
public class row {
    ArrayList items = new ArrayList();

    public row(Value[] x) {
        this.items = items;
    }

    public Value get(int index) {
        return (Value) items.get(index);
    }

}

