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
        if (x.getValue() instanceof Integer && y.getValue() instanceof Integer) {
            return (float) ((Integer) x.getValue() - (Integer) y.getValue());
        } else if (x.getValue() instanceof Integer && y.getValue() instanceof Float) {
            return (Integer) x.getValue() - (Float) y.getValue();
        } else if (x.getValue() instanceof Float && y.getValue() instanceof Integer) {
            return (Float) x.getValue() - (Integer) y.getValue();
        } else if (x.getValue() instanceof Float && y.getValue() instanceof Float) {
            return (Float) x.getValue() - (Float) y.getValue();
        } else if (x.getValue().getClass().getName() != y.getValue().getClass().getName()) {
            throw new Error();
        } else {
            return (float) ((x.toString()).charAt(1) - (y.toString()).charAt(1));
        }
    }

    public Value addValue(Value x, Value y) {
        if (x.getValue() instanceof Integer && y.getValue() instanceof Integer) {
            return new Value(((Integer) x.getValue() + (Integer) y.getValue()));
        } else if (x.getValue() instanceof Integer && y.getValue() instanceof Float) {
            return new Value((Integer) x.getValue() + (Float) y.getValue());
        } else if (x.getValue() instanceof Float && y.getValue() instanceof Integer) {
            return new Value((Float) x.getValue() + (Integer) y.getValue());
        } else if (x.getValue() instanceof Float && y.getValue() instanceof Float) {
            return new Value((Float) x.getValue() + (Float) y.getValue());
        } else if (x.getValue().getClass().getName() != y.getValue().getClass().getName()) {
            throw new Error();
        } else if (x.getValue().getClass().getName() instanceof String && x.getClass().getName() instanceof String) {
            Value tempVal = new Value(((String) x.getValue()) + ((String) y.getValue()));
            tempVal.value = ((String) tempVal.getValue()).replaceAll("''", "");
            tempVal.label = ((String) tempVal.getValue()).replaceAll("''", "");
            return tempVal;
        } else {
            return new Value(((String) x.getValue()) + ((String) y.getValue()));
        }
    }

    public Value minusValue(Value x, Value y) {
        if (x.getValue() instanceof Integer && y.getValue() instanceof Integer) {
            return new Value(((Integer) x.getValue() - (Integer) y.getValue()));
        } else if (x.getValue() instanceof Integer && y.getValue() instanceof Float) {
            return new Value((Integer) x.getValue() - (Float) y.getValue());
        } else if (x.getValue() instanceof Float && y.getValue() instanceof Integer) {
            return new Value((Float) x.getValue() - (Integer) y.getValue());
        } else if (x.getValue() instanceof Float && y.getValue() instanceof Float) {
            return new Value((Float) x.getValue() - (Float) y.getValue());
        } else {
            throw new Error();
        }
    }

    public Value multiplyValue(Value x, Value y) {
        if (x.getValue() instanceof Integer && y.getValue() instanceof Integer) {
            return new Value(((Integer) x.getValue() * (Integer) y.getValue()));
        } else if (x.getValue() instanceof Integer && y.getValue() instanceof Float) {
            return new Value((Integer) x.getValue() * (Float) y.getValue());
        } else if (x.getValue() instanceof Float && y.getValue() instanceof Integer) {
            return new Value((Float) x.getValue() * (Integer) y.getValue());
        } else if (x.getValue() instanceof Float && y.getValue() instanceof Float) {
            return new Value((Float) x.getValue() * (Float) y.getValue());
        } else {
            System.out.println("Error: invalid operation");
            throw new Error();
        }
    }

    public Value divideValue(Value x, Value y) {
        if (x.getValue() instanceof Integer && y.getValue() instanceof Integer) {
            return new Value(((Integer) x.getValue() / (Integer) y.getValue()));
        } else if (x.getValue() instanceof Integer && y.getValue() instanceof Float) {
            return new Value((Integer) x.getValue() / (Float) y.getValue());
        } else if (x.getValue() instanceof Float && y.getValue() instanceof Integer) {
            return new Value((Float) x.getValue() / (Integer) y.getValue());
        } else if (x.getValue() instanceof Float && y.getValue() instanceof Float) {
            return new Value((Float) x.getValue() / (Float) y.getValue());
        } else {
            throw new Error();
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
