package db;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.math.BigDecimal;
/**
 * Created by noraharhen on 2/24/17.
 */

/** delegation of db.column class to an arraylist **/
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
    public column(String type) {
        this.items = items;
    }

    /** adds item to end of db.column **/
    public boolean add(T x) {
        items.add(x);
        return true;
    }

    public boolean checksameType(T item) {
        String type = this.get(0).getClass().getName();
        return (item.getClass().getName() == type);
    }

    public int size() {
        return items.size();
    }

    public T get(int index) {
        return items.get(index);
    }

    public int compare(Value x, Value y) {
        if (x.value instanceof Number && y.value instanceof Number) {
            return new BigDecimal(x.value.toString()).compareTo(new BigDecimal(y.value.toString()));
        } else if (x.value.getClass().getName() != y.value.getClass().getName()) {
            System.out.print("You're comparing two different types!");
            return 1204;
        } else {
            return ((String) x.value).length() - ((String) y.value).length();
        }
    }

    public int compare(Value x, T i) {
        if (x.value instanceof Number && i instanceof Number) {
            return new BigDecimal(x.value.toString()).compareTo(new BigDecimal(i.toString()));
        } else if (x.value.getClass().getName() != i.getClass().getName()) {
            System.out.print("You're comparing two different types!");
            return 1204;
        } else {
            return ((String) x.value).length() - ((String) i).length();
        }
    }

    public Value addValue(Value x, Value y) {
        if (x.value instanceof Number && y.value instanceof Number) {
            return new Value(new BigDecimal(x.value.toString()).add(new BigDecimal(y.value.toString())));
        } else if (x.value.getClass().getName() != y.value.getClass().getName()) {
            System.out.print("You're adding two different types!");
            return new Value();
        } else {
            return new Value(((String) x.value) + ((String) y.value));
        }
    }

    public Value minusValue(Value x, Value y) {
        if (x.value instanceof Number && y.value instanceof Number) {
            return new Value(new BigDecimal(x.value.toString()).subtract(new BigDecimal(y.value.toString())));
        } else {
            System.out.print("Input error both aren't numbers");
            return new Value();
        }
    }

    public Value multiplyValue(Value x, Value y) {
        if (x.value instanceof Number && y.value instanceof Number) {
            return new Value(new BigDecimal(x.value.toString()).multiply(new BigDecimal(y.value.toString())));
        } else {
            System.out.print("Input error both aren't numbers");
            return new Value();
        }
    }

    public Value divideValue(Value x, Value y) {
        if (x.value instanceof Number && y.value instanceof Number) {
            return new Value(new BigDecimal(x.value.toString()).divide(new BigDecimal(y.value.toString())));
        } else {
            System.out.print("Input error both aren't numbers");
            return new Value();
        }
    }

    public Value createValue(String i) {
        try {
            Integer.parseInt(i);
            return new Value(Integer.parseInt(i));
        } catch (NumberFormatException e) {
        }
        try {
            Float.parseFloat(i);
            return new Value(Float.parseFloat(i));
        } catch(NumberFormatException e) {
            return new Value(i);
        }
    }
}
