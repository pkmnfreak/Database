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

    public String checkType(T i) {
        if (i.getClass().getName() == "java.lang.Integer") {
            return "int";
        } else if (i.getClass().getName() == "java.lang.Float") {
            return "float";
        } else if (i.getClass().getName() == "java.lang.String") {
            return "string";
        } else {
            return i.getClass().getName();
        }
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


    public boolean greaterThan(Value value1, Value value2) {
        return (compare(value1, value2) > 0);
    }

    public boolean lessThan(Value value1, Value value2) {
        return (compare(value1, value2) < 0);
    }

    public boolean greaterThanOrEqualTo(Value value1, Value value2) {
        return (compare(value1, value2) >= 0);
    }

    public boolean lessThanOrEqualTo(Value value1, Value value2) {
        return (compare(value1, value2) <= 0);
    }

    public boolean equalTo(Value value1, Value value2) {
        return (compare(value1, value2) == 0);
    }

    public boolean notEqualTo(Value value1, Value value2) {
        return (compare(value1, value2) != 0);
    }


    public Float compare(Value x, Value y) {
        if (x.value instanceof Integer && y.value instanceof Integer) {
            return (float) ((Integer) x.value - (Integer) y.value);
        } else if (x.value instanceof Integer && y.value instanceof Float) {
            return (Integer) x.value - (Float) y.value;
        } else if (x.value instanceof Float && y.value instanceof Integer) {
            return (Float) x.value - (Integer) y.value;
        } else if (x.value instanceof Float && y.value instanceof Float) {
            return (Float) x.value - (Float) y.value;
        } else if (x.value.getClass().getName() != y.value.getClass().getName()) {
            System.out.print("ERROR: You're comparing two different types!");
            return  Float.valueOf("ERROR: You're comparing two different types!");
        } else {
            return (float) ((x.toString()).charAt(1) - (y.toString()).charAt(1));
        }
    }

    public Value addValue(Value x, Value y) {
        if (x.value instanceof Integer && y.value instanceof Integer) {
            return new Value(((Integer) x.value + (Integer) y.value));
        } else if (x.value instanceof Integer && y.value instanceof Float) {
            return new Value((Integer) x.value + (Float) y.value);
        } else if (x.value instanceof Float && y.value instanceof Integer) {
            return new Value((Float) x.value + (Integer) y.value);
        } else if (x.value instanceof Float && y.value instanceof Float) {
            return new Value((Float) x.value + (Float) y.value);
        } else if (x.value.getClass().getName() != y.value.getClass().getName()) {
            System.out.print("You're adding two different types!");
            return new Value();
        } else {
            return new Value(((String) x.value) + ((String) y.value));
        }
    }

    public Value minusValue(Value x, Value y) {
        if (x.value instanceof Integer && y.value instanceof Integer) {
            return new Value(((Integer) x.value - (Integer) y.value));
        } else if (x.value instanceof Integer && y.value instanceof Float) {
            return new Value((Integer) x.value - (Float) y.value);
        } else if (x.value instanceof Float && y.value instanceof Integer) {
            return new Value((Float) x.value - (Integer) y.value);
        } else if (x.value instanceof Float && y.value instanceof Float) {
            return new Value((Float) x.value - (Float) y.value);
        } else {
            System.out.print("Input error both aren't numbers");
            return new Value("Error: Input error both aren't numbers");
        }
    }

    public Value multiplyValue(Value x, Value y) {
        if (x.value instanceof Integer && y.value instanceof Integer) {
            return new Value(((Integer) x.value * (Integer) y.value));
        } else if (x.value instanceof Integer && y.value instanceof Float) {
            return new Value((Integer) x.value * (Float) y.value);
        } else if (x.value instanceof Float && y.value instanceof Integer) {
            return new Value((Float) x.value * (Integer) y.value);
        } else if (x.value instanceof Float && y.value instanceof Float) {
            return new Value((Float) x.value * (Float) y.value);
        } else {
            System.out.print("Input error both aren't numbers");
            return new Value("Error: Input error both aren't numbers");
        }
    }

    public Value divideValue(Value x, Value y) {
        if (x.value instanceof Integer && y.value instanceof Integer) {
            return new Value(((Integer) x.value / (Integer) y.value));
        } else if (x.value instanceof Integer && y.value instanceof Float) {
            return new Value((Integer) x.value / (Float) y.value);
        } else if (x.value instanceof Float && y.value instanceof Integer) {
            return new Value((Float) x.value / (Integer) y.value);
        } else if (x.value instanceof Float && y.value instanceof Float) {
            return new Value((Float) x.value / (Float) y.value);
        } else {
            System.out.print("Input error both aren't numbers");
            return new Value("Error: Input error both aren't numbers");
        }
    }

    public Value createValue(String i) {
        try {
            Integer.parseInt(i);
            return new Value(Integer.parseInt(i));
        } catch (NumberFormatException e) {
            try {
                Float.parseFloat(i);
                return new Value(Float.parseFloat(i));
            } catch (NumberFormatException f) {
                return new Value(i);
            }
        }
    }
}
